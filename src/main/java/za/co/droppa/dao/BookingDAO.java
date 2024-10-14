package za.co.droppa.dao;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import za.co.droppa.model.Booking;
import za.co.droppa.repository.BookingRepository;

import java.util.Optional;

@Service
@AllArgsConstructor
public class BookingDAO {
    private BookingRepository bookingRepository;

    public Booking findByTrackNo(String trackNo) {
        Optional<Booking> bookingOp = bookingRepository.findByTrackNo(trackNo);

        return bookingOp.orElse(null);
    }
}
