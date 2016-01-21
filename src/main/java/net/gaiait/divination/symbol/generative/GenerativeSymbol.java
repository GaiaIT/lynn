package net.gaiait.divination.symbol.generative;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.apache.commons.math3.random.RandomDataGenerator;
import org.apache.commons.math3.random.Well19937c;

import com.fasterxml.jackson.annotation.JsonIgnore;

import net.gaiait.divination.reading.Hexagram;
import net.gaiait.divination.reading.Reading;
import net.gaiait.divination.symbol.Symbol;
import net.gaiait.divination.symbol.generative.config.EmptyGenerativeConfig;
import net.gaiait.divination.symbol.generative.config.GenerativeConfig;

final class GenerativeSymbol implements Symbol {
    
    private final RandomDataGenerator lynn;
    private final Reading reading;
    
    private final int graphSize;
    private final int edgeThreshold;
    private final int nodeThreshold;
    private final int breakProbability;
    private final int breakThreshold;
    private final int pieces;

    private final List<StatefulSymbolNode> symbolNodes;
    private final D3JsonVisualImpl d3;

    private GenerativeSymbol(Reading reading, GenerativeConfig config) {
        this.reading = reading;
        this.lynn = new RandomDataGenerator(new Well19937c(
                reading.getTime().atZone(ZoneId.systemDefault()).toEpochSecond()
                + System.identityHashCode(reading.getHexagram())
                + System.identityHashCode(reading.getSubject())
                + System.identityHashCode(reading.getDiviner())));
                
        this.graphSize = config.getGraphSize().orElse(GenerativeSymbolConstants.DEFAULT_GRAPH_SIZE);
        this.edgeThreshold = config.getEdgeThreshold().orElse(GenerativeSymbolConstants.DEFAULT_EDGE_THRESHOLD);
        this.nodeThreshold = config.getNodeThreshold().orElse(GenerativeSymbolConstants.DEFAULT_NODE_THRESHOLD);
        this.breakProbability = config.getBreakProbability().orElse(GenerativeSymbolConstants.DEFAULT_BREAK_PROBABILITY);
        this.breakThreshold = config.getBreakThreshold().orElse(GenerativeSymbolConstants.DEFAULT_BREAK_THRESHOLD);
        this.pieces = config.getPieces().orElse(GenerativeSymbolConstants.DEFAULT_PIECES);
        
        this.symbolNodes = assemblePieces();
        this.d3 = D3JsonVisualImpl.newInstance(symbolNodes);
    }
    
    static GenerativeSymbol newInstanceFromConfig(Reading reading, GenerativeConfig config) {
        if (!GenerativeSymbolValidator.verifyReading(reading)) {
            throw new IllegalArgumentException();
        }
        if (!GenerativeSymbolValidator.verifyConfig(config)) {
            throw new IllegalArgumentException();
        }
        return new GenerativeSymbol(reading, config);
    }
    
    static GenerativeSymbol newDefaultInstance(Reading reading) {
        if (!GenerativeSymbolValidator.verifyReading(reading)) {
            throw new IllegalArgumentException();
        }
        return new GenerativeSymbol(reading, EmptyGenerativeConfig.newInstance());
    }
    
    /**
     * Each generative symbol consists of a graph of nodes; these are the nodes!
     * The nodes are transient since we serialize to and from json a bunch elsewhere...
     */
    class StatefulSymbolNode {
        private final String name;
        private final transient List<StatefulSymbolNode> sisterNodes;

        private StatefulSymbolNode(String name) {
            this.name = name;
            this.sisterNodes = new ArrayList<>();
        }

        public String getName() {
            return name;
        }

        @JsonIgnore
        public List<StatefulSymbolNode> getSisterNodes() {
            return Collections.unmodifiableList(sisterNodes);
        }

        private void addSisterNode(StatefulSymbolNode node) {
            sisterNodes.add(node);
        }

    }

    /**
     * Since a symbol may be divided into pieces, we randomly determine the size of each piece before creating
     * a separate graph for each piece. The total of nodes in the pieces should add up to the graphSize.
     * 1. Choose x random numbers, where x=pieces
     * 2. Sum each random number
     * 3. Divide each number by the sum
     * 4. Multiple each result by the intended graphSize. Each final number is a piece size
     * 5. Create a graph of nodes for each piece, with each graph having node total = piece size
     * 6. Collect all the pieces as a single list, thus creating the final graph of graph(s).
     * 
     */
    private List<StatefulSymbolNode> assemblePieces() {
       
       final List<Integer> sizeSeeds = IntStream.range(0, pieces)
               .map(i -> lynn.nextInt(1, 101))
               .boxed()
               .collect(Collectors.toList());
       
       final double seedTotal = sizeSeeds.stream().reduce((a, b) -> a + b).orElse(sizeSeeds.get(0));
       
       return IntStream.range(0, pieces)
               .parallel()
               .mapToObj(pieceIndex -> {
                   int pieceSize = (int) Math.round((sizeSeeds.get(pieceIndex) / seedTotal) * graphSize);
                   return getPieceGraph(pieceIndex, pieceSize);
               })
               .flatMap(Collection::stream)
               .collect(Collectors.toList());
    }
    
    /**
     * Larger symbol pieces need three existing, connected nodes to create interesting patterns, but if the
     * piece size is smaller than three, then we should just generate nodes as-is (though they'll be
     * fairly homogenous).
     */
    private List<StatefulSymbolNode> getPieceGraph(int piece, int pieceSize) {
        if (pieceSize < 3) {
            return createNodes(new ArrayList<>(), pieceSize, 0);
        }
        return createNodes(buildStarterNodes(piece), pieceSize - 3, piece);
    }
    
