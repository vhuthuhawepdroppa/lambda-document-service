package za.co.droppa.model;


import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import za.co.droppa.model.geo.Location;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "droppa.booking")
public class Booking {
	@Id
	private ObjectId id;

    private String dropOffOid;

    private String pickUpOid;

    private String userOid;

    private String vehicleType;

	private boolean isBucket;

    private String pickUpAddress;

    private String pickUpDate;

    private String pickUpTime;

    private String dropOffAddress;

	private String skynetStatus;

    private double price;

    private String phoneNo;

    private int labour;

    private String comment;

    private String timestamp;

    private boolean canopy;

    private int load;

    private BookingStatus status;
    
    private int pickUpFloors;
    
    private int dropFloors;
    
	private String driverOid;

    private String dropOid;

    private boolean movedToExpress;

    private String trackNo;
    
    private Location pickUpCoordinate;
    
    private Location dropOffCoordinate;

    private boolean isPayed;

    private Province province;
    
    private String platform;
    
    private String promotionCode;

	private String toEmail;

    private boolean isRated;

    private String firebaseToken;

    private BookingPaymentType paymentType;
    
    private LocalDateTime bookingCreatedDate;

	private boolean isExpress;

	private int distance;

	private double itemMass;

	private boolean isItemPicked;

	private TransportMode transportMode;

	private String mainCityOid;

	private Type type;

	private double pickUpAmt;

	private double airlineAmt;

	private double dropOffAmt;

	private String parentBookingOid;

	private String returnComments;

	private Province destinationProvince;

	private String fromSuburb;

	private String toSuburb;

	private String pickUpPCode;

	private String dropOffPCode;


	private WaybillResponse waybillResponse;

	private String manualWaybill;

	private boolean isManualWaybill;

	private boolean isPaidViaWallet;

	private String customerReference;

	private String costCenter;

	private List<ParcelDimension> parcelDimensions;

	private List<BookingParcel> parcels;

	private String shopify_orderNo;


	private LocalDateTime expectedDeliveryDate;


	/*Linda
	16/08/2023*/
	private boolean isTender;

	/*
	Terrence
	26/06/2022
	 */
	private String retailId;


	private boolean isDashBike;


	private String tipOid;

	private String toAlternativeContactName;
	private String toAlternativeContactNumber;
	private boolean isUber;


	private boolean priority;

	private boolean emailed;
	private String fromCity;
	private String toCity;

	private String regionOid;


	//who will br responsible for the vat and duties
	private boolean isReceiverPaying;

	private BookingMetaData metaData;

}
