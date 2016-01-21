package net.gaiait.divination.reading;

import java.util.Optional;

public interface ReadingRequest {

    String getSubject();

    int getHexagramNo();
    
    Optional<String> getDiviner();

}
