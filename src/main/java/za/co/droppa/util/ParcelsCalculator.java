package za.co.droppa.util;

import za.co.droppa.dto.booking.Parcel;
import za.co.droppa.model.ParcelDimension;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ParcelsCalculator
{
    public static double calculateVolume(Parcel parcel){
        double volume = parcel.getVolume() == 0 ? (parcel.getLength()/100 * parcel.getWidth()/100 * parcel.getHeight()/100) * (parcel.getQuantity()==0 ? 1 : parcel.getQuantity()) :  parcel.getVolume();
        BigDecimal roundedVolume = new BigDecimal(volume).setScale(1, RoundingMode.HALF_UP);
        return roundedVolume.doubleValue() ;
    }
    public static double calculateVolume(ParcelDimension parcel){
        double volume = parcel.getVolume() == 0 ? ((parcel.getParcel_length() / 100 * parcel.getParcel_breadth() / 100 * parcel.getParcel_height() / 100) * 1) : parcel.getVolume();
        BigDecimal roundedVolume = new BigDecimal(volume).setScale(1, RoundingMode.HALF_UP);
        return roundedVolume.doubleValue() ;
    }

}
