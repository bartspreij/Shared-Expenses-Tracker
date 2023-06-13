package dev.goochem.splitter.graph;

import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Node<T> {

    private T data;
    private boolean visited;
    @ToString.Exclude
    private List<Node<T>> neighbors = new ArrayList<>();

}
