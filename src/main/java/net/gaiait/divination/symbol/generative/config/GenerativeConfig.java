package net.gaiait.divination.symbol.generative.config;

import java.util.Optional;

import net.gaiait.divination.symbol.SymbolConfig;

public interface GenerativeConfig extends SymbolConfig {
    
    Optional<String> getName();

    Optional<Integer> getGraphSize();

    Optional<Integer> getEdgeThreshold();

    Optional<Integer> getNodeThreshold();

    Optional<Integer> getBreakProbability();

    Optional<Integer> getBreakThreshold();
    
    Optional<Integer> getPieces();

}
