package dev.moneet.schema.context;

import dev.moneet.schema.domain.*;
import dev.moneet.schema.graph.SchemaGraph;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class FocusedSchemaStrategy implements ContextStrategy {

    private final String targetTable;
    private final int depth;
    private final SchemaFormatter formatter = new SchemaFormatter();

    public FocusedSchemaStrategy(String targetTable, int depth) {
        this.targetTable = targetTable;
        this.depth = depth;
    }

    @Override
    public String generate(DatabaseSchema schema, SchemaGraph graph) {

        Set<String> relevantTables =
                graph.getSubgraph(targetTable, depth);

        List<Table> selected = new ArrayList<>();

        for (Table table : schema.getTables()) {
            if (relevantTables.contains(table.getName())) {
                selected.add(table);
            }
        }

        return formatter.format(selected);
    }
}