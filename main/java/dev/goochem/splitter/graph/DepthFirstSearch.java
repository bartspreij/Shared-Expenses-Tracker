package dev.goochem.splitter.graph;

import java.util.Deque;
import java.util.LinkedList;

public class DepthFirstSearch<T> {
    public void traverseRecursively(Node<T> node) {
        node.setVisited(true);
        System.out.println(node);
        node.getNeighbors().forEach(neighbor -> {
            if (!neighbor.isVisited()) {
                traverseRecursively(neighbor);
            }
        });
    }
}
