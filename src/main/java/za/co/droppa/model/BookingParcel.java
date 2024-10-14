package za.co.droppa.model;

import lombok.Data;

@Data
public class BookingParcel{

    private String parcel_number;
    private double parcel_length;
    private double parcel_breadth;
    private double parcel_height;
    private double parcel_mass;
    private String customer_reference;
    private String costCenter;
    private String noBox;

}
