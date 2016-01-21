package net.gaiait.divination.symbol.generative.config;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GenerativeConfigController {
    
    private static final Logger LOGGER = LogManager.getLogger(GenerativeConfigController.class);

    public static final String URI_SAVE_CONFIG = "/saveConfig";
    public static final String URI_GET_CONFIGS = "/allConfigs";
    
    @Autowired private GenerativeConfigService generativeConfigService;
    @Autowired private GenerativeConfigValidator generativeConfigValidator;
    
    @RequestMapping(value = URI_SAVE_CONFIG, consumes = "application/json")
    public ConfigPojo saveConfig(@Valid @RequestBody GenerativeConfigEntity basicSymbolConfig, BindingResult result) {
        LOGGER.info("Recieved request for saving config: " + basicSymbolConfig);
        if (result.hasErrors()) {
            return null;
        }
        return generativeConfigService.save(basicSymbolConfig).map(ConfigPojo::newInstance).orElse(null);
    }
    
    @RequestMapping(value = URI_GET_CONFIGS, method = RequestMethod.GET)
    public List<ConfigPojo> getSymbol() {
        return generativeConfigService.getAllConfigs().stream().map(ConfigPojo::newInstance).collect(Collectors.toList());
    }
    
    
    /**
     * Boring class to easily serialize what we need for a front-end rendering.
     */
    static class ConfigPojo {
        private String name;
        private Integer graphSize;
        private Integer edgeThreshold;
        private Integer nodeThreshold;
        private Integer breakProbability;
        private Integer breakThreshold;
        private Integer pieces;
        
        private ConfigPojo() {}
        
        private static ConfigPojo newInstance(GenerativeConfig config) {
            ConfigPojo pojo = new ConfigPojo();
            config.getName().ifPresent(pojo::setName);
            config.getGraphSize().ifPresent(pojo::setGraphSize);
            config.getEdgeThreshold().ifPresent(pojo::setEdgeThreshold);
            config.getNodeThreshold().ifPresent(pojo::setNodeThreshold);
            config.getBreakProbability().ifPresent(pojo::setBreakProbability);
            config.getBreakThreshold().ifPresent(pojo::setBreakThreshold);
            config.getPieces().ifPresent(pojo::setPieces);
            return pojo;
        }

        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
        public Integer getGraphSize() {
            return graphSize;
        }
        public void setGraphSize(Integer graphSize) {
            this.graphSize = graphSize;
        }
        public Integer getEdgeThreshold() {
            return edgeThreshold;
        }
        public void setEdgeThreshold(Integer edgeThreshold) {
            this.edgeThreshold = edgeThreshold;
        }
        public Integer getNodeThreshold() {
            return nodeThreshold;
        }
        public void setNodeThreshold(Integer nodeThreshold) {
            this.nodeThreshold = nodeThreshold;
        }
        public Integer getBreakProbability() {
            return breakProbability;
        }
        public void setBreakProbability(Integer breakProbability) {
            this.breakProbability = breakProbability;
        }
        public Integer getBreakThreshold() {
            return breakThreshold;
        }
        public void setBreakThreshold(Integer breakThreshold) {
            this.breakThreshold = breakThreshold;
        }
        public Integer getPieces() {
            return pieces;
        }
        public void setPieces(Integer pieces) {
            this.pieces = pieces;
        }
    }
    
    @InitBinder("basicSymbolConfig")
    public void initReqBinder(WebDataBinder binder) {
        binder.setValidator(generativeConfigValidator);
    }

}
