package za.co.droppa.dao;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import za.co.droppa.model.Contact;
import za.co.droppa.repository.ContactRepository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class ContactDAO {

    ContactRepository repository;

    public Contact findById(String id) {
        Optional<Contact> contact = repository.findById(new ObjectId(id));
        return contact.orElse(null);
    }
}
