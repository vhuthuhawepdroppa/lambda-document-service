package za.co.droppa.model;

import lombok.Data;
import org.bson.types.ObjectId;

@Data
public class PersistentObject {
    private ObjectId oid;
}
