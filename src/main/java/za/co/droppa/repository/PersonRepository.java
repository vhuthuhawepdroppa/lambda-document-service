package za.co.droppa.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.droppa.model.Person;

public interface PersonRepository extends MongoRepository<Person, ObjectId> {

}
