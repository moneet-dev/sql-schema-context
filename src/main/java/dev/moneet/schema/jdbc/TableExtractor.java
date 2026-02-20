package dev.moneet.schema.jdbc;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class TableExtractor {

    public List<String> extractTables(DatabaseMetaData metaData, String schema) {

        List<String> tables = new ArrayList<>();

        try (ResultSet rs = metaData.getTables(
                null,                 // catalog (rarely needed)
                schema,               // schema filter
                "%",                  // table name pattern
                new String[]{"TABLE"} // restrict to real tables
        )) {

            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                tables.add(tableName);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to extract tables for schema: " + schema, e);
        }

        return tables;
    }
}
