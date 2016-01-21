package net.gaiait.divination.reading;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

public final class ReadingRequestImpl implements ReadingRequest {

    public static final String SUBJECT_FIELD = "subject";
    public static final String HEXAGRAM_FIELD = "hexagramNo";
    public static final String DIVINER_FIELD = "diviner";

    private final String subject;
    private final int hexagramNo;
    private final String diviner;

    private ReadingRequestImpl(Builder builder) {
        this.subject = builder.subject;
        this.hexagramNo = builder.hexagramNo;
        this.diviner = builder.diviner;
    }

    public static class Builder {
        private final String subject;
        private final int hexagramNo;
        private String diviner;
        
        public Builder(String subject, int hexagramNo) {
            this.subject = subject;
            this.hexagramNo = hexagramNo;
        }
        
        Builder diviner(String diviner) {
            this.diviner = diviner;
            return this;
        }
        
        ReadingRequestImpl build() {
            return new ReadingRequestImpl(this);
        }
        
    }
    
    @JsonCreator
    public static ReadingRequestImpl newInstanceFromJson(JsonNode json) {
        String subject = json.get(SUBJECT_FIELD).asText();
        int hexagramNo = json.get(HEXAGRAM_FIELD).asInt();
        
        Builder builder = new Builder(subject, hexagramNo);      
        Optional.ofNullable(json.get(DIVINER_FIELD).asText()).map(builder::diviner);
        
        return builder.build();
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public int getHexagramNo() {
        return hexagramNo;
    }

    @Override
    public Optional<String> getDiviner() {
        return Optional.ofNullable(diviner);
    }

}
