package net.gaiait.divination.reading;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.OptionalLong;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StringUtils;

final class ReadingImpl implements Reading {

    private static final Logger LOGGER = LogManager.getLogger(ReadingImpl.class);

    private static final String DEFAULT_DIVINER = "Elemental Spirits";

    private final String subject;
    private final Hexagram hexagram;
    private final String diviner;
    private final long secondsSinceEpoch;

    private ReadingImpl(Builder builder) {
        this.subject = builder.subject;
        this.hexagram = builder.hexagram;
        this.diviner = builder.diviner.orElse(DEFAULT_DIVINER);
        this.secondsSinceEpoch = builder.secondsSinceEpoch.orElse(Instant.now().getEpochSecond());
    }
    
    static class Builder {
        private final String subject;
        private final Hexagram hexagram;
        private Optional<String> diviner;
        private OptionalLong secondsSinceEpoch;
        
        Builder(String sub, Hexagram hex) {
            if (StringUtils.isEmpty(sub)) {
                LOGGER.error("Reading subject cannot be empty");
                throw new IllegalArgumentException();
            }
            if (hex == null) {
                LOGGER.error("Reading hexgram cannot be null");
                throw new IllegalArgumentException();
            }
            this.subject = sub;
            this.hexagram = hex;
            this.diviner = Optional.empty();
            this.secondsSinceEpoch = OptionalLong.empty();
        }
        
        Builder(String sub, int hexagramNo) {
            if (StringUtils.isEmpty(sub)) {
                LOGGER.error("Reading subject cannot be empty");
                throw new IllegalArgumentException();
            }
            this.subject = sub;
            try {
                this.hexagram = Hexagram.ofInt(hexagramNo);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e);
            }
            this.diviner = Optional.empty();
            this.secondsSinceEpoch = OptionalLong.empty();
        }
        
        Builder diviner(String diviner) {
            if (StringUtils.isEmpty(diviner)) {
                LOGGER.error("Diviner cannot be empty");
                throw new IllegalArgumentException();
            }
            this.diviner = Optional.of(diviner);
            return this;
        }
        
        Builder dateTime(long secondsSinceEpoch) {
            if (secondsSinceEpoch == 0) {
                LOGGER.error("DateTime cannot be 0");
                throw new IllegalArgumentException();
            }
            this.secondsSinceEpoch = OptionalLong.of(secondsSinceEpoch);
            return this;
        }
        
        ReadingImpl build() {
            return new ReadingImpl(this);
        }      
        
    }
    
    static ReadingImpl newInstanceFromRequest(ReadingRequest req) {
        if (req == null) {
            LOGGER.error("Request cannot be null");
            throw new IllegalArgumentException();
        }
        Builder builder = new Builder(req.getSubject(), req.getHexagramNo());
        req.getDiviner().ifPresent(builder::diviner);
        return builder.build();
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public Hexagram getHexagram() {
        return hexagram;
    }
    
    @Override
    public String getDiviner() {
        return diviner;
    }
    
    @Override
    public LocalDateTime getTime() {
        return Instant.ofEpochSecond(secondsSinceEpoch).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReadingImpl [subject=");
        sb.append(subject);
        sb.append(", hexagram=");
        sb.append(hexagram);
        sb.append(", diviner=");
        sb.append(diviner);
        sb.append(", secondsSinceEpoch=");
        sb.append(secondsSinceEpoch);
        sb.append("]");
        return sb.toString();
    }

}
