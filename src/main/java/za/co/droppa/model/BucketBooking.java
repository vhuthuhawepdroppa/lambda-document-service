package za.co.droppa.model;


import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BucketBooking {

    private List<Booking> bookings;

    private Retail retail;

    private String retailOid;

    private double price;

    private String driverOid;

    private String dropOid;

    private BucketBookingStatus status;

    private String vehicleType;

    private LocalDateTime date;

    private String time;

    private String trackNo;

    private String comments;

    private String labour;

    private Province province;

    private boolean isPayed;

    private String promotionCode;

    private double priceAfterPromo;

    private double returnAmt;

    private boolean isStandardRate;

    private boolean availableExpress;

    private boolean isExpress;

    private LocalDateTime bookingCreatedDate;

    private BookingPaymentType paymentType;


    private boolean isPaidViaWallet;

    private Type type;

    private boolean hasParcels;


    private String toEmail;

    private boolean isInBound;

    private boolean priority;
}
