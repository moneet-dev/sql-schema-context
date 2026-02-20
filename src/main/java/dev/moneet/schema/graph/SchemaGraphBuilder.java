package dev.moneet.schema.graph;

import dev.moneet.schema.domain.DatabaseSchema;
import dev.moneet.schema.domain.ForeignKey;
import dev.moneet.schema.domain.Table;

import java.util.*;

public final class SchemaGraphBuilder {

    public SchemaGraph build(DatabaseSchema schema) {

        Map<String, Set<String>> dependencies = new HashMap<>();
        Map<String, Set<String>> dependents = new HashMap<>();

        // Initialize all tables
        for (Table table : schema.getTables()) {
            String tableName = table.getName();

            dependencies.put(tableName, new HashSet<>());
            dependents.put(tableName, new HashSet<>());
        }

        // Build relationships
        for (Table table : schema.getTables()) {

            String sourceTable = table.getName();

            for (ForeignKey fk : table.getForeignKeys()) {

                String targetTable = fk.getReferencedTable();

                // Add dependency: source → target
                dependencies.get(sourceTable).add(targetTable);

                // Add reverse mapping: target → source
                dependents.get(targetTable).add(sourceTable);
            }
        }

        // Make immutable before returning
        Map<String, Set<String>> immutableDependencies = makeImmutable(dependencies);
        Map<String, Set<String>> immutableDependents = makeImmutable(dependents);

        return new SchemaGraph(immutableDependencies, immutableDependents);
    }

    private Map<String, Set<String>> makeImmutable(Map<String, Set<String>> map) {
        Map<String, Set<String>> result = new HashMap<>();

        for (Map.Entry<String, Set<String>> entry : map.entrySet()) {
            result.put(entry.getKey(), Set.copyOf(entry.getValue()));
        }

        return Map.copyOf(result);
    }
}
