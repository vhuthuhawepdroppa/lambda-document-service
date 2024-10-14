package za.co.droppa.dto.campos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import za.co.droppa.dto.booking.Parcel;
import za.co.droppa.model.geo.Address;

import java.util.List;

@Getter
@Setter
@ToString
public class CamposRateRequest
{
    private Address pickUpAddress;
    private Address destinationAddress;
    private boolean collection;
    private LoadType load_type;
    private List<Parcel> parcels;
    private CamposVehicle vehicle;
    private double distance;
    private double duration;
    private int labour;
}
