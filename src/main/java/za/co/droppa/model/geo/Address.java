package za.co.droppa.model.geo;

import lombok.Data;

@Data
public class Address {

    private String addressLine1;
    private String locality;
    private String city;
    private String postalCode;
}
