package net.gaiait.divination.symbol.persistence;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalLong;
import java.util.concurrent.TimeUnit;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import net.gaiait.divination.reading.Hexagram;
import net.gaiait.divination.symbol.Symbol;
import net.gaiait.divination.symbol.generative.GenerativeSymbolConstants;

/**
 * This class is very flattened so it can be easily serialized into both Redis and PostgreSQL.
 * d3Json is transient because we're not interested in storing visualization data; we just wanna know
 * about the metadata...
 */
@Entity
public final class PersistedSymbolEntity implements PersistedSymbol {

    private static final Logger LOGGER = LogManager.getLogger(PersistedSymbolEntity.class);

    private static final String D3_JSON_FIELD = "d3Json";
    private static final String TIME_FIELD = "timeAsEpochSeconds";
    private static final String DIVINER_FIELD = "diviner";
    private static final String EXPIRATION_FIELD = "expiration";
    private static final String HEXAGRAM_FIELD = "hexagram";
    private static final String ID_FIELD = "id";
    private static final String SUBJECT_FIELD = "subject";
    private static final String UNIT_FIELD = "unit";
    private static final String URL_FIELD = "url";
    
    @Id 
    private Long id;
    
    @Lob
    @Type(type="org.hibernate.type.StringClobType")
    private String d3Json;
    
    private String subject;
    private Hexagram hexagram;
    private long expiration;
    private TimeUnit unit;
    private String url;  
    private String diviner;
    private Long timeAsEpochSeconds;
    
    PersistedSymbolEntity() {
      // Empty constructor required for JPA..
    }

    private PersistedSymbolEntity(Builder builder) {
        this.subject = builder.subject;
        this.hexagram = builder.hexagram;
        this.diviner = builder.diviner;
        this.timeAsEpochSeconds = builder.time.atZone(ZoneId.systemDefault()).toEpochSecond();
        this.d3Json = builder.d3Json;
        this.expiration = builder.expiration.orElse(GenerativeSymbolConstants.DEFAULT_EXPIRATION);
        this.unit = builder.unit.orElse(GenerativeSymbolConstants.DEFAULT_TIME_UNIT);
        this.url = builder.url;
        this.id = builder.id;
    }

    static class Builder {
        private String subject;
        private Hexagram hexagram;
        private String d3Json;
        private OptionalLong expiration;
        private Optional<TimeUnit> unit;
        private String url;
        private Long id;
        private String diviner;
        private LocalDateTime time;

        Builder() {
            this.expiration = OptionalLong.empty();
            this.unit = Optional.empty();
        }

        Builder(Symbol symbol) {
            if (symbol == null) {
                LOGGER.error("Symbol cannot be null");
                throw new IllegalArgumentException();
            }
            this.subject = symbol.getSubject();
            this.hexagram = symbol.getHexagram();
            this.diviner = symbol.getDiviner();
            this.time = symbol.getTime();
            this.d3Json = new Gson().toJson(symbol.getJson());
            this.expiration = OptionalLong.empty();
            this.unit = Optional.empty();
        }
        
        Builder(PersistedSymbol symbol) {
            if (symbol == null) {
                LOGGER.error("Symbol cannot be null");
                throw new IllegalArgumentException();
            }
            this.subject = symbol.getSubject();
            this.hexagram = symbol.getHexagram();
            this.diviner = symbol.getDiviner();
            this.time = symbol.getTime();
            this.d3Json = new Gson().toJson(symbol.getJson());
            this.id = symbol.getId();
            this.url = symbol.getUrl();
            this.expiration = OptionalLong.of(symbol.getExpiration());
            this.unit = Optional.ofNullable(symbol.getUnit());
        }

        Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        Builder hexagram(Hexagram hexagram) {
            this.hexagram = hexagram;
            return this;
        }
        
        Builder diviner(String diviner) {
            this.diviner = diviner;
            return this;
        }
        
        Builder time(LocalDateTime time) {
            this.time = time;
            return this;
        }

        Builder d3Json(Map<String, Object> d3Json) {
            this.d3Json = new Gson().toJson(d3Json);
            return this;
        }
        
