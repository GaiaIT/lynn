package net.gaiait.divination.symbol.persistence;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import net.gaiait.divination.symbol.Symbol;
import net.gaiait.divination.symbol.SymbolRequest;

/**
 * Symbol data is stored in Redis thusly:
 * 
 * symbol:count - an int counter used to create symbol idNumbers
 * symbol:{idNumber} - A symboldata object, containing D3 json, subject, and url.
 * 
 * gallery:count - an int counter used to create reading idNumbers
 * gallery:{idNumber} - A set containing symbol objects
 * gallery:names:{setName} - A key/value that points to a gallery, e.g. g:123
 * 
 */
@Service
final class CacheServiceImpl implements CacheService {
    
    private static final Logger LOGGER = LogManager.getLogger(CacheServiceImpl.class);
    
    private static final String SYMBOL_COUNT = "symbols:count";
    private static final String SYMBOL_KEY_BASE = "symbols:symbol:";
    
    @Autowired private RedisTemplate<String, PersistedSymbolEntity> redisSymbolData;
    
    @Override
    public Function<Symbol, Optional<PersistedSymbol>> cacheByReq(SymbolRequest req) {
        return s -> cacheSymbol(s, Optional.ofNullable(req));
    }
    
    @Override
    public Optional<PersistedSymbol> getSymbolById(long id) {
        String key = buildSymbolKey(id);
        return getSymbol(key);
    }
    
    @Override
    public boolean symbolExists(long id) {
        String key = buildSymbolKey(id);
        return symbolExists(key);
    }
    
    @Override
    public List<PersistedSymbol> getRandomSymbols(OptionalInt count) {
        String pattern = SYMBOL_KEY_BASE + "*";
        OptionalInt symbolCount = count == null ? OptionalInt.empty() : count;
        
        return getSymbols(pattern, symbolCount.orElse(50));
    }
    
    private Optional<PersistedSymbol> cacheSymbol(Symbol symbol, Optional<SymbolRequest> req) {
        long id = incrementSymbolCount();
        String key = buildSymbolKey(id);
        String url = buildSymbolUrl(id);

        try {
            PersistedSymbolEntity.Builder b = new PersistedSymbolEntity.Builder(symbol).url(url).id(id);
            req.ifPresent(r -> 
                r.getExpiration().ifPresent(b::expiration)        
            );          
            PersistedSymbolEntity persisted = b.build();
            cacheSymbol(key, persisted, persisted.getExpiration(), persisted.getUnit());

            return Optional.of(persisted);
        } catch (IllegalArgumentException e) {
            LOGGER.error("Failure persisting symbol", e);
            return Optional.empty();
        }
    }
    
    private static String buildSymbolKey(long id) {
        return SYMBOL_KEY_BASE + id;
    }
    
    private static String buildSymbolUrl(long id) {
        return "/fortune/" + id;
    }
    
    /**
     * Redis dependent methods!
     * 
     */
     
    private long incrementSymbolCount() {
        return redisSymbolData.opsForValue().increment(SYMBOL_COUNT, 1);
    }
    
    private void cacheSymbol(String key, PersistedSymbolEntity data, long timeout, TimeUnit unit) {
        validateKey(key, data, timeout);
        redisSymbolData.opsForValue().set(key, data, timeout, unit);
    }
    
    private Optional<PersistedSymbol> getSymbol(String key) {
        if (key == null) {
            LOGGER.error("Unable to get persisted symbol for empty key");
            return Optional.empty();
        }
        return Optional.ofNullable(redisSymbolData.opsForValue().get(key));
    }
    
    private List<PersistedSymbol> getSymbols(String pattern, int count) {
        if (pattern == null) {
            LOGGER.error("Unable to get persisted symbols for empty key");
            return new LinkedList<>();
        }
        Set<String> symbolKeys = redisSymbolData.keys(pattern).stream().limit(count).collect(Collectors.toSet());
        return new LinkedList<>(redisSymbolData.opsForValue().multiGet(symbolKeys));
    }
    
    private boolean symbolExists(String key) {
        return redisSymbolData.hasKey(key);
    }

    private static void validateKey(String key, Object value, long expiration) {
        if (key == null || value == null || expiration <= 0) {
            LOGGER.error("Invalid arguments to save object");
            throw new IllegalArgumentException();
        }
    }

}
