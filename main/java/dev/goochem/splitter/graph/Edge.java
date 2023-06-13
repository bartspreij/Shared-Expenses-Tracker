package dev.goochem.splitter.graph;

public class Edge {
    private String src, dst;
    private double weight;

    public Edge(String src, String dst, double weight) {
        this.src = dst;
        this.dst = dst;
        this.weight = weight;
    }

    public String getDst() {
        return dst;
    }

    public double getWeight() {
        return weight;
    }

    @Override
    public String toString() {
        return "Edge {" +
                "v = " + src +
                ", w = " + dst +
                ", weight = " + weight +
                "}";
    }
}
