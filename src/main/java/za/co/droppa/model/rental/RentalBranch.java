package za.co.droppa.model.rental;


import lombok.Data;
import za.co.droppa.model.Province;

@Data
public class RentalBranch {

    private Province province;

    private String name;

    private String emailAddress;

}
