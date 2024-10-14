package za.co.droppa.dto;

import lombok.Data;
import za.co.droppa.dto.campos.CamposRateResponse;
import za.co.droppa.model.Booking;
import za.co.droppa.model.Contact;
import za.co.droppa.model.Person;

@Data
public class CampostCreateDTO {
    Booking booking;
    Person person;
    Contact contact;
    Contact dropOff;
    CamposRateResponse camposRateResponse;
}
