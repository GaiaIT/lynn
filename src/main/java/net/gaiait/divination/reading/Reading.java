package net.gaiait.divination.reading;

import java.time.LocalDateTime;

public interface Reading {

    String getSubject();

    Hexagram getHexagram();
    
    String getDiviner();
    
    LocalDateTime getTime();

}
