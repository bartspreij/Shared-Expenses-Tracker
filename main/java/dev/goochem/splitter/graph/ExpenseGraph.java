package dev.goochem.splitter.graph;

import dev.goochem.splitter.entities.Person;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/* This is a weighted directed graph.
    The purpose is to reduce the amount of transactions needed between people whom owe each other */
public class ExpenseGraph {
    private List<Node> nodes;

    public ExpenseGraph() {
        this.nodes = new ArrayList<>();
    }

    public void addNode(String data) {
        Node<String> newNode = new Node<>();
        newNode.setData(data);
        nodes.add(newNode);
    }

}
