package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.rental.RentalDetailsDto;
import carsharing.carsharingservice.dto.rental.RentalRequestDto;
import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import carsharing.carsharingservice.dto.rental.RentalSearchParametersDto;
import carsharing.carsharingservice.exception.badrequest.ActivePaymentsException;
import carsharing.carsharingservice.exception.badrequest.EmptyCarInventoryException;
import carsharing.carsharingservice.exception.badrequest.InvalidDateException;
import carsharing.carsharingservice.exception.badrequest.TwiceReturnedRentalException;
import carsharing.carsharingservice.exception.notfound.CarNotFoundException;
import carsharing.carsharingservice.exception.notfound.RentalNotFoundException;
import carsharing.carsharingservice.exception.notfound.UserNotFoundException;
import carsharing.carsharingservice.mapper.RentalMapper;
import carsharing.carsharingservice.model.Car;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.model.User;
import carsharing.carsharingservice.repository.CarRepository;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.repository.UserRepository;
import carsharing.carsharingservice.security.AccessManager;
import carsharing.carsharingservice.service.RentalService;
import carsharing.carsharingservice.service.TelegramNotificationService;
import java.time.LocalDate;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class RentalServiceImpl implements RentalService {
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
        LocalDate rentalDate = rentalDto.getRentalDate();
        LocalDate returnDate = rentalDto.getReturnDate();

        validateDates(rentalDate, returnDate, currentDate);

        validateNoPendingPayments(userId);

        Car savedCar = carRepository.save(car);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        Rental rental = rentalMapper.toModel(rentalDto);
        rental.setUser(user);
        rental.setCar(savedCar);

        Rental savedRental = rentalRepository.save(rental);
        telegramNotificationService.sendRentalCreatedNotification(savedRental);
        return rentalMapper.toDto(savedRental);
    }

    private void validateDates(LocalDate rentalDate,
                               LocalDate returnDate,
                               LocalDate currentDate) {

        if (rentalDate.isBefore(currentDate)) {
            throw new InvalidDateException("Rental date cannot be before today");
        }

        if (returnDate.isBefore(rentalDate)) {
            throw new InvalidDateException("Return date cannot be before rental date");
        }

        if (returnDate.isBefore(currentDate)) {
            throw new InvalidDateException("Return date cannot be in the past");
        }
    }

    private void validateNoPendingPayments(Long userId) {
        List<Payment> pendingPayments =
                paymentRepository.findByUserIdAndStatus(userId, PaymentStatus.PENDING);

        if (!pendingPayments.isEmpty()) {
            throw new ActivePaymentsException(
                    pendingPayments.get(0).getRental().getId()
            );
        }
    }

    @Override
    public List<RentalResponseDto> findRentalsByUser(RentalSearchParametersDto paramsDto,
                                                     Authentication authentication) {

        boolean isManager = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MANAGER"));

        Long userId = paramsDto.userId();

        accessManager.checkOwnerOrManager(authentication, userId);

        List<Rental> rentals;

        if (isManager && userId == null) {
            rentals = rentalRepository.findByIsActive(paramsDto.isActive());
        } else {
            Long targetUserId = userId != null
                    ? userId
                    : ((User) authentication.getPrincipal()).getId();

            rentals = rentalRepository.findByUserIdAndIsActive(targetUserId, paramsDto.isActive());
        }

        return rentals.stream()
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
