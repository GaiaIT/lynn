package net.gaiait.divination.symbol.generative;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import net.gaiait.divination.symbol.JsonVisual;

/**
 * SimpleSymbolD3 contains: {
 * 
 * positionMap: a map of cell name -> index within the nodes array
 * d3data: a map of...
 * ---- nodes: [node1, node2, node3...]
 * ---- rels: [{source:n, target:m}...]
 * 
 * example: {positionMap=[{Julia=1}, {Emily=2}, {Noah=0}], 
 * d3data={nodes=[Node[2], Node[1], Node[0]],
 * rels=[{source=0, target=1}, {source=1, target=2}]}}
 * 
 */
final class D3JsonVisualImpl implements JsonVisual {

    public static final String NODES_FIELD = "nodes";
    public static final String RELS_FIELD = "rels";
    public static final String POSITION_MAP_FIELD = "positionMap";
    public static final String SOURCE_FIELD = "source";
    public static final String TARGET_FIELD = "target";

    private final List<GenerativeSymbol.StatefulSymbolNode> nodes;
    private final Map<String, Integer> positionMap;
    private final List<Map<String, Integer>> relations;
    private final Map<String, Object> d3Json;

    private D3JsonVisualImpl(List<GenerativeSymbol.StatefulSymbolNode> nodes) {
        this.nodes = nodes;

        this.positionMap = new HashMap<>();
        IntStream.range(0, nodes.size()).forEach(i -> positionMap.put(nodes.get(i).getName(), i));

        this.relations = new ArrayList<>();
        nodes.stream().forEach(node -> {
            final int source = positionMap.get(node.getName());
            node.getSisterNodes().stream().map(sisterNode -> {
                Map<String, Integer> result = new HashMap<>(2);
                result.put(SOURCE_FIELD, source);
                result.put(TARGET_FIELD, positionMap.get(sisterNode.getName()));
                return result;
            }).forEach(relations::add);
        });

        this.d3Json = new HashMap<>();
        d3Json.put(POSITION_MAP_FIELD, positionMap);
        d3Json.put(NODES_FIELD, nodes);
        d3Json.put(RELS_FIELD, relations);
    }

    static D3JsonVisualImpl newInstance(List<GenerativeSymbol.StatefulSymbolNode> nodes) {
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalArgumentException();
        }
        return new D3JsonVisualImpl(new ArrayList<>(nodes));
    }

    @Override
    public Map<String, Object> getJson() {
        return Collections.unmodifiableMap(d3Json);
    }

    List<GenerativeSymbol.StatefulSymbolNode> getNodes() {
        return Collections.unmodifiableList(nodes);
    }

    Map<String, Integer> getPositionMap() {
        return Collections.unmodifiableMap(positionMap);
    }

    List<Map<String, Integer>> getRelations() {
        return Collections.unmodifiableList(relations);
    }

}
