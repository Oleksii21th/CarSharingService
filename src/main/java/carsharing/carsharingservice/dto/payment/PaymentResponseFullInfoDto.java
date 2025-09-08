package carsharing.carsharingservice.dto.payment;

import carsharing.carsharingservice.dto.rental.RentalResponseDto;
import java.math.BigDecimal;

public class PaymentResponseFullInfoDto {
    private Long id;
    private Long userId;
    private String type;
    private BigDecimal amountToPay;
    private RentalResponseDto rental;

    public PaymentResponseFullInfoDto(Long id,
                                      Long userId,
                                      String type,
                                      BigDecimal amountToPay,
                                      RentalResponseDto rental) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amountToPay = amountToPay;
        this.rental = rental;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(BigDecimal amountToPay) {
        this.amountToPay = amountToPay;
    }

    public RentalResponseDto getRental() {
        return rental;
    }

    public void setRental(RentalResponseDto rental) {
        this.rental = rental;
    }
}
