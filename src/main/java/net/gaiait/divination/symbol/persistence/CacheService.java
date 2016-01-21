package net.gaiait.divination.symbol.persistence;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import net.gaiait.divination.symbol.Symbol;
import net.gaiait.divination.symbol.SymbolRequest;

public interface CacheService {
      
    Function<Symbol, Optional<PersistedSymbol>> cacheByReq(SymbolRequest req);
    
    Optional<PersistedSymbol> getSymbolById(long id);
    
    List<PersistedSymbol> getRandomSymbols(OptionalInt count);
    
    boolean symbolExists(long id);

}
