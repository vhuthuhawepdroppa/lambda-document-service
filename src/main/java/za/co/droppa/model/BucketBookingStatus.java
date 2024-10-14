package za.co.droppa.model;

/**
 * Developer: Nicolas Tebogo
 * Date: 18/09/2018
 */
public enum BucketBookingStatus {

	BUCKET_AWAITING("Awaiting to be pushed"),
    COMPLETE("Booking successfully complete"),
    AWAITING_DRIVER("Awaiting driver to be assigned"),
    IN_TRANSACT("Driver in transit"),
    INVALID("Booking cancelled"),
    AWAITING_PAYMENT("Awaiting payments"),
    CANCELLED("Cancelled"),
    CREATED_WAYBILL("Waybill Created"),
    AWAITING_AUTHORIZATION("Awaiting Authorization"),
    ELEMENT_TO_ANOTHER_BRANCH("Moved to another branch"),
	RESERVED("Accepted");

    private String description;

    BucketBookingStatus(String description) {
        this.description = description;
    }

    public String description() { return description; }
    
    public static BucketBookingStatus findByName(String name) {
        final String status = name.trim().toUpperCase();
        for (BucketBookingStatus v : values()) {
            if (v.name().equals(status)) {
                return v;
            }
        }
        return null;
    }
    
}
