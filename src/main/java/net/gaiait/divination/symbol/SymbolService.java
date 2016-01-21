package net.gaiait.divination.symbol;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;

import net.gaiait.divination.symbol.persistence.PersistedSymbol;

public interface SymbolService {

    Optional<Symbol> createSymbol(SymbolRequest req);

    Optional<PersistedSymbol> createAndPersistSymbol(SymbolRequest req);

    Optional<PersistedSymbol> getPersistedSymbolById(long id);
    
    List<PersistedSymbol> getRandomPersistedSymbols(OptionalInt count);
    
    boolean symbolExists(long id);

}