        Builder d3JsonString(String d3JsonString) {
            this.d3Json = d3JsonString;
            return this;
        }

        Builder expiration(long expiration) {
            this.expiration = OptionalLong.of(expiration);
            return this;
        }

        Builder unit(TimeUnit unit) {
            this.unit = Optional.ofNullable(unit);
            return this;
        }

        Builder url(String url) {
            this.url = url;
            return this;
        }

        Builder id(long id) {
            this.id = id;
            return this;
        }

        public PersistedSymbolEntity build() {
            if (subject == null || hexagram == null || diviner == null || time == null) {
                LOGGER.error("Reading values for persisted symbol are invalid");
                throw new IllegalArgumentException();
            }
            if (d3Json == null || url == null || id == 0) {
                LOGGER.error("D3data or metadata for persisted symbol are invalid");
                throw new IllegalArgumentException();
            }
            return new PersistedSymbolEntity(this);
        }

    }
    
    public static PersistedSymbolEntity newInstanceFromSymbol(PersistedSymbol symbol) {
        return new Builder(symbol).build();
    }

    @JsonCreator
    public static PersistedSymbolEntity newInstanceFromJson(JsonNode json) {
        Builder b = new Builder();

        b.subject(json.get(SUBJECT_FIELD).asText())
            .diviner(json.get(DIVINER_FIELD).asText())
            .d3JsonString(json.get(D3_JSON_FIELD).asText())
            .expiration(json.get(EXPIRATION_FIELD).asLong())
            .id(json.get(ID_FIELD).asLong())
            .url(json.get(URL_FIELD).asText());

        try {
            ObjectMapper om = new ObjectMapper();

            Hexagram hex = om.treeToValue(json.get(HEXAGRAM_FIELD), Hexagram.class);
            TimeUnit timeUnit = om.treeToValue(json.get(UNIT_FIELD), TimeUnit.class);
            LocalDateTime time = Instant.ofEpochSecond(json.get(TIME_FIELD).asLong())
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            b.time(time).hexagram(hex).unit(timeUnit);
        } catch (JsonProcessingException e) {
            LOGGER.error("Error deserializing json for persisted symbol builder ", e);
            throw new IllegalArgumentException();
        }

        return b.build();
    }

    @Override
    public String getSubject() {
        return subject;
    }

    @Override
    public Hexagram getHexagram() {
        return hexagram;
    }
    
    /**
     * We're only interested in storing the string in postgres/redis..
     */
    @JsonIgnore
    @SuppressWarnings("unchecked")
    @Override
    public Map<String, Object> getJson() {
        return new Gson().fromJson(d3Json, Map.class); 
    }
    
    @JsonProperty(D3_JSON_FIELD)
    String getD3JsonString() {
        return d3Json;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getUrl() {
        return url;
    }
    
    /**
     * Redis does not store dates, so we serialize timeAsEpochSeconds instead.
     */
    @JsonIgnore
    @Override
    public LocalDateTime getTime() {
        return Instant.ofEpochSecond(timeAsEpochSeconds).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }
    
    @JsonProperty(TIME_FIELD)
    long getTimeAsEpochSeconds() {
        return timeAsEpochSeconds;
    }
    
    @Override
    public String getDiviner() {
        return diviner;      
    }
    
    @Override
    public long getExpiration() {
        return expiration;
    }

    @Override
    public TimeUnit getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("PersistedSymbolEntity [id=");
        sb.append(id);
        sb.append(", d3Json=");
        sb.append(d3Json.contains("nodes"));
        sb.append(", subject=");
        sb.append(subject);
        sb.append(", hexagram=");
        sb.append(hexagram);
        sb.append(", expiration=");
        sb.append(expiration);
        sb.append(", unit=");
        sb.append(unit);
        sb.append(", url=");
        sb.append(url);
        sb.append(", diviner=");
        sb.append(diviner);
        sb.append(", timeAsEpochSeconds=");
        sb.append(timeAsEpochSeconds);
        sb.append("]");
        return sb.toString();
    }

    
    

}
