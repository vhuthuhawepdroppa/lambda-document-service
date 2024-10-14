package za.co.droppa.model;


import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "droppa.persons")
public class Person {
    @Id
    private ObjectId id;

    private String deviceId;

    private String mobile;

    private CodeConfirmation mobileConfirmation;

    private String email;

    private String skynetId;

    private CodeConfirmation emailConfirmation;

    private String firstName;

    private String surname;

    private String rsaId;

    private byte[] avatar; //Base64 encoded

    private List<String> documentOids;

    private String userAccountOid;

    private String retailId;

    private boolean isSkynet;

    private String accountNo;
}
