package net.gaiait.divination.symbol.generative;

import java.util.Optional;
import java.util.OptionalLong;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

import net.gaiait.divination.reading.ReadingRequest;
import net.gaiait.divination.reading.ReadingRequestImpl;
import net.gaiait.divination.symbol.SymbolConfig;
import net.gaiait.divination.symbol.SymbolRequest;
import net.gaiait.divination.symbol.generative.config.GenerativeConfig;
import net.gaiait.divination.symbol.generative.config.GenerativeConfigEntity;

public final class GenerativeSymbolRequest implements SymbolRequest {

    public static final String READING_FIELD = "reading";
    public static final String CONFIG_FIELD = "symbolConfig";
    public static final String EXPIRATION_FIELD = "expiration";

    private final ReadingRequest reading;
    private final GenerativeConfig generativeConfig;
    private final Long expiration;

    private GenerativeSymbolRequest(Builder builder) {
        this.reading = builder.readingReq;
        this.generativeConfig = builder.generativeConfig;
        this.expiration = builder.expiration;
    }
    
    public static class Builder {
        
        private final ReadingRequest readingReq;
        private GenerativeConfig generativeConfig;
        private Long expiration;
        
        public Builder(ReadingRequest req) {
            this.readingReq = req;
        }
        
        public Builder generativeConfig(GenerativeConfig config) {
            this.generativeConfig = config;
            return this;
        }
        
        public Builder expiration(Long expiration) {
            this.expiration = expiration;
            return this;
        }
        
        public GenerativeSymbolRequest build() {
            return new GenerativeSymbolRequest(this);
        }
        
    }

    @JsonCreator
    public static GenerativeSymbolRequest newInstanceFromJson(JsonNode json) {
        ReadingRequest req = ReadingRequestImpl.newInstanceFromJson(json.get(READING_FIELD));
        GenerativeSymbolRequest.Builder builder = new GenerativeSymbolRequest.Builder(req);
        
        OptionalLong.of(json.get(EXPIRATION_FIELD).asLong()).ifPresent(builder::expiration);
        Optional.ofNullable(json.get(CONFIG_FIELD)).map(GenerativeConfigEntity::newInstanceFromJson).map(builder::generativeConfig);
        
        return builder.build();
    }
    
    @Override
    public ReadingRequest getReadingRequest() {
        return reading;
    }

    @Override
    public String getSubject() {
        return reading.getSubject();
    }

    @Override
    public int getHexagramNo() {
        return reading.getHexagramNo();
    }
    
    @Override
    public Optional<String> getDiviner() {
        return reading.getDiviner();
    }
    
    @Override
    public Optional<SymbolConfig> getSymbolConfig() {
        return Optional.ofNullable(generativeConfig);
    }
    
    @Override
    public OptionalLong getExpiration() {
        return OptionalLong.of(expiration);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GenerativeSymbolRequest [reading=");
        sb.append(reading);
        sb.append(", generativeConfig=");
        sb.append(generativeConfig);
        sb.append(", expiration=");
        sb.append(expiration);
        sb.append("]");
        return sb.toString();
    }

}
