package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.exception.notfound.PaymentNotFoundException;
import carsharing.carsharingservice.exception.notfound.RentalNotFoundException;
import carsharing.carsharingservice.mapper.PaymentMapper;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.model.PaymentType;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.security.AccessManager;
import carsharing.carsharingservice.service.PaymentService;
import carsharing.carsharingservice.service.TelegramNotificationService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PENDING;
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final PaymentType PAYMENT = PaymentType.PAYMENT;
    private static final PaymentType FINE = PaymentType.FINE;
    private static final String STRIPE_RETURN_STATUS_PAID = "paid";

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final AccessManager accessManager;
    private final String stripeSecretKey = System.getenv("STRIPE_SECRET_KEY");
    private final TelegramNotificationService telegramNotificationService;

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              RentalRepository rentalRepository,
                              PaymentMapper paymentMapper,
                              AccessManager accessManager,
                              TelegramNotificationService telegramNotificationService) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.paymentMapper = paymentMapper;
        this.accessManager = accessManager;
        this.telegramNotificationService = telegramNotificationService;
    }

    @Override
    public List<PaymentResponseDto> findAllPayments(Long userId,
                                                    Authentication authentication) {
        if (accessManager.isManager(authentication) && userId == null) {
            return paymentRepository.findAll().stream()
                    .map(paymentMapper::toDto)
                    .toList();
        }

        Long targetUserId = accessManager.resolveUserId(authentication, userId);
        accessManager.checkOwnerOrManager(authentication, targetUserId);

        return paymentRepository.findPaymentsByUserId(targetUserId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponseDto savePaymentSession(PaymentRequestDto requestDto,
                                                 Authentication authentication) {
        Long rentalId = requestDto.rentalId();
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new RentalNotFoundException(rentalId));

        Long userId = rental.getUser() != null ? rental.getUser().getId() : null;
        Long targetUserId = accessManager.resolveUserId(authentication, userId);
        accessManager.checkOwnerOrManager(authentication, targetUserId);

        PaymentType typeOfPayment = PaymentType.valueOf(requestDto.paymentType());

        Optional<Payment> existingPayment = paymentRepository
                .findByRentalIdAndType(rentalId, typeOfPayment)
                .stream()
                .findFirst();

        if (existingPayment.isPresent()
                && existingPayment.get().getStatus() == DEFAULT_PAYMENT_STATUS) {
            return paymentMapper.toDto(existingPayment.get());
        }

        BigDecimal days = typeOfPayment == PAYMENT
                ? BigDecimal.valueOf(getNumberOfDaysRent(rental))
                : BigDecimal.valueOf(getNumberOfFineDays(rental));

        BigDecimal amount = rental.getCar().getDailyFee()
                .multiply(days)
                .multiply(typeOfPayment == FINE ? FINE_MULTIPLIER : BigDecimal.ONE);

        Payment payment = new Payment();
        payment.setRental(rental);
        payment.setType(typeOfPayment);
        payment.setStatus(DEFAULT_PAYMENT_STATUS);
        payment.setAmountToPay(amount);

        String baseUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .build()
                .toUriString();

        String successUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/payments/success")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .toUriString();

        String cancelUrl = UriComponentsBuilder.fromUriString(baseUrl)
                .path("/payments/cancel")
                .queryParam("session_id", "{CHECKOUT_SESSION_ID}")
                .toUriString();

        Stripe.apiKey = stripeSecretKey;
        Session session = createSession(amount, typeOfPayment, successUrl, cancelUrl);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());

        Payment saved = paymentRepository.save(payment);
        PaymentResponseDto responseDto = paymentMapper.toDto(saved);
        responseDto.setDescription("Go to this link to finish payment: " + session.getUrl());
        return responseDto;
    }

    @Override
    public PaymentResponseFullInfoDto updatePaymentStatus(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new PaymentNotFoundException(sessionId));

        Stripe.apiKey = stripeSecretKey;

        try {
            Session session = Session.retrieve(sessionId);
            String status = session.getPaymentStatus();

            if (!STRIPE_RETURN_STATUS_PAID.equals(status)) {
                throw new IllegalStateException("Payment not completed in Stripe");
            }

            payment.setStatus(PaymentStatus.PAID);
            paymentRepository.save(payment);
            telegramNotificationService.sendPaymentSuccessNotification(payment);
        } catch (StripeException exception) {
            throw new RuntimeException("Failed to verify Stripe session", exception);
        }

        return paymentMapper.toFullInfoDto(payment);
    }

    private int getNumberOfDaysRent(Rental rental) {
        LocalDate start = rental.getRentalDate();
        LocalDate end = rental.getActualReturnDate() != null
                ? rental.getActualReturnDate() : LocalDate.now();
        return (int) Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(start, end));
    }

    private int getNumberOfFineDays(Rental rental) {
        LocalDate scheduledReturn = rental.getReturnDate();
        LocalDate actualReturn = rental.getActualReturnDate() != null
                ? rental.getActualReturnDate() : LocalDate.now();
        return (int) Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(
                scheduledReturn, actualReturn));
    }

    private Session createSession(BigDecimal amount,
                                  PaymentType type,
                                  String successUrl,
                                  String cancelUrl) {
        try {
            return Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(successUrl)
                            .setCancelUrl(cancelUrl)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("usd")
                                                            .setUnitAmountDecimal(
                                                                    amount.multiply(
                                                                            BigDecimal.valueOf(100)
                                                                    )
                                                            )
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem
                                                                            .PriceData
                                                                            .ProductData.builder()
                                                                            .setName("Car Rental ("
                                                                                    + type + ")")
                                                                            .build()
                                                            )
                                                            .build()
                                            )
                                            .build()
                            )
                            .build()
            );
        } catch (StripeException e) {
            throw new RuntimeException("Failed to create Stripe session", e);
        }
    }
}
