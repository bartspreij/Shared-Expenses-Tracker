package dev.goochem.splitter.graph;

import lombok.Data;

import java.util.ArrayList;

@Data
public class Vertex {

    private String data;
    private ArrayList<Edge> edges;

    public Vertex(String inputData) {
        this.data = inputData;
        this.edges = new ArrayList<>();
    }

    public void addEdge(Vertex endVertex, Double weight) {
        edges.add(new Edge(this, endVertex, weight));
    }

    public void removeEdge(Vertex endVertex) {
        edges.removeIf(edge -> edge.getEnd().equals(endVertex));
    }

    public void print(boolean showWeight) {
        StringBuilder message = new StringBuilder();

        if (edges.size() == 0) {
            System.out.println(data + " -->");
            return;
        }

        for(int i = 0; i < edges.size(); i++) {
            if (i == 0) {
                message.append(edges.get(i).getStart().data).append(" -->  ");
            }

            message.append(edges.get(i).getEnd().data);

            if (showWeight) {
                message.append(" (").append(edges.get(i).getWeight()).append(")");
            }

            if (i != edges.size() - 1) {
                message.append(", ");
            }
        }
        System.out.println(message);
    }
}
