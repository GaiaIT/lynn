package net.gaiait.divination.symbol.generative.config;

import java.util.Optional;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;

import net.gaiait.divination.symbol.generative.GenerativeSymbolValidator;

@Component
final class GenerativeConfigValidatorImpl implements GenerativeConfigValidator {
    
    public static final String NULL_ERROR = "null";
    public static final String NAME_EMPTY_ERROR = "name.empty";
    public static final String GRAPH_SIZE_RANGE_ERROR = "graphSize.range";
    public static final String EDGE_THRESHOLD_RANGE_ERROR = "edgeThreshold.range";
    public static final String NODE_THRESHOLD_RANGE_ERROR = "nodeThreshold.range";
    public static final String BREAK_PROBABILITY_RANGE_ERROR = "breakProbability.range";
    public static final String BREAK_THRESHOLD_RANGE_ERROR = "breakThreshold.range";
    public static final String PIECES_RANGE_ERROR = "pieces.range";

    @Override
    public boolean supports(Class<?> clazz) {
        return GenerativeConfigEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        if (obj == null) {
            e.reject(NULL_ERROR);
            return;
        }
        GenerativeConfig config = (GenerativeConfig) obj;
        config.getName().ifPresent(n -> validateName(n, e));
        config.getGraphSize().ifPresent(gs -> validateGraphSize(gs, e));
        config.getEdgeThreshold().ifPresent(et -> validateEdgeThreshold(et, e));
        config.getNodeThreshold().ifPresent(nt -> validateNodeThreshold(nt, e));
        config.getBreakProbability().ifPresent(bp -> validateBreakProbability(bp, e));
        config.getBreakThreshold().ifPresent(bt -> validateBreakThreshold(bt, e));
        config.getPieces().ifPresent(p -> validatePieces(p, e, config.getGraphSize()));
    }
    
    private static void validateName(String name, Errors e) {
        if (StringUtils.isEmpty(name)) {
            e.rejectValue(GenerativeConfigEntity.NAME_FIELD, NAME_EMPTY_ERROR);
        }
    }
    
    private static void validateGraphSize(Integer gs, Errors e) {
        if (!GenerativeSymbolValidator.verifyGraphSize(gs)) {
            e.rejectValue(GenerativeConfigEntity.GRAPH_SIZE_FIELD, GRAPH_SIZE_RANGE_ERROR);
        }
    }

    private static void validateEdgeThreshold(Integer et, Errors e) {
        if (!GenerativeSymbolValidator.verifyEdgeThreshold(et)) {
            e.rejectValue(GenerativeConfigEntity.EDGE_THRESHOLD_FIELD, EDGE_THRESHOLD_RANGE_ERROR);
        }
    }

    private static void validateNodeThreshold(Integer nt, Errors e) {
        if (!GenerativeSymbolValidator.verifyNodeThreshold(nt)) {
            e.rejectValue(GenerativeConfigEntity.NODE_THRESHOLD_FIELD, NODE_THRESHOLD_RANGE_ERROR);
        }
    }

    private static void validateBreakProbability(Integer bp, Errors e) {
        if (!GenerativeSymbolValidator.verifyBreakProbability(bp)) {
            e.rejectValue(GenerativeConfigEntity.BREAK_PROBABILITY_FIELD, BREAK_PROBABILITY_RANGE_ERROR);
        }
    }

    private static void validateBreakThreshold(Integer bt, Errors e) {
        if (!GenerativeSymbolValidator.verifyBreakThreshold(bt)) {
            e.rejectValue(GenerativeConfigEntity.BREAK_THRESHOLD_FIELD, BREAK_THRESHOLD_RANGE_ERROR);
        }
    }
    
    private static void validatePieces(Integer p, Errors e, Optional<Integer> graphSize) {
        if (!GenerativeSymbolValidator.verifyPieces(p, graphSize)) {
            e.rejectValue(GenerativeConfigEntity.PIECES_FIELD, PIECES_RANGE_ERROR);
        }
    }

}
