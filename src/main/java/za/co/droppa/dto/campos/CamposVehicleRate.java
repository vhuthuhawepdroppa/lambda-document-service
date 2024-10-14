package za.co.droppa.dto.campos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class CamposVehicleRate
{
    private String vehicle;
    private double base_price;
    private double price_per_km;
}
