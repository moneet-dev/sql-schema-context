package dev.moneet.schema.graph;

import java.util.List;

public final class JoinPathFormatter {

    public static String format(List<GraphEdge> edges) {

        StringBuilder sb = new StringBuilder();
        sb.append("Join Path:\n");

        for (GraphEdge edge : edges) {
            sb.append(" - ")
                    .append(edge.getFromTable())
                    .append(".")
                    .append(edge.getFromColumn())
                    .append(" = ")
                    .append(edge.getToTable())
                    .append(".")
                    .append(edge.getToColumn())
                    .append("\n");
        }

        return sb.toString();
    }
}