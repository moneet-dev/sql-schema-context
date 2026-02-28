package dev.moneet.schema.graph;

import java.util.LinkedHashSet;
import java.util.Set;

public final class DfsTraversalStrategy implements TraversalStrategy {

    @Override
    public Set<String> traverse(String start, int depth, SchemaGraph graph) {
        Set<String> visited = new LinkedHashSet<>();
        dfs(start, depth, graph, visited);
        return visited;
    }

    private void dfs(String current,
                     int depth,
                     SchemaGraph graph,
                     Set<String> visited) {

        if (depth < 0 || visited.contains(current)) {
            return;
        }

        visited.add(current);

        for (String neighbor : graph.getDependencies(current)) {
            dfs(neighbor, depth - 1, graph, visited);
        }

        for (String neighbor : graph.getDependents(current)) {
            dfs(neighbor, depth - 1, graph, visited);
        }
    }
}