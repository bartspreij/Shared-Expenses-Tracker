package dev.goochem.splitter.graph;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class Graph {

    private List<Vertex> vertices;
    private boolean isWeighted;
    private boolean isDirected;

    public Graph(boolean inputIsWeighted, boolean inputIsDirected) {
        this.vertices = new ArrayList<>();
        this.isWeighted = inputIsWeighted;
        this.isDirected = inputIsDirected;
    }

    public Vertex addVertex(String data) {
        Vertex newVertex = new Vertex(data);
        vertices.add(newVertex);
        return newVertex;
    }

    public void addEdge(Vertex vertex1, Vertex vertex2, Double weight) {
        if (!isWeighted) {
            weight = null;
        }
        vertex1.addEdge(vertex2, weight);
        if (!isDirected) {
            vertex2.addEdge(vertex1, weight);
        }
    }

    public void removeEdge(Vertex vertex1, Vertex vertex2) {
        vertex1.removeEdge(vertex2);
        if (!isDirected) {
            vertex2.removeEdge(vertex1);
        }
    }

    public void removeVertex(Vertex vertex) {
        this.vertices.remove(vertex);
    }

    public List<Vertex> getVertices() {
        return vertices;
    }

    public Vertex getVertexByValue(String value) {
        for(Vertex v: vertices) {
            if (v.getData().equals(value)) {
                return v;
            }
        }

        return null;
    }

    public void print() {
        for(Vertex v: vertices) {
            v.print(isWeighted);
        }
    }
}
