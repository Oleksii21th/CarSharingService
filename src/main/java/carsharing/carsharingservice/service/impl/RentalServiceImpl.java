package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.rental.RentalDetailsDto;
import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.exception.badrequest.ActivePaymentsException;
import carsharing.carsharingservice.exception.badrequest.EmptyCarInventoryException;
import carsharing.carsharingservice.exception.badrequest.InvalidRentalDateException;
import carsharing.carsharingservice.exception.badrequest.TwiceReturnedRentalException;
import carsharing.carsharingservice.exception.notfound.CarNotFoundException;
import carsharing.carsharingservice.exception.notfound.RentalNotFoundException;
import carsharing.carsharingservice.mapper.RentalMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.security.AccessManager;
import carsharing.carsharingservice.service.RentalService;
import carsharing.carsharingservice.service.TelegramNotificationService;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class RentalServiceImpl implements RentalService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    private final RentalRepository rentalRepository;
    private final UserRepository userRepository;
    private final CarRepository carRepository;
    private final RentalMapper rentalMapper;
    private final PaymentRepository paymentRepository;
    private final TelegramNotificationService telegramNotificationService;
    private final AccessManager accessManager;

    public RentalServiceImpl(RentalRepository rentalRepository,
                             UserRepository userRepository,
                             CarRepository carRepository,
                             RentalMapper rentalMapper,
                             PaymentRepository paymentRepository,
                             TelegramNotificationService telegramNotificationService,
                             AccessManager accessManager) {
        this.rentalRepository = rentalRepository;
        this.userRepository = userRepository;
        this.carRepository = carRepository;
        this.rentalMapper = rentalMapper;
        this.paymentRepository = paymentRepository;
        this.telegramNotificationService = telegramNotificationService;
        this.accessManager = accessManager;
    }

    @Override
    public RentalResponseDto save(Long userId, RentalRequestDto rentalDto) {
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

        List<Payment> pendingPayments =
                paymentRepository.findByUserIdAndStatus(userId, PaymentStatus.PENDING);
        if (!pendingPayments.isEmpty()) {
            throw new ActivePaymentsException(pendingPayments.get(0).getRental().getId());
        }

        Car savedCar = carRepository.save(car);
        Rental rental = rentalMapper.toModel(rentalDto);
        rental.setUser(userRepository.findById(userId).get());
        rental.setCar(savedCar);

        Rental savedRental = rentalRepository.save(rental);
        telegramNotificationService.sendRentalCreatedNotification(savedRental);
        return rentalMapper.toDto(savedRental);
    }

    @Override
    public List<RentalResponseDto> findRentalsByUser(RentalSearchParametersDto paramsDto,
                                                     Authentication authentication) {
        Long targetUserId = accessManager.resolveUserId(authentication, paramsDto.userId());
        accessManager.checkOwnerOrManager(authentication, targetUserId);

        return rentalRepository.findByUserIdAndIsActive(targetUserId, paramsDto.isActive())
                .stream()
                .map(rentalMapper::toDto)
                .toList();
    }

    @Override
    public RentalDetailsDto findRentalById(Long id, Authentication authentication) {
        Rental rental = rentalRepository.findById(id)
                .orElseThrow(() -> new RentalNotFoundException(id));

        Long userId = rental.getUser() != null ? rental.getUser().getId() : null;
        Long targetUserId = accessManager.resolveUserId(authentication, userId);
        accessManager.checkOwnerOrManager(authentication, targetUserId);

        return rentalMapper.toDetailsDto(rental);
    }

    @Override
    public RentalResponseDto returnRental(Long userId, Long rentalId) {
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
