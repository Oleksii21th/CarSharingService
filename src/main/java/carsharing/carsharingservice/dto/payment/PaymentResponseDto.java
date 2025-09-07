package carsharing.carsharingservice.dto.payment;

import java.math.BigDecimal;

public class PaymentResponseDto {
    private Long id;
    private Long rentalId;
    private String status;
    private String type;
    private String sessionId;
    private BigDecimal amountToPay;
    private String description;

    public PaymentResponseDto(Long id,
                              Long rentalId,
                              String status,
                              String type,
                              String sessionId,
                              BigDecimal amountToPay,
                              String description) {
        this.id = id;
        this.rentalId = rentalId;
        this.status = status;
        this.type = type;
        this.sessionId = sessionId;
        this.amountToPay = amountToPay;
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRentalId() {
        return rentalId;
    }

    public void setRentalId(Long rentalId) {
        this.rentalId = rentalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public BigDecimal getAmountToPay() {
        return amountToPay;
    }

    public void setAmountToPay(BigDecimal amountToPay) {
        this.amountToPay = amountToPay;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
