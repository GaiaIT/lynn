package net.gaiait.divination.symbol.persistence;

import java.util.concurrent.TimeUnit;

public interface PersistenceData {
    
    // We opt for Long so we easily use map...
    Long getId();

    String getUrl();
    
    long getExpiration();

    TimeUnit getUnit();

}
