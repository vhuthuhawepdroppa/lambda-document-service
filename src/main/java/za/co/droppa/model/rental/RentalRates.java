package za.co.droppa.model.rental;

import lombok.Data;

@Data
public class RentalRates{

    private double basePrice;

    private String rateType;//Weekly, Daily , Monthly

    private String lisenceCode;

    private int freeKms;

    private double driverRate;

    private double trackingAmt;

    private double insuranceAmt;

    private double excessKmFee;

    private double fuelFee;

    private RateCategory category;

}
