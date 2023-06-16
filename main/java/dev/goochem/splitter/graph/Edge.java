package dev.goochem.splitter.graph;

import lombok.Data;

@Data
public class Edge {
    private int from, to;
    private Edge residual;
    private double flow;
    private double capacity;

    public Edge(int from, int to, double capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
    }

    public boolean isResidual() {
        return capacity == 0;
    }

    public double remainingCapacity() {
        return capacity - flow;
    }

    public void augment(double bottleNeck) {
        flow += bottleNeck;
        residual.flow -= bottleNeck;
    }

    public String toString(int s, int t) {
        String u = (from == s) ? "s" : ((from == t) ? "t" : String.valueOf(from));
        String v = (to == s) ? "s" : ((to == t) ? "t" : String.valueOf(to));
        return String.format(
                "Edge %s -> %s | flow = %.2f | capacity = %.2f | is residual: %s",
                u, v, flow, capacity, isResidual());
    }
}
