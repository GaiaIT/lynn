package net.gaiait.divination.symbol.generative.config;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class GenerativeConfigServiceImpl implements GenerativeConfigService {
    
    private static final Logger LOGGER = LogManager.getLogger(GenerativeConfigServiceImpl.class);
    
    @Autowired private GenerativeConfigRepository repo;

    @Override
    public Optional<GenerativeConfig> save(GenerativeConfig config) {
        try {
            GenerativeConfigEntity entity = GenerativeConfigEntity.newInstance(config);
            return Optional.ofNullable(repo.save(entity));
        } catch (Exception e) {
            LOGGER.error("Error saving config: {}", e);
            return Optional.empty();
        }
    }

    @Override
    public List<GenerativeConfig> getAllConfigs() {
        try {
            return new LinkedList<GenerativeConfig>(repo.findAll());
        } catch (Exception e) {
            LOGGER.error("Error saving config: {}", e);
            return new LinkedList<>();
        }
    }

}
