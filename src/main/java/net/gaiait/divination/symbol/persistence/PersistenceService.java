package net.gaiait.divination.symbol.persistence;

import java.util.Optional;

public interface PersistenceService {
    
    Optional<PersistedSymbol> save(PersistedSymbol symbol);

}
