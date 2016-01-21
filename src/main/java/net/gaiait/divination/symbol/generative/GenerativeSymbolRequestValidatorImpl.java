package net.gaiait.divination.symbol.generative;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import net.gaiait.divination.reading.ReadingRequest;
import net.gaiait.divination.reading.ReadingRequestValidator;
import net.gaiait.divination.symbol.SymbolConfig;
import net.gaiait.divination.symbol.SymbolRequest;
import net.gaiait.divination.symbol.generative.config.GenerativeConfigValidator;

@Component
final class GenerativeSymbolRequestValidatorImpl implements GenerativeSymbolRequestValidator {

    public static final String NULL_ERROR = "null";
    public static final String EXPIRATION_RANGE_ERROR = "expiration.range";
    
    @Autowired private ReadingRequestValidator readingRequestValidator;
    @Autowired private GenerativeConfigValidator configValidator;

    @Override
    public boolean supports(Class<?> clazz) {
        return GenerativeSymbolRequest.class.equals(clazz);
    }

    @Override
    public void validate(Object obj, Errors e) {
        if (obj == null) {
            e.reject(NULL_ERROR);
            return;
        }
        SymbolRequest req = (SymbolRequest) obj;
        validateReadingRequest(req.getReadingRequest(), e);
        
        req.getExpiration().ifPresent(ex -> validateExpiration(ex, e));
        req.getSymbolConfig().ifPresent(sc -> validateConfig(sc, e));
    }

    private static void validateExpiration(long ex, Errors e) {
        if (ex < GenerativeSymbolConstants.DEFAULT_EXPIRATION) {
            e.rejectValue(GenerativeSymbolRequest.EXPIRATION_FIELD, EXPIRATION_RANGE_ERROR);
        }
    }
    
    private void validateReadingRequest(ReadingRequest req, Errors e) {
        readingRequestValidator.validate(req, e);
    }
    
    private void validateConfig(SymbolConfig sc, Errors e) {
        configValidator.validate(sc, e);
    }

    

}
