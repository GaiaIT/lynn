package net.gaiait.divination.symbol.generative.config;

import java.util.Optional;

/**
 * Convenience class for when we want a clean slate, e.g.
 * creating a GenerativeSymbol but we want it to resort to its
 * default values.
 */
public class EmptyGenerativeConfig implements GenerativeConfig {
    
    private static final EmptyGenerativeConfig EMPTY_SYMBOL_CONFIG = new EmptyGenerativeConfig();
    
    public static EmptyGenerativeConfig newInstance() {
        return EMPTY_SYMBOL_CONFIG;
    }
    
    @Override
    public Optional<String> getName() {
        return Optional.empty();
    }
    @Override
    public Optional<Integer> getGraphSize() {
        return Optional.empty();
    }
    @Override
    public Optional<Integer> getEdgeThreshold() {
        return Optional.empty();
    }
    @Override
    public Optional<Integer> getNodeThreshold() {
        return Optional.empty();
    }
    @Override
    public Optional<Integer> getBreakProbability() {
        return Optional.empty();
    }
    @Override
    public Optional<Integer> getBreakThreshold() {
        return Optional.empty();
    }
    @Override
    public Optional<Integer> getPieces() {
        return Optional.empty();
    }         
}
