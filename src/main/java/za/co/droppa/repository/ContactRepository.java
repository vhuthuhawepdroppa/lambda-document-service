package za.co.droppa.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.droppa.model.Contact;

public interface ContactRepository extends MongoRepository<Contact, ObjectId> {
}
