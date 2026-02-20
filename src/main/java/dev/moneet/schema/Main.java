package dev.moneet.schema;

import dev.moneet.schema.context.ContextStrategy;
import dev.moneet.schema.context.FocusedSchemaStrategy;
import dev.moneet.schema.context.FullSchemaStrategy;
import dev.moneet.schema.context.SchemaContextEngine;
import dev.moneet.schema.domain.DatabaseSchema;
import dev.moneet.schema.graph.SchemaGraph;
import dev.moneet.schema.graph.SchemaGraphBuilder;
import dev.moneet.schema.jdbc.JdbcSchemaMetadataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("   Schema Context Library Demo");
        System.out.println("========================================\n");

        // Create in-memory SQLite database with production-like schema
        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        createProductionDatabase(conn);

        // Load schema metadata
        System.out.println("1. LOADING SCHEMA FROM DATABASE...\n");
        DatabaseSchema schema = new JdbcSchemaMetadataSource(conn, null).load();

        // Display schema details
        displaySchemaDetails(schema);

        // Build dependency graph
        System.out.println("\n2. BUILDING DEPENDENCY GRAPH...\n");
        SchemaGraph graph = new SchemaGraphBuilder().build(schema);

        // Display dependency tree
        displayDependencyTree(graph);

        // Generate context queries
        System.out.println("\n3. GENERATING SCHEMA CONTEXTS...\n");
        generateContextQueries(schema, graph);

        conn.close();
        System.out.println("\n========================================");
        System.out.println("   Demo Complete");
        System.out.println("========================================");
    }

    private static void createProductionDatabase(Connection conn) throws Exception {
        System.out.println("Creating production-like schema...\n");

        try (Statement stmt = conn.createStatement()) {
            // Companies table
            stmt.execute("""
                CREATE TABLE companies (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL,
                    industry TEXT,
                    founded_year INTEGER
                )
            """);

            // Users table
            stmt.execute("""
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY,
                    company_id INTEGER NOT NULL,
                    email TEXT UNIQUE NOT NULL,
                    first_name TEXT,
                    last_name TEXT,
                    created_at TEXT,
                    FOREIGN KEY(company_id) REFERENCES companies(id)
                )
            """);

            // Accounts table
            stmt.execute("""
                CREATE TABLE accounts (
                    id INTEGER PRIMARY KEY,
                    company_id INTEGER NOT NULL,
                    account_type TEXT,
                    balance DECIMAL,
                    created_at TEXT,
                    FOREIGN KEY(company_id) REFERENCES companies(id)
                )
            """);

            // Products table
            stmt.execute("""
                CREATE TABLE products (
                    id INTEGER PRIMARY KEY,
                    company_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    price DECIMAL,
                    stock_quantity INTEGER,
                    FOREIGN KEY(company_id) REFERENCES companies(id)
                )
            """);

            // Orders table
            stmt.execute("""
                CREATE TABLE orders (
                    id INTEGER PRIMARY KEY,
                    user_id INTEGER NOT NULL,
                    account_id INTEGER,
                    order_date TEXT,
                    total_amount DECIMAL,
                    status TEXT,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(account_id) REFERENCES accounts(id)
                )
            """);

            // Order Items table
            stmt.execute("""
                CREATE TABLE order_items (
                    id INTEGER PRIMARY KEY,
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    quantity INTEGER,
                    unit_price DECIMAL,
                    FOREIGN KEY(order_id) REFERENCES orders(id),
                    FOREIGN KEY(product_id) REFERENCES products(id)
                )
            """);

            // Invoices table
            stmt.execute("""
                CREATE TABLE invoices (
                    id INTEGER PRIMARY KEY,
                    order_id INTEGER NOT NULL,
                    invoice_date TEXT,
                    due_date TEXT,
                    amount DECIMAL,
                    status TEXT,
                    FOREIGN KEY(order_id) REFERENCES orders(id)
                )
            """);
        }

        System.out.println("✓ Database created with 7 tables\n");
    }

    private static void displaySchemaDetails(DatabaseSchema schema) {
        System.out.println("SCHEMA DETAILS:");
        System.out.println("-".repeat(80));

        for (var table : schema.getTables()) {
            System.out.println("\nTable: " + table.getName());
            System.out.println("  Columns:");
            for (var column : table.getColumns()) {
                System.out.println("    - " + column.getName() + " (" + column.getType() + ")" +
                        (column.isNullable() ? " [NULLABLE]" : ""));
            }

            var primaryKeys = table.getPrimaryKeys();
            if (!primaryKeys.isEmpty()) {
                System.out.println("  Primary Keys: " + primaryKeys);
            }

            var foreignKeys = table.getForeignKeys();
            if (!foreignKeys.isEmpty()) {
                System.out.println("  Foreign Keys:");
                for (var fk : foreignKeys) {
                    System.out.println("    - " + fk.getColumn() + " -> " +
                            fk.getReferencedTable() + "(" + fk.getReferencedColumn() + ")");
                }
            }
        }

        System.out.println("\n" + "-".repeat(80));
        System.out.println("Total tables: " + schema.getTables().size());
    }

    private static void displayDependencyTree(SchemaGraph graph) {
        System.out.println("DEPENDENCY TREE:");
        System.out.println("-".repeat(80));
        System.out.println(graph.toTree());
        System.out.println("-".repeat(80));
    }

    private static void generateContextQueries(DatabaseSchema schema, SchemaGraph graph)
            throws Exception {

        // Full schema context
        System.out.println("A. FULL SCHEMA CONTEXT");
        System.out.println("-".repeat(80));
        ContextStrategy fullStrategy = new FullSchemaStrategy();
        SchemaContextEngine fullEngine = new SchemaContextEngine(fullStrategy);
        String fullContext = fullEngine.generate(schema, graph);
        System.out.println(fullContext);

        // Focused context: orders (depth 1)
        System.out.println("\n\nB. FOCUSED CONTEXT: 'orders' table (depth 1)");
        System.out.println("-".repeat(80));
        ContextStrategy ordersStrategy = new FocusedSchemaStrategy("orders", 1);
        SchemaContextEngine ordersEngine = new SchemaContextEngine(ordersStrategy);
        String ordersContext = ordersEngine.generate(schema, graph);
        System.out.println(ordersContext);

        // Focused context: products (depth 2)
        System.out.println("\n\nC. FOCUSED CONTEXT: 'products' table (depth 2)");
        System.out.println("-".repeat(80));
        ContextStrategy productsStrategy = new FocusedSchemaStrategy("products", 2);
        SchemaContextEngine productsEngine = new SchemaContextEngine(productsStrategy);
        String productsContext = productsEngine.generate(schema, graph);
        System.out.println(productsContext);

        // Focused context: users (depth 1)
        System.out.println("\n\nD. FOCUSED CONTEXT: 'users' table (depth 1)");
        System.out.println("-".repeat(80));
        ContextStrategy usersStrategy = new FocusedSchemaStrategy("users", 1);
        SchemaContextEngine usersEngine = new SchemaContextEngine(usersStrategy);
        String usersContext = usersEngine.generate(schema, graph);
        System.out.println(usersContext);
    }
}
