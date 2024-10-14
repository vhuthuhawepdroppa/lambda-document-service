package za.co.droppa.model;


import lombok.Data;

@Data
public class ParcelDimension {

    private double parcel_length;

    private double parcel_breadth;

    private double parcel_height;

    private double parcel_mass;

    private String parcel_reference;
    private String description;
    private String parcel_number;
    private double volume;
    private String item;
    private int quantity;
    private String hs_code;
    private String price;


    @Override
    public boolean equals(Object o) {
        if(o instanceof ParcelDimension) {
            ParcelDimension other = (ParcelDimension) o;
            return other.getParcel_length() == parcel_length && other.getParcel_breadth() == parcel_breadth && other.getParcel_height() == parcel_height && other.getParcel_mass() == parcel_mass;
        }

        return false;
    }
}
