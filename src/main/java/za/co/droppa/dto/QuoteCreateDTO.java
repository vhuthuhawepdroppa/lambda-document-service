package za.co.droppa.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class QuoteCreateDTO
{
    public List<String> pickUpAddresses;

    public List<String> dropOffAddresses;

    public double totalPrice;

    public int parcelNumber;

    public String selectedService;

    public double insuranceAmount;

    public int numHelpers;

    public String retailId;

    @Override
    public String toString() {
        return "DownloadQuoteDTO{" +
                "pickUpAddresses=" + pickUpAddresses +
                ", dropOffAddresses=" + dropOffAddresses +
                ", totalPrice=" + totalPrice +
                ", parcelNumber=" + parcelNumber +
                ", selectedService='" + selectedService + '\'' +
                '}';
    }


    public boolean isFurniture(){
        return selectedService.equals("move_furniture") || selectedService.equals("multiple_bookings");
    }


    public static QuoteCreateDTO createDummy(){
        QuoteCreateDTO dummy = new QuoteCreateDTO();
        dummy.pickUpAddresses = new ArrayList<>();
        dummy.dropOffAddresses = new ArrayList<>();
        dummy.totalPrice = 0.0;
        dummy.parcelNumber = 0;
        dummy.selectedService = "";
        dummy.insuranceAmount = 0.0;
        dummy.numHelpers = 0;
        dummy.retailId = "";
        return dummy;
    }
}
