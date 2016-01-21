package net.gaiait.divination.symbol.generative;

import java.util.concurrent.TimeUnit;

import org.springframework.stereotype.Component;

@Component
public final class GenerativeSymbolConstants {

    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.DAYS;
    public static final long DEFAULT_EXPIRATION = 1;
    public static final long EXPIRATION_IN_SECONDS = DEFAULT_TIME_UNIT.toSeconds(DEFAULT_EXPIRATION);
    public static final int DEFAULT_EDGE_THRESHOLD = 7;
    public static final int DEFAULT_NODE_THRESHOLD = 7;
    public static final int DEFAULT_GRAPH_SIZE = 200;
    public static final int DEFAULT_BREAK_PROBABILITY = 10;
    public static final int DEFAULT_BREAK_THRESHOLD = 1;
    public static final int DEFAULT_PIECES = 1;

    public static final int MIN_HEXAGRAM = 1;
    public static final long MIN_EXPIRATION = 1;
    public static final int MIN_EDGE_THRESHOLD = 1;
    public static final int MIN_NODE_THRESHOLD = 1;
    public static final int MIN_GRAPH_SIZE = 3;
    public static final int MIN_BREAK_PROBABILITY = 0;
    public static final int MIN_BREAK_THRESHOLD = 1;
    public static final int MIN_PIECES = 1;

    public static final int MAX_HEXAGRAM = 64;
    public static final int MAX_EDGE_THRESHOLD = 20;
    public static final int MAX_NODE_THRESHOLD = 20;
    public static final int MAX_GRAPH_SIZE = 400;
    public static final int MAX_BREAK_PROBABILITY = 100;
    public static final int MAX_BREAK_THRESHOLD = 20;
    
    private GenerativeSymbolConstants() {}

}
