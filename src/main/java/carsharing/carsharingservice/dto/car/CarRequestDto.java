package carsharing.carsharingservice.dto.car;

import carsharing.carsharingservice.validation.ValidCarType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CarRequestDto {
    @NotNull
    private String model;
    @NotNull
    private String brand;
    @NotNull
    @ValidCarType
    private String type;
    @NotNull
    private int inventory;
    @NotNull
    @Min(value = 0)
    private BigDecimal dailyFee;

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getInventory() {
        return inventory;
    }

    public void setInventory(int inventory) {
        this.inventory = inventory;
    }

    public BigDecimal getDailyFee() {
        return dailyFee;
    }

    public void setDailyFee(BigDecimal dailyFee) {
        this.dailyFee = dailyFee;
    }
}