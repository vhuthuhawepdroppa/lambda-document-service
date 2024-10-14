package za.co.droppa.dto;

import lombok.Data;
import za.co.droppa.model.BucketBooking;

@Data
public class DeliveryNoteCreateDTO {
    BillDTO billDTO;
    BucketBooking bucketBooking;
}
