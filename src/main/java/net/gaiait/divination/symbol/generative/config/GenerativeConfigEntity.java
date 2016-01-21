package net.gaiait.divination.symbol.generative.config;

import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.JsonNode;

@Entity
public final class GenerativeConfigEntity implements GenerativeConfig {
    
    public static final String NAME_FIELD = "name";
    public static final String GRAPH_SIZE_FIELD = "graphSize";
    public static final String EDGE_THRESHOLD_FIELD = "edgeThreshold";
    public static final String NODE_THRESHOLD_FIELD = "nodeThreshold";
    public static final String BREAK_PROBABILITY_FIELD = "breakProbability";
    public static final String BREAK_THRESHOLD_FIELD = "breakThreshold";
    public static final String PIECES_FIELD = "pieces";
       
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private long id;
    private String name;
    private Integer graphSize;
    private Integer edgeThreshold;
    private Integer nodeThreshold;
    private Integer breakProbability;
    private Integer breakThreshold;
    private Integer pieces;
    
    GenerativeConfigEntity() {
     // Empty constructor required for JPA..
    }
    
    private GenerativeConfigEntity(Builder builder) {
        this.name = builder.name;
        this.graphSize = builder.graphSize;
        this.edgeThreshold = builder.edgeThreshold;
        this.nodeThreshold = builder.nodeThreshold;
        this.breakProbability = builder.breakProbability;
        this.breakThreshold = builder.breakThreshold;
        this.pieces = builder.pieces;
    }
    
    static class Builder {
        private String name;
        private Integer graphSize;
        private Integer edgeThreshold;
        private Integer nodeThreshold;
        private Integer breakProbability;
        private Integer breakThreshold;
        private Integer pieces;
        
        Builder() {}
        
        Builder(GenerativeConfig config) {
            config.getName().ifPresent(this::name);
            config.getGraphSize().ifPresent(this::graphSize);
            config.getEdgeThreshold().ifPresent(this::edgeThreshold);
            config.getNodeThreshold().ifPresent(this::nodeThreshold);
            config.getBreakProbability().ifPresent(this::breakProbability);
            config.getBreakThreshold().ifPresent(this::breakThreshold);
            config.getPieces().ifPresent(this::pieces);
        }
        
        Builder name(String name) {
            this.name = name;
            return this;
        }

        Builder graphSize(Integer graphSize) {
            this.graphSize = graphSize;
            return this;
        }

        Builder edgeThreshold(Integer edgeThreshold) {
            this.edgeThreshold = edgeThreshold;
            return this;
        }

        Builder nodeThreshold(Integer nodeThreshold) {
            this.nodeThreshold = nodeThreshold;
            return this;
        }

        Builder breakProbability(Integer breakProbability) {
            this.breakProbability = breakProbability;
            return this;
        }

        Builder breakThreshold(Integer breakThreshold) {
            this.breakThreshold = breakThreshold;
            return this;
        }
        
        Builder pieces(Integer pieces) {
            this.pieces = pieces;
            return this;
        }

        GenerativeConfigEntity build() {
            return new GenerativeConfigEntity(this);
        }
    }
 
    static GenerativeConfigEntity newInstance(GenerativeConfig config) {
        return new Builder(config).build();
    }
    
    @JsonCreator
    public static GenerativeConfigEntity newInstanceFromJson(JsonNode json) {
        Builder b = new Builder();
        Optional.ofNullable(json.get(GenerativeConfigEntity.NAME_FIELD).asText()).ifPresent(b::name);
        Optional.ofNullable(json.get(GenerativeConfigEntity.GRAPH_SIZE_FIELD).asInt()).ifPresent(b::graphSize);
        Optional.ofNullable(json.get(GenerativeConfigEntity.EDGE_THRESHOLD_FIELD).asInt()).ifPresent(b::edgeThreshold);
        Optional.ofNullable(json.get(GenerativeConfigEntity.NODE_THRESHOLD_FIELD).asInt()).ifPresent(b::nodeThreshold);
        Optional.ofNullable(json.get(GenerativeConfigEntity.BREAK_PROBABILITY_FIELD).asInt()).ifPresent(b::breakProbability);
        Optional.ofNullable(json.get(GenerativeConfigEntity.BREAK_THRESHOLD_FIELD).asInt()).ifPresent(b::breakThreshold);
        Optional.ofNullable(json.get(GenerativeConfigEntity.PIECES_FIELD).asInt()).ifPresent(b::pieces);      
        return b.build();
    }

    @Override
    public Optional<String> getName() {
        return Optional.ofNullable(name);
    }

    @Override
    public Optional<Integer> getGraphSize() {
        return Optional.ofNullable(graphSize);
    }

    @Override
    public Optional<Integer> getEdgeThreshold() {
        return Optional.ofNullable(edgeThreshold);
    }

    @Override
    public Optional<Integer> getNodeThreshold() {
        return Optional.ofNullable(nodeThreshold);
    }

    @Override
    public Optional<Integer> getBreakProbability() {
        return Optional.ofNullable(breakProbability);
    }

    @Override
    public Optional<Integer> getBreakThreshold() {
        return Optional.ofNullable(breakThreshold);
    }

    @Override
    public Optional<Integer> getPieces() {
        return Optional.ofNullable(pieces);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GenerativeConfigEntity [id=");
        sb.append(id);
        sb.append(", name=");
        sb.append(name);
        sb.append(", graphSize=");
        sb.append(graphSize);
        sb.append(", edgeThreshold=");
        sb.append(edgeThreshold);
        sb.append(", nodeThreshold=");
        sb.append(nodeThreshold);
        sb.append(", breakProbability=");
        sb.append(breakProbability);
        sb.append(", breakThreshold=");
        sb.append(breakThreshold);
        sb.append(", pieces=");
        sb.append(pieces);
        sb.append("]");
        return sb.toString();
    }

}
