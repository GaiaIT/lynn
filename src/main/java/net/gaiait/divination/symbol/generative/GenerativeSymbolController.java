package net.gaiait.divination.symbol.generative;

import java.util.List;
import java.util.Map;
import java.util.OptionalInt;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import net.gaiait.divination.reading.Hexagram;
import net.gaiait.divination.symbol.Symbol;
import net.gaiait.divination.symbol.SymbolService;
import net.gaiait.divination.symbol.persistence.PersistedSymbol;

@RestController
public final class GenerativeSymbolController {

    private static final Logger LOGGER = LogManager.getLogger(GenerativeSymbolController.class);

    public static final String URI_TRANSIENT = "/symbol/transient";
    public static final String URI_PERSISTED = "/symbol/persisted";
    public static final String URI_RANDOM = "/symbol/random";

    @Autowired private SymbolService symbolService;
    @Autowired private GenerativeSymbolRequestValidator generativeSymbolRequestValidator;

    @RequestMapping(value = URI_TRANSIENT, consumes = "application/json")
    public SymbolResponse transientPost(@Valid @RequestBody GenerativeSymbolRequest req, BindingResult result) {
        LOGGER.info("Recieved post request for transient symbol: " + req);

        if (result.hasErrors()) {
            return new SymbolResponse(result.getAllErrors());
        }
        return symbolService.createSymbol(req).map(SymbolResponse::new).orElse(null);
    }

    @RequestMapping(value = URI_PERSISTED, consumes = "application/json")
    public SymbolResponse persistedPost(@Valid @RequestBody GenerativeSymbolRequest req, BindingResult result) {
        LOGGER.info("Recieved post request for persisted symbol: " + req);

        if (result.hasErrors()) {
            return new SymbolResponse(result.getAllErrors());
        }
        return symbolService.createAndPersistSymbol(req).map(SymbolResponse::new).orElse(new SymbolResponse());
    }

    @RequestMapping(value = URI_PERSISTED + "/{id}", method = RequestMethod.GET)
    public SymbolResponse persistedGet(@PathVariable long id) {
        LOGGER.info("Recieved get request for persisted symbol with id {}" + id);

        return symbolService.getPersistedSymbolById(id).map(SymbolResponse::new).orElse(null);
    }

    @RequestMapping(value = URI_RANDOM, method = RequestMethod.GET)
    public List<SymbolResponse> randomGet() {
        LOGGER.info("Recieved get request for random persisted symbols with id");

        return symbolService.getRandomPersistedSymbols(OptionalInt.empty()).stream()
                .map(SymbolResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * Boring class to easily serialize what we need for a front-end rendering.
     */
    static class SymbolResponse {
        private List<ObjectError> errors;
        private String subject;
        private Map<String, Object> hexagram;
        private Map<String, Object> d3Data;
        private String url;

        private SymbolResponse() {
            LOGGER.info("No symbol was returned");
        }

        private SymbolResponse(List<ObjectError> e) {
            LOGGER.info("Errors being returned in response: " + e);
            errors = e;
        }

        private SymbolResponse(Symbol s) {
            LOGGER.info("Symbol being returned in response: " + s);

            subject = s.getSubject();
            hexagram = s.getHexagram().getJson();
            d3Data = s.getJson();

            if (s instanceof PersistedSymbol) {
                url = ((PersistedSymbol) s).getUrl();
            }
        }

        public List<ObjectError> getErrors() {
            return errors;
        }

        public void setErrors(List<ObjectError> errors) {
            this.errors = errors;
        }

        public String getSubject() {
            return subject;
        }

        public void setSubject(String subject) {
            this.subject = subject;
        }

        public Map<String, Object> getHexagram() {
            return hexagram;
        }

        public void setHexagram(Hexagram hexagram) {
            this.hexagram = hexagram.getJson();
        }

        public Map<String, Object> getD3Data() {
            return d3Data;
        }

        public void setD3Data(Map<String, Object> d3Data) {
            this.d3Data = d3Data;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }

    @InitBinder("generativeSymbolRequest")
    public void initReqBinder(WebDataBinder binder) {
        binder.setValidator(generativeSymbolRequestValidator);
    }

}
