package za.co.droppa.repository;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import za.co.droppa.model.Booking;

import java.util.Optional;

public interface BookingRepository extends MongoRepository<Booking, ObjectId> {
    Optional<Booking> findByTrackNo(String trackNo);
}
