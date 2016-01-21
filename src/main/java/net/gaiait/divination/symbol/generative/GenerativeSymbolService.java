package net.gaiait.divination.symbol.generative;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.function.Function;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.gaiait.divination.reading.Reading;
import net.gaiait.divination.reading.ReadingService;
import net.gaiait.divination.symbol.Symbol;
import net.gaiait.divination.symbol.SymbolConfig;
import net.gaiait.divination.symbol.SymbolRequest;
import net.gaiait.divination.symbol.SymbolService;
import net.gaiait.divination.symbol.generative.config.GenerativeConfig;
import net.gaiait.divination.symbol.persistence.CacheService;
import net.gaiait.divination.symbol.persistence.PersistedSymbol;
import net.gaiait.divination.symbol.persistence.PersistenceService;


@Service
final class GenerativeSymbolService implements SymbolService {

    private static final Logger LOGGER = LogManager.getLogger(GenerativeSymbolService.class);

    @Autowired private ReadingService readingService;
    @Autowired private CacheService cacheService;
    @Autowired private PersistenceService persistenceService;
    
    @Override
    public Optional<Symbol> createSymbol(SymbolRequest req) {
        return buildSymbol(req);
    }

    @Override
    public Optional<PersistedSymbol> createAndPersistSymbol(SymbolRequest req) {
        Function<Symbol, Optional<PersistedSymbol>> cache = cacheService.cacheByReq(req);
        return buildSymbol(req).flatMap(cache).flatMap(persistenceService::save);
    }
    
    @Override
    public Optional<PersistedSymbol> getPersistedSymbolById(long id) {
        return cacheService.getSymbolById(id);
    }
    
    @Override
    public List<PersistedSymbol> getRandomPersistedSymbols(OptionalInt count) {
        return cacheService.getRandomSymbols(count);
    }  
    
    @Override
    public boolean symbolExists(long id) {
        return cacheService.symbolExists(id);
    }
    
    private Optional<Symbol> buildSymbol(SymbolRequest req) {
        try {
            return readingService.createReading(req).map(buildSymbolByReq(req));
        } catch (IllegalArgumentException e) {
            LOGGER.error("Unable to build symbol. {}", e);
            return Optional.empty();
        }
    }
    
    private Function<Reading, Symbol> buildSymbolByReq(SymbolRequest req) {
        if (!req.getSymbolConfig().isPresent()) {
            return reading -> GenerativeSymbol.newDefaultInstance(reading);
        }
        SymbolConfig config = req.getSymbolConfig().get();
        if (config instanceof GenerativeConfig) {
            return reading -> GenerativeSymbol.newInstanceFromConfig(reading, (GenerativeConfig) config);
        } else {
            LOGGER.error("Invalid config for generative symbol");
            return reading -> null;
        }
    }

}
