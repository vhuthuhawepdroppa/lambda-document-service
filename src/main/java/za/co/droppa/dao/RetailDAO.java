package za.co.droppa.dao;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import za.co.droppa.model.Retail;
import za.co.droppa.repository.RetailRepository;

import java.util.Optional;

@AllArgsConstructor
@Repository
public class RetailDAO {
    RetailRepository repository;

    public Retail findById(String id) {
        Optional<Retail> retail = repository.findById(new ObjectId(id));
        return retail.orElse(null);
    }
}
