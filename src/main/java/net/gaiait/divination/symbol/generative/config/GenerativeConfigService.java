package net.gaiait.divination.symbol.generative.config;

import java.util.List;
import java.util.Optional;

public interface GenerativeConfigService {
    
    Optional<GenerativeConfig> save(GenerativeConfig config);
    
    List<GenerativeConfig> getAllConfigs();

}
