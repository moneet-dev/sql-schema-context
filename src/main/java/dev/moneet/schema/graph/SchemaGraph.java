package dev.moneet.schema.graph;

import java.util.*;

public final class SchemaGraph {

    private final Map<String, Set<String>> dependencies;
    private final Map<String, Set<String>> dependents;

    public SchemaGraph(Map<String, Set<String>> dependencies,
                       Map<String, Set<String>> dependents) {

        this.dependencies = dependencies;
        this.dependents = dependents;
    }

    // ---------- Basic Accessors ----------

    public Set<String> getDependencies(String table) {
        return dependencies.getOrDefault(table, Set.of());
    }

    public Set<String> getDependents(String table) {
        return dependents.getOrDefault(table, Set.of());
    }

    public Set<String> getTables() {
        return Collections.unmodifiableSet(dependencies.keySet());
    }

    // ---------- Root & Leaf ----------

    public Set<String> getRootTables() {
        Set<String> roots = new LinkedHashSet<>();
        for (String table : dependencies.keySet()) {
            if (dependencies.get(table).isEmpty()) {
                roots.add(table);
            }
        }
        return roots;
    }

    public Set<String> getLeafTables() {
        Set<String> leaves = new LinkedHashSet<>();
        for (String table : dependents.keySet()) {
            if (dependents.get(table).isEmpty()) {
                leaves.add(table);
            }
        }
        return leaves;
    }

    // ---------- Traversal ----------

    public Set<String> getSubgraph(String start, int depth) {
        return traverse(start, depth, new DfsTraversalStrategy());
    }

    public Set<String> traverse(String start,
                                int depth,
                                TraversalStrategy strategy) {
        return strategy.traverse(start, depth, this);
    }

    // ---------- BFS Level Grouping ----------

    public Map<Integer, Set<String>> getLevels(String start) {

        Map<Integer, Set<String>> levels = new LinkedHashMap<>();
        Set<String> visited = new LinkedHashSet<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(start);
        visited.add(start);

        int depth = 0;

        while (!queue.isEmpty()) {

            int size = queue.size();
            Set<String> currentLevel = new LinkedHashSet<>();

            for (int i = 0; i < size; i++) {

                String current = queue.poll();
                currentLevel.add(current);

                for (String neighbor : getDependencies(current)) {
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }

                for (String neighbor : getDependents(current)) {
                    if (visited.add(neighbor)) {
                        queue.add(neighbor);
                    }
                }
            }

            levels.put(depth, currentLevel);
            depth++;
        }

        return levels;
    }

    // ---------- Distance Map ----------

    public Map<String, Integer> getDistances(String start) {

        Map<String, Integer> distances = new LinkedHashMap<>();
        Queue<String> queue = new LinkedList<>();

        queue.add(start);
        distances.put(start, 0);

        while (!queue.isEmpty()) {

            String current = queue.poll();
            int currentDistance = distances.get(current);

            for (String neighbor : getDependencies(current)) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, currentDistance + 1);
                    queue.add(neighbor);
                }
            }

            for (String neighbor : getDependents(current)) {
                if (!distances.containsKey(neighbor)) {
                    distances.put(neighbor, currentDistance + 1);
                    queue.add(neighbor);
                }
            }
        }

        return distances;
    }

    // ---------- Tree Rendering ----------

    public String toTree() {

        StringBuilder sb = new StringBuilder();

        for (String root : getRootTables()) {
            buildTree(root, sb, "", new LinkedHashSet<>());
        }

        return sb.toString();
    }

    private void buildTree(String current,
                           StringBuilder sb,
                           String indent,
                           Set<String> path) {

        sb.append(indent).append(current).append("\n");

        if (path.contains(current)) {
            sb.append(indent).append("  (cycle detected)\n");
            return;
        }

        Set<String> newPath = new LinkedHashSet<>(path);
        newPath.add(current);

        for (String dependent : getDependents(current)) {
            buildTree(dependent, sb, indent + "  ", newPath);
        }
    }

    @Override
    public String toString() {
        return "SchemaGraph{" +
                "dependencies=" + dependencies +
                '}';
    }
}