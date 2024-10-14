package za.co.droppa.dto.booking;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Parcel
{

    private String description;
    private int quantity;
    private double length;
    private double width;
    private double height;
    private double volume;
    private String item;

}