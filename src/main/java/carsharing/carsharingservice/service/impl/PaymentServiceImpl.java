package carsharing.carsharingservice.service.impl;

import carsharing.carsharingservice.dto.payment.PaymentRequestDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseDto;
import carsharing.carsharingservice.dto.payment.PaymentResponseFullInfoDto;
import carsharing.carsharingservice.exception.notfound.PaymentNotFoundException;
import carsharing.carsharingservice.mapper.PaymentMapper;
import carsharing.carsharingservice.model.Payment;
import carsharing.carsharingservice.model.PaymentStatus;
import carsharing.carsharingservice.model.PaymentType;
import carsharing.carsharingservice.model.Rental;
import carsharing.carsharingservice.repository.PaymentRepository;
import carsharing.carsharingservice.repository.RentalRepository;
import carsharing.carsharingservice.service.PaymentService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import io.github.cdimascio.dotenv.Dotenv;
import jakarta.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {
    private static final PaymentStatus DEFAULT_PAYMENT_STATUS = PaymentStatus.PENDING;
    private static final BigDecimal FINE_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final PaymentType PAYMENT = PaymentType.PAYMENT;
    private static final PaymentType FINE = PaymentType.FINE;
    private static final String SUCCESS_URL =
            "http://localhost:8080/payments/success?session_id={CHECKOUT_SESSION_ID}";
    private static final String CANCEL_URL =
            "http://localhost:8080/payments/cancel?session_id={CHECKOUT_SESSION_ID}";

    private final PaymentRepository paymentRepository;
    private final RentalRepository rentalRepository;
    private final PaymentMapper paymentMapper;
    private final String stripeSecretKey = Dotenv.load().get("STRIPE_SECRET_KEY");

    public PaymentServiceImpl(PaymentRepository paymentRepository,
                              RentalRepository rentalRepository,
                              PaymentMapper paymentMapper) {
        this.paymentRepository = paymentRepository;
        this.rentalRepository = rentalRepository;
        this.paymentMapper = paymentMapper;
    }

    @Override
    public List<PaymentResponseDto> findAllPayments(Long userId) {
        return paymentRepository.findPaymentsByUserId(userId).stream()
                .map(paymentMapper::toDto)
                .toList();
    }

    @Override
    public PaymentResponseDto savePaymentSession(PaymentRequestDto requestDto) {
        Long rentalId = requestDto.rentalId();
        Rental rental = rentalRepository.findById(rentalId)
                .orElseThrow(() -> new PaymentNotFoundException(rentalId));

        PaymentType typeOfPayment = PaymentType.valueOf(requestDto.paymentType());

        Optional<Payment> existingPayment = paymentRepository
                .findByRentalIdAndType(requestDto.rentalId(), typeOfPayment)
                .stream()
                .findFirst();

        if (existingPayment.isPresent() && existingPayment.get().getStatus() == DEFAULT_PAYMENT_STATUS) {
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

        Stripe.apiKey = stripeSecretKey;
        Session session = createSession(amount, typeOfPayment);
        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());

        Payment saved = paymentRepository.save(payment);
        PaymentResponseDto responseDto = paymentMapper.toDto(saved);
        responseDto.setDescription("Go to this link to finish payment: " + session.getUrl());
        return responseDto;
    }

    @Override
    public PaymentResponseFullInfoDto updatePaymentStatus(String sessionId, PaymentStatus status) {
        Payment payment = paymentRepository.findBySessionId(sessionId)
                .orElseThrow(() -> new PaymentNotFoundException(sessionId));
        payment.setStatus(status);
        return paymentMapper.toFullInfoDto(paymentRepository.save(payment));
    }

    private int getNumberOfDaysRent(Rental rental) {
        LocalDate start = rental.getRentalDate();
        LocalDate end = rental.getActualReturnDate() != null ? rental.getActualReturnDate() : LocalDate.now();
        return (int) Math.max(1, java.time.temporal.ChronoUnit.DAYS.between(start, end));
    }

    private int getNumberOfFineDays(Rental rental) {
        LocalDate scheduledReturn = rental.getReturnDate();
        LocalDate actualReturn = rental.getActualReturnDate() != null ? rental.getActualReturnDate() : LocalDate.now();
        return (int) Math.max(0, java.time.temporal.ChronoUnit.DAYS.between(scheduledReturn, actualReturn));
    }

    private Session createSession(BigDecimal amount, PaymentType type) {
        try {
            return Session.create(
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl(SUCCESS_URL)
                            .setCancelUrl(CANCEL_URL)
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("usd")
                                                            .setUnitAmountDecimal(amount.multiply(BigDecimal.valueOf(100)))
                                                            .setProductData(
                                                                    SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                            .setName("Car Rental (" + type + ")")
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
