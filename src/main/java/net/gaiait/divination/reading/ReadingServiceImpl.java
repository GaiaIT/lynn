package net.gaiait.divination.reading;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

@Service
final class ReadingServiceImpl implements ReadingService {

    private static final Logger LOGGER = LogManager.getLogger(ReadingServiceImpl.class);

    @Override
    public Optional<Reading> createReading(ReadingRequest req) {
        if (req == null) {
            LOGGER.error("Failed to create reading; reading request is null");
            return Optional.empty();
        }
        try {
            return Optional.of(ReadingImpl.newInstanceFromRequest(req));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failed to create reading; reading request has invalid arguments", e);
            return Optional.empty();
        }
    }

}
