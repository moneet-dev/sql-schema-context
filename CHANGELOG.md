# Changelog

All notable changes to this project are documented here.

---

## v0.4 – Minimal Multi-Vendor Metadata Support

### Added

- `DatabaseVendor` enum for vendor detection
- Vendor resolution via `DatabaseMetaData#getDatabaseProductName()`
- Schema resolution logic:
  - PostgreSQL → public
  - MySQL → uses catalog
  - SQL Server → dbo
  - Oracle → current user
- Catalog + schema passed to all extractors
- Identifier normalization using JDBC metadata flags
- Multi-vendor compatibility for:
  - Table extraction
  - Column extraction
  - Primary key extraction
  - Foreign key extraction
  - Index extraction

### Improved

- Graph consistency across different JDBC drivers
- Reduced risk of identifier mismatch due to case handling
- Cleaner extraction flow in `JdbcSchemaMetadataSource`

### Architectural Decisions

- No dialect abstraction layer introduced
- No plugin mechanism added
- Vendor handling remains minimal and localized
- Design remains metadata-only

### Notes

Multi-vendor support validated against:

- SQLite
- PostgreSQL
- MySQL

Docker/Testcontainers not required for local development.

---

## v0.3 – Graph Enhancements

- Edge-based `SchemaGraph`
- Directional traversal support
- BFS + DFS strategies
- Shortest path detection
- Join path formatting
- Index-aware join quality hints
- Level grouping API
- Distance map API

---

## v0.2 – Context Engine

- `FullSchemaStrategy`
- `FocusedSchemaStrategy`
- Depth-controlled traversal
- Context generation engine

---

## v0.1 – Core Extraction + DFS

- JDBC metadata extraction
- Domain modeling
- Basic graph building
- DFS traversal