    /**
     * Each symbol piece needs existing starting nodes to add edges to, so the artists are kind enough to
     * lend their likenesses for the occasion!
     */
    private List<StatefulSymbolNode> buildStarterNodes(int i) {
        StatefulSymbolNode noah = new StatefulSymbolNode("Noah " + i);
        StatefulSymbolNode emily = new StatefulSymbolNode("Emily " + i);
        StatefulSymbolNode julia = new StatefulSymbolNode("Julia " + i);

        julia.addSisterNode(emily);
        julia.addSisterNode(noah);
        emily.addSisterNode(julia);
        noah.addSisterNode(julia);

        List<StatefulSymbolNode> nodes = new ArrayList<>();
        nodes.add(noah);
        nodes.add(emily);
        nodes.add(julia);
        
        return nodes;
    }

    /**
     * Symbols get built thusly: 
     * 1. Create new node.
     * 2a. Either select n=nodeThreshold existing nodes who haven't reached their edgeThreshold... 
     * OR 
     * 2b. Just choose n=breakThreshold existing nodes at random! (this is what gives us cool shapes...)
     * 3. Associate each selected node to the new node, and vice versa.
     * 4. Add new node to existing list of symbol nodes.
     * 5. Create more nodes until depth=0!
     */
    private List<StatefulSymbolNode> createNodes(List<StatefulSymbolNode> nodes, int depth, int piece) {
        if (depth == 0) {
            return nodes;
        }
        final StatefulSymbolNode newNode = new StatefulSymbolNode(piece + "_" + depth);
        final SisterRules rules = new SisterRules();

        List<StatefulSymbolNode> sisterCandidates = new ArrayList<>(nodes).stream()
                .filter(rules.getNewSisterEligibility())
                .collect(Collectors.toList());

        /**
         * This a randomly generated symbol, after all, so we shuffle to make sure the available
         * sister nodes are not just the most recently added nodes to the list.
         */
        Collections.shuffle(sisterCandidates);

        sisterCandidates.stream()
            .limit(rules.getNewSisterCount())
            .forEach(n -> {
                newNode.addSisterNode(n);
                n.addSisterNode(newNode);
            });
        nodes.add(newNode);

        return createNodes(nodes, depth - 1, piece);
    }

    /**
     * Rules to determine how a new will be allowed to connect to the existing nodes. This is
     * purposely random given the class' breakProbability; the interesting shape of the
     * ReadingSymbol comes from when we occasionally allow a node to connect to a single other node,
     * regardless of that node's count of existing edges.
     * 
     */
    private class SisterRules {
        private final int newSisterCount;
        private final Predicate<StatefulSymbolNode> newSisterEligibility;

        private SisterRules() {
            boolean breakRules = lynn.nextInt(0, GenerativeSymbolConstants.MAX_BREAK_PROBABILITY) <= breakProbability;
            this.newSisterEligibility = newSisterPredicate(breakRules);
            this.newSisterCount = breakRules ? breakThreshold : nodeThreshold;          
        }

        private int getNewSisterCount() {
            return newSisterCount;
        }

        private Predicate<StatefulSymbolNode> getNewSisterEligibility() {
            return newSisterEligibility;
        }
        
        private Predicate<StatefulSymbolNode> newSisterPredicate(boolean breakRules) {
            if (breakRules) {
                return (StatefulSymbolNode n) -> true;
            } else {
                return (StatefulSymbolNode n) -> n.getSisterNodes().size() < edgeThreshold;
            }
        }

    }

    /**
     * boring getters
     */

    @Override
    public String getSubject() {
        return reading.getSubject();
    }

    @Override
    public Hexagram getHexagram() {
        return reading.getHexagram();
    }
    
    @Override
    public String getDiviner() {
        return reading.getDiviner();
    }

    @Override
    public LocalDateTime getTime() {
        return reading.getTime();
    }

    @Override
    public Map<String, Object> getJson() {
        return d3.getJson();
    }

    List<StatefulSymbolNode> getSymbolNodes() {
        return Collections.unmodifiableList(symbolNodes);
    }

    D3JsonVisualImpl getD3Data() {
        return d3;
    }
    
    int getGraphSize() {
        return graphSize;
    }
    
    int getEdgeThreshold() {
        return edgeThreshold;
    }
    
    int getNodeThreshold() {
        return nodeThreshold;
    }
    
    int getBreakProbability() {
        return breakProbability;
    }
    
    int getBreakThreshold() {
        return breakThreshold;
    }
    
    int getPieces() {
        return pieces;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("GenerativeSymbol [lynn=");
        if (lynn != null && reading != null && reading.getHexagram() != null && reading.getHexagram().getNumber() > 0) {
            sb.append(lynn.nextHexString(reading.getHexagram().getNumber()) + " :D ");
        } else {
            sb.append("nay!");
        }
        sb.append(", reading=");
        sb.append(reading);
        sb.append(", graphSize=");
        sb.append(graphSize);
        sb.append(", edgeThreshold=");
        sb.append(edgeThreshold);
        sb.append(", nodeThreshold=");
        sb.append(nodeThreshold);
        sb.append(", breakProbability=");
        sb.append(breakProbability);
        sb.append(", breakThreshold=");
        sb.append(breakThreshold);
        sb.append(", pieces=");
        sb.append(pieces);
        sb.append(", symbolNodes=");
        if (symbolNodes != null && !symbolNodes.isEmpty()) {
            sb.append("yay!");
        } else {
            sb.append("nay!");
        }
        sb.append(", d3=");
        if (d3 != null) {
            sb.append("yay!");
        } else {
            sb.append("nay!");
        }
        sb.append("]");
        return sb.toString();
    }

}
