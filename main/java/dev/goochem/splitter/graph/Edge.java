package dev.goochem.splitter.graph;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class Edge {
    private final int from;
    int to;
    Edge residual;
    private BigDecimal flow;
    private final BigDecimal capacity;

    public Edge(int from, int to, BigDecimal capacity) {
        this.from = from;
        this.to = to;
        this.capacity = capacity;
        this.flow = BigDecimal.ZERO;
    }

    public boolean isResidual() {
        return capacity.compareTo(BigDecimal.ZERO) == 0;
    }

    public BigDecimal remainingCapacity() {
        return capacity.subtract(flow);
    }

    public void augment(BigDecimal bottleNeck) {
        flow = flow.add(bottleNeck);
        residual.flow = residual.flow.subtract(bottleNeck);
    }

    public String toString(int s, int t) {
        String u = (from == s) ? "s" : ((from == t) ? "t" : String.valueOf(from));
        String v = (to == s) ? "s" : ((to == t) ? "t" : String.valueOf(to));
        return String.format(
                "Edge %s -> %s | flow = %s | capacity = %s | is residual: %s",
                u, v, flow, capacity, isResidual());
    }
}
