package za.co.droppa.model;

/**
 * Developer: Nicolas Tebogo
 * Date: 09/05/2017
 */

public enum BookingStatus {
	
    COMPLETE("Booking successfully complete"),
    AWAITING_DRIVER("Awaiting driver to be assigned"),
    IN_TRANSACT("Driver in transit"),
    AWAITING_PAYMENT("Awaiting payments"),
	RESERVED("Accepted"),
	REMOVED("Booking removed from bucket"),
	CANCELLED("Booking cancelled"),
	INVALID("Booking cancelled"),
	UNKNOWN_STATUS("Booking is more than 30 days old"),

    OUT_FOR_COLLECTION("DRIVER IS OUT TO COLLECTION"),
    PROCESSING_FLIGHT_DELIVERY("Processing Flight"),
    CREATED_WAYBILL("Waybill Created"),
	REQUEST("Request Booking"),
    WAYBILL_CREATION_FAILED("Failed to create way Bill"),
    PROCESSING_WAYBILL("Processing Waybill"),
    AWAITING_SHIPPING("Awaiting to be shipped"),
	BUCKET("Bucket Booking"),
    OUT_FOR_DELIVERY("Out of Delivery Booking"),
    COLLECTED("Collected Booking"),
    PROCESSING_COORDINATES("Coordinates are null");

    private String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String description() { return description; }
    
    public static BookingStatus findByName(String name) {
        for (BookingStatus v : values()) {
            if (v.name().equals(name.toUpperCase())) {
                return v;
            }
        }
        return null;
    }
}
