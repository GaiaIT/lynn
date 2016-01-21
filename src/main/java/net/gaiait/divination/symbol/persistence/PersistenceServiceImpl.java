package net.gaiait.divination.symbol.persistence;

import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
final class PersistenceServiceImpl implements PersistenceService {
    
    private static final Logger LOGGER = LogManager.getLogger(PersistenceServiceImpl.class);
    
    @Autowired private PersistedSymbolRepository repo;

    @Override
    public Optional<PersistedSymbol> save(PersistedSymbol symbol) {
        try {
            PersistedSymbolEntity entity = PersistedSymbolEntity.newInstanceFromSymbol(symbol);
            PersistedSymbol saved = repo.save(entity);
            return Optional.ofNullable(saved);
        } catch (Exception e) {
            LOGGER.error("Error saving symbol: {}", e);
            return Optional.empty();
        }
    }

}
