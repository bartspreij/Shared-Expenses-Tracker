package dev.goochem.splitter.graph;

import dev.goochem.splitter.graph.Vertex;
import lombok.Data;

@Data
public class Edge {
    private Vertex start;
    private Vertex end;
    private Double weight;

    public Edge(Vertex startV, Vertex endV, Double inputWeight) {
        this.start = startV;
        this.end = endV;
        this.weight = inputWeight;
    }
}
