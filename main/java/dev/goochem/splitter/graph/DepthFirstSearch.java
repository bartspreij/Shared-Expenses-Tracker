package dev.goochem.splitter.graph;

import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

public class DepthFirstSearch {
    public void traverseRecursively(Vertex start, List<Vertex> visitedVertices) {
        System.out.println(start.getData());

        for (Edge e : start.getEdges()) {
            Vertex neighbor = e.getEnd();

            if (!visitedVertices.contains(neighbor)) {
                visitedVertices.add(neighbor);
                traverseRecursively(neighbor, visitedVertices);
            }
        }
    }
}
