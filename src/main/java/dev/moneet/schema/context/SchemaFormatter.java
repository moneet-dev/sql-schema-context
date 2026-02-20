package dev.moneet.schema.context;

import dev.moneet.schema.domain.*;

import java.util.List;
import java.util.Set;

public final class SchemaFormatter {

    public String format(List<Table> tables) {

        StringBuilder sb = new StringBuilder();

        for (Table table : tables) {

            sb.append("Table ").append(table.getName()).append(":\n");

            Set<String> primaryKeys = table.getPrimaryKeys();

            for (Column column : table.getColumns()) {

                sb.append("  ").append(column.getName());

                if (primaryKeys.contains(column.getName())) {
                    sb.append(" (PK)");
                }

                table.getForeignKeys().stream()
                        .filter(fk -> fk.getColumn().equals(column.getName()))
                        .findFirst()
                        .ifPresent(fk ->
                                sb.append(" (FK -> ")
                                        .append(fk.getReferencedTable())
                                        .append(".")
                                        .append(fk.getReferencedColumn())
                                        .append(")")
                        );

                sb.append("\n");
            }

            sb.append("\n");
        }

        return sb.toString();
    }
}