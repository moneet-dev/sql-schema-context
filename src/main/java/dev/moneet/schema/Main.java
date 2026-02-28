package dev.moneet.schema;

import dev.moneet.schema.context.*;
import dev.moneet.schema.domain.DatabaseSchema;
import dev.moneet.schema.graph.*;
import dev.moneet.schema.jdbc.JdbcSchemaMetadataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class Main {

    public static void main(String[] args) throws Exception {

        banner();

        Connection conn = DriverManager.getConnection("jdbc:sqlite::memory:");
        createProductionDatabase(conn);

        System.out.println("\n1. LOADING SCHEMA...");
        DatabaseSchema schema =
                new JdbcSchemaMetadataSource(conn, null).load();

        System.out.println("\n2. BUILDING GRAPH...");
        SchemaGraph graph =
                new SchemaGraphBuilder().build(schema);

        displaySchema(schema);
        displayGraphInsights(graph);

        System.out.println("\n3. CONTEXT GENERATION...");
        demonstrateContextStrategies(schema, graph);

        conn.close();
        System.out.println("\nDemo complete.");
    }

    // ---------------------------------------------------
    // Banner
    // ---------------------------------------------------

    private static void banner() {
        System.out.println("========================================");
        System.out.println("   Schema Context Engine (v0.2 Demo)");
        System.out.println("========================================");
    }

    // ---------------------------------------------------
    // Schema Creation
    // ---------------------------------------------------

    private static void createProductionDatabase(Connection conn) throws Exception {

        try (Statement stmt = conn.createStatement()) {

            stmt.execute("""
                CREATE TABLE companies (
                    id INTEGER PRIMARY KEY,
                    name TEXT NOT NULL
                )
            """);

            stmt.execute("""
                CREATE TABLE users (
                    id INTEGER PRIMARY KEY,
                    company_id INTEGER NOT NULL,
                    email TEXT NOT NULL,
                    FOREIGN KEY(company_id) REFERENCES companies(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE accounts (
                    id INTEGER PRIMARY KEY,
                    company_id INTEGER NOT NULL,
                    FOREIGN KEY(company_id) REFERENCES companies(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE orders (
                    id INTEGER PRIMARY KEY,
                    user_id INTEGER NOT NULL,
                    account_id INTEGER,
                    FOREIGN KEY(user_id) REFERENCES users(id),
                    FOREIGN KEY(account_id) REFERENCES accounts(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE products (
                    id INTEGER PRIMARY KEY,
                    company_id INTEGER NOT NULL,
                    FOREIGN KEY(company_id) REFERENCES companies(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE order_items (
                    id INTEGER PRIMARY KEY,
                    order_id INTEGER NOT NULL,
                    product_id INTEGER NOT NULL,
                    FOREIGN KEY(order_id) REFERENCES orders(id),
                    FOREIGN KEY(product_id) REFERENCES products(id)
                )
            """);
        }

        System.out.println("✓ In-memory database created.");
    }

    // ---------------------------------------------------
    // Schema Display
    // ---------------------------------------------------

    private static void displaySchema(DatabaseSchema schema) {

        System.out.println("\nSCHEMA TABLES:");
        System.out.println("----------------------------------------");

        schema.getTables().forEach(table ->
                System.out.println(" - " + table.getName())
        );
    }

    // ---------------------------------------------------
    // Graph Insights
    // ---------------------------------------------------

    private static void displayGraphInsights(SchemaGraph graph) {

        System.out.println("\nDEPENDENCY TREE:");
        System.out.println("----------------------------------------");
        System.out.println(graph.toTree());

        System.out.println("DFS (orders, depth 2):");
        System.out.println(
                graph.traverse("orders", 2,
                        new DfsTraversalStrategy())
        );

        System.out.println("\nBFS (orders, depth 2):");
        System.out.println(
                graph.traverse("orders", 2,
                        new BfsTraversalStrategy())
        );

        System.out.println("\nLEVEL GROUPING (orders):");
        graph.getLevels("orders").forEach((level, tables) ->
                System.out.println("Level " + level + " → " + tables)
        );

        System.out.println("\nDISTANCES (orders):");
        graph.getDistances("orders").forEach((table, dist) ->
                System.out.println(table + " → " + dist)
        );
    }

    // ---------------------------------------------------
    // Context Demonstration
    // ---------------------------------------------------

    private static void demonstrateContextStrategies(DatabaseSchema schema,
                                                     SchemaGraph graph) {

        printContext("FULL SCHEMA",
                new FullSchemaStrategy(),
                schema, graph);

        printContext("FOCUSED (orders, depth 1, DFS)",
                new FocusedSchemaStrategy("orders", 1),
                schema, graph);

        printContext("FOCUSED (orders, depth 2, BFS)",
                new FocusedSchemaStrategy(
                        "orders",
                        2,
                        new BfsTraversalStrategy()
                ),
                schema, graph);
    }

    private static void printContext(String title,
                                     ContextStrategy strategy,
                                     DatabaseSchema schema,
                                     SchemaGraph graph) {

        System.out.println("\n" + title);
        System.out.println("----------------------------------------");

        SchemaContextEngine engine =
                new SchemaContextEngine(strategy);

        System.out.println(engine.generate(schema, graph));
    }
}