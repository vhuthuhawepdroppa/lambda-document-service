package za.co.droppa.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import za.co.droppa.model.geo.Address;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Document(collection = "droppa.retail")
public class Retail {
    @Id
    private ObjectId id;

    private String registrationNumber;

    private String name;

    private Person contact;

    private Person owner;

    private List<String> driverOids;

    private List<String> promoOids;

    private LocalDateTime invoiceDate;

    private String vatNo;

    private boolean canInvoice;

    private Address address;

    private String accountManagerOid;

    private boolean canAddParcels;

    private boolean canAssignDriver;

    private List<Person> person;

    private List<String> documentOids;


    /**
     * This is the retail id of the retail that recruited this retail
     */
    private String parentRetailId;


    private boolean circleRouteRate;

    public boolean priority;

    private String whitelistingId;
}
