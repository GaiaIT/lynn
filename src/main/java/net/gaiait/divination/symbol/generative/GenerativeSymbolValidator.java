package net.gaiait.divination.symbol.generative;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import net.gaiait.divination.reading.Hexagram;
import net.gaiait.divination.reading.Reading;
import net.gaiait.divination.symbol.generative.config.GenerativeConfig;

@Component
public final class GenerativeSymbolValidator {
    
    private static final Logger LOGGER = LogManager.getLogger(GenerativeSymbolValidator.class);
    
    /**
     * Verify Readings....
     */  
    public static boolean verifyReading(Reading reading) {
        if (reading == null) {
            LOGGER.error("Generative symbol cannot have null reading");
            return false;
        }
        List<Boolean> validity = new LinkedList<>();
        validity.add(verifySubject(reading.getSubject()));
        validity.add(verifyHexagram(reading.getHexagram()));
        validity.add(verifyDiviner(reading.getDiviner()));
        validity.add(verifyTime(reading.getTime()));
        if (validity.contains(Boolean.FALSE)) {
            return false;
        }
        return true;
    }
    
    public static boolean verifySubject(String subject) {
        if (StringUtils.isEmpty(subject)) {
            LOGGER.error("Generative symbol cannot have empty subject");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyHexagram(Hexagram hex) {
        if (hex == null) {
            LOGGER.error("Generative symbol cannot have null hexagram");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyDiviner(String diviner) {
        if (StringUtils.isEmpty(diviner)) {
            LOGGER.error("Generative symbol cannot have empty diviner");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyTime(LocalDateTime time) {
        if (time == null) {
            LOGGER.error("Generative symbol cannot have null time");
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    /**
     * Verify configs....
     */
    public static boolean verifyConfig(GenerativeConfig config) {
        if (config == null) {
            LOGGER.error("Generative symbol cannot have null config");
            return false;
        }
        List<Boolean> validity = new LinkedList<>();
        validity.add(config.getGraphSize().map(GenerativeSymbolValidator::verifyGraphSize).orElse(Boolean.TRUE));
        validity.add(config.getEdgeThreshold().map(GenerativeSymbolValidator::verifyEdgeThreshold).orElse(Boolean.TRUE));
        validity.add(config.getNodeThreshold().map(GenerativeSymbolValidator::verifyNodeThreshold).orElse(Boolean.TRUE));
        validity.add(config.getBreakProbability().map(GenerativeSymbolValidator::verifyBreakProbability).orElse(Boolean.TRUE));
        validity.add(config.getBreakThreshold().map(GenerativeSymbolValidator::verifyBreakThreshold).orElse(Boolean.TRUE));
        validity.add(config.getPieces().map(p -> verifyPieces(p, config.getGraphSize())).orElse(Boolean.TRUE));
        if (validity.contains(Boolean.FALSE)) {
            return false;
        }
        return true;
    }
    
    public static boolean verifyGraphSize(int graphSize) {
        if (graphSize < GenerativeSymbolConstants.MIN_GRAPH_SIZE || graphSize > GenerativeSymbolConstants.MAX_GRAPH_SIZE) {
            LOGGER.error("graphSize for generative symbol exceeds range, {}", graphSize);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyEdgeThreshold(int edgeThreshold) {
        if (edgeThreshold < GenerativeSymbolConstants.MIN_EDGE_THRESHOLD || edgeThreshold > GenerativeSymbolConstants.MAX_EDGE_THRESHOLD) {
            LOGGER.error("edgeThreshold for generative symbol exceeds range, {}", edgeThreshold);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyNodeThreshold(int nodeThreshold) {
        if (nodeThreshold < GenerativeSymbolConstants.MIN_NODE_THRESHOLD || nodeThreshold > GenerativeSymbolConstants.MAX_NODE_THRESHOLD) {
            LOGGER.error("nodeThreshold for generative symbol exceeds range, {}", nodeThreshold);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyBreakProbability(int breakProbability) {
        if (breakProbability < GenerativeSymbolConstants.MIN_BREAK_PROBABILITY
                || breakProbability > GenerativeSymbolConstants.MAX_BREAK_PROBABILITY) {
            LOGGER.error("breakProbability for generative symbol exceeds range, {}", breakProbability);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyBreakThreshold(int breakThreshold) {
        if (breakThreshold < GenerativeSymbolConstants.MIN_BREAK_THRESHOLD
                || breakThreshold > GenerativeSymbolConstants.MAX_BREAK_THRESHOLD) {
            LOGGER.error("breakThreshold for generative symbol exceeds range, {}", breakThreshold);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }
    
    public static boolean verifyPieces(int pieces, Optional<Integer> graphSize) {
        if (pieces < GenerativeSymbolConstants.MIN_PIECES) {
            LOGGER.error("pieces for generative symbol is below range, {}", pieces);
            return Boolean.FALSE;
        }
        if (pieces > graphSize.orElse(GenerativeSymbolConstants.MAX_GRAPH_SIZE)) {
            LOGGER.error("pieces for generative symbol exceeds range, {}", pieces);
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

}
