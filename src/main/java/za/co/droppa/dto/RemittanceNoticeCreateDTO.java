package za.co.droppa.dto;

import lombok.Data;
import za.co.droppa.model.BankAccount;
import za.co.droppa.model.Booking;
import za.co.droppa.model.Person;

@Data
public class RemittanceNoticeCreateDTO {
    Booking booking;
    Person person;
    BankAccount bankAccount;
}
