package net.gaiait.divination.symbol;

import java.util.Optional;
import java.util.OptionalLong;

import net.gaiait.divination.reading.ReadingRequest;

public interface SymbolRequest extends ReadingRequest {
    
    ReadingRequest getReadingRequest();

    Optional<SymbolConfig> getSymbolConfig();
    
    OptionalLong getExpiration();

}
