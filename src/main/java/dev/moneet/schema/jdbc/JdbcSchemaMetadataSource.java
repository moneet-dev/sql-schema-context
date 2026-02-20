package dev.moneet.schema.jdbc;

import dev.moneet.schema.domain.*;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class JdbcSchemaMetadataSource implements SchemaMetadataSource {

    private final Connection connection;

    private final TableExtractor tableExtractor;
    private final ColumnExtractor columnExtractor;
    private final PrimaryKeyExtractor pkExtractor;
    private final ForeignKeyExtractor fkExtractor;
    private final IndexExtractor indexExtractor;
	private final String schema;

    public JdbcSchemaMetadataSource(Connection connection, String schema) {
        this.connection = connection;
		this.schema = schema;
        this.tableExtractor = new TableExtractor();
        this.columnExtractor = new ColumnExtractor();
        this.pkExtractor = new PrimaryKeyExtractor();
        this.fkExtractor = new ForeignKeyExtractor();
        this.indexExtractor = new IndexExtractor();
    }

    @Override
    public DatabaseSchema load() {
        try {
            DatabaseMetaData metaData = connection.getMetaData();

            List<String> tableNames = tableExtractor.extractTables(metaData, schema);
            List<Table> tables = new ArrayList<>();

            for (String tableName : tableNames) {

                List<Column> columns =
        				columnExtractor.extract(metaData, schema, tableName);

                var primaryKeys =
                        pkExtractor.extract(metaData, schema, tableName);

                var foreignKeys =
                        fkExtractor.extract(metaData, schema, tableName);

                var indexes =
                        indexExtractor.extract(metaData, schema, tableName);

                Table table = new Table(
                        tableName,
                        columns,
                        primaryKeys,
                        foreignKeys,
                        indexes
                );

                tables.add(table);
            }

            return new DatabaseSchema(tables);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load schema metadata", e);
        }
    }
}
