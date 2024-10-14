package za.co.droppa.model.rental;


import lombok.Data;
import za.co.droppa.model.geo.Location;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class RentalTruck{

    private Location from;

    private String branchOid;

    private LocalDateTime in;

    private LocalDateTime out;

    private RateUnit rateUnit;

    private RateCategory category;

    private Integer assistance;

    private Integer includedDistance;

    private String comments;

    private String customerOid;

    private String trackNo;

    private double total;

    private double holderFee;

    private RentalStatus status;

    private String platform;

    private LocalDateTime rentalCreationDate;

    private PaymentType paymentType;

    private boolean isPayed;

    private String typeRateOid;

    private List<DailyDistance> dailyDistance;

    private String driverOid;
}
