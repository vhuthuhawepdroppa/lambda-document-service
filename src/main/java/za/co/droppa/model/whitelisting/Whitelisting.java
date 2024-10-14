package za.co.droppa.model.whitelisting;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "droppa.whitelisting")
public class Whitelisting
{
    @Id
    private ObjectId id;

    private String name;
    private String retailId;

    private String logo;

    private boolean canUseDroppaSite;

}
