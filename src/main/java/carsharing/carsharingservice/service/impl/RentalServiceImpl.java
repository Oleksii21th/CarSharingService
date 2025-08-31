package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.rental.AddRentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.dto.rental.ReturnDateDto;
import carsharing.carsharingservice.exception.badrequest.EmptyCarInventoryException;
import carsharing.carsharingservice.exception.badrequest.InvalidRentalDateException;
import carsharing.carsharingservice.exception.badrequest.TwiceReturnedRentalException;
import carsharing.carsharingservice.exception.notfound.CarNotFoundException;
import carsharing.carsharingservice.exception.notfound.RentalNotFoundException;
import carsharing.carsharingservice.mapper.RentalMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.service.RentalService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class RentalServiceImpl implements RentalService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;

    public RentalServiceImpl(RentalRepository rentalRepository,
                             UserRepository userRepository,
                             CarRepository carRepository,
                             RentalMapper rentalMapper) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.rentalMapper = rentalMapper;
    }

    @Override
    public RentalResponseDto save(Long userId, AddRentalRequestDto rentalDto) {
        Long carId = rentalDto.getCarId();
        Car car = carRepository.findById(carId)
                .orElseThrow(() -> new CarNotFoundException(carId));

        if (car.getInventory() == 0) {
            throw new EmptyCarInventoryException(car.getModel());
        }
        car.setInventory(car.getInventory() - 1);

        LocalDate currentDate = LocalDate.now();
        LocalDate rentalDate = LocalDate.parse(rentalDto.getRentalDate(), FORMATTER);
        if (rentalDate.isBefore(currentDate)) {
            throw new InvalidRentalDateException();
        }

        Car savedCar = carRepository.save(car);
        Rental rental = rentalMapper.toModel(rentalDto);
        rental.setUser(userRepository.findById(userId).get());
        rental.setCar(savedCar);

        Rental savedRental = rentalRepository.save(rental);
        return rentalMapper.toDto(savedRental);
    }

    @Override
    public List<RentalResponseDto> findRentalsByUser(RentalSearchParametersDto paramsDto) {
        List<Rental> rentals = rentalRepository.findByUserIdAndIsActive(
                paramsDto.userId(), paramsDto.isActive());

        return rentals.stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalResponseDto findRentalById(Long id) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));

        return rentalMapper.toDto(rental);
    }

    @Override
    public RentalResponseDto returnRental(Long userId, ReturnDateDto returnDateDto) {
        Long rentalId = returnDateDto.rentalId();

        Rental existingRental = rentalRepository.findByUserIdAndId(userId, rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        if (!existingRental.isActive()) {
            throw new TwiceReturnedRentalException();
        }

        existingRental.setActualReturnDate(LocalDate.now());
        existingRental.setActive(false);

        Car car = existingRental.getCar();
        car.setInventory(car.getInventory() + 1);
        carRepository.save(car);

        Rental returnedRental = rentalRepository.save(existingRental);
        return rentalMapper.toDto(returnedRental);
    }
}
