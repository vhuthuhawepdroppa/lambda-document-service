package za.co.droppa.dto.campos;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;


@Getter
@Setter
@ToString
@Builder
public class CamposRateResponse
{
    public double price_excluding_vat;
    private double rateAmount;
    private double fuel_levey;
    private double collectionAmount;
    private double vat;
    private double total_price;
    private List<CamposVehicleRate> rates;
    private String recommendVehicle;
}
