package za.co.droppa.dao;

import lombok.AllArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Repository;
import za.co.droppa.model.whitelisting.Whitelisting;
import za.co.droppa.repository.WhitelistingRepository;

import java.util.Optional;

@Repository
@AllArgsConstructor
public class WhitelistingDAO {
    WhitelistingRepository repository;

    public Whitelisting findById(String id) {
        Optional<Whitelisting> whitelisting = repository.findById(new ObjectId(id));
        return whitelisting.orElse(null);
    }
}
