package za.co.droppa.dao;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import za.co.droppa.model.Person;
import za.co.droppa.repository.PersonRepository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class PersonDAO {
    PersonRepository repository;

    public Person findById(String id) {
        Optional<Person> person = repository.findById(new ObjectId(id));
        return person.orElse(null);
    }
}
