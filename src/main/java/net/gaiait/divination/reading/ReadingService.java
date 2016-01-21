package net.gaiait.divination.reading;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public interface ReadingService {

    Optional<Reading> createReading(ReadingRequest req);

}
