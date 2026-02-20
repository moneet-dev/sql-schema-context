package dev.moneet.schema.graph;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;

public final class SchemaGraph {

    private final Map<String, Set<String>> dependencies;
    private final Map<String, Set<String>> dependents;

    public SchemaGraph(Map<String, Set<String>> dependencies,
                       Map<String, Set<String>> dependents) {

        this.dependencies = dependencies;
        this.dependents = dependents;
    }

    public Set<String> getDependencies(String table) {
        return dependencies.getOrDefault(table, Set.of());
    }

    public Set<String> getDependents(String table) {
        return dependents.getOrDefault(table, Set.of());
    }

    public Set<String> getTables() {
        return Collections.unmodifiableSet(dependencies.keySet());
    }

    public Set<String> getRootTables() {
    Set<String> roots = new HashSet<>();

    for (String table : dependencies.keySet()) {
        if (dependencies.get(table).isEmpty()) {
            roots.add(table);
        }
    }
    return roots;
    }

    public Set<String> getLeafTables() {
    Set<String> leaves = new HashSet<>();

    for (String table : dependents.keySet()) {
        if (dependents.get(table).isEmpty()) {
            leaves.add(table);
        }
    }
    return leaves;
    }

    public Set<String> getSubgraph(String start, int maxDepth) {

    Set<String> visited = new HashSet<>();
    dfs(start, maxDepth, visited);

    return visited;
  }

private void dfs(String current, int depth, Set<String> visited) {

    if (depth < 0 || visited.contains(current)) {
        return;
    }

    visited.add(current);

    for (String neighbor : getDependencies(current)) {
        dfs(neighbor, depth - 1, visited);
    }
   }

   public String toTree() {
    StringBuilder sb = new StringBuilder();

    Set<String> roots = getRootTables();

    for (String root : roots) {
        buildTree(root, sb, "", new HashSet<>());
    }

    return sb.toString();
}

private void buildTree(String current,
                       StringBuilder sb,
                       String indent,
                       Set<String> visited) {

    sb.append(indent).append(current).append("\n");

    if (visited.contains(current)) {
        sb.append(indent).append("  (cycle detected)\n");
        return;
    }

    visited.add(current);

    for (String dependent : getDependents(current)) {
        buildTree(dependent, sb, indent + "  ", visited);
    }
}



    @Override
    public String toString() {
        return "SchemaGraph{" +
                "dependencies=" + dependencies +
                '}';
    }
}
