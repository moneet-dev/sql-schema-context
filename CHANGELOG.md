# Changelog

## [0.2.0] - Distance-aware Traversal

### Added
- TraversalStrategy abstraction
- BfsTraversalStrategy
- Level grouping API (getLevels)
- Distance API (getDistances)

### Refactored
- Extracted DFS logic into DfsTraversalStrategy

### Notes
- No breaking changes
- Existing getSubgraph() behavior preserved

## [0.3.0] - Edge-Based Graph & Shortest Path

### Added
- GraphEdge class representing FK relationships
- Edge-aware SchemaGraph implementation
- Shortest path reconstruction via getShortestPathEdges()
- Join path foundation for SQL guidance
- Directional traversal support:
    - DEPENDENCIES_ONLY
    - DEPENDENTS_ONLY
    - BIDIRECTIONAL

### Refactored
- Replaced adjacency map (table → table) with edge-based graph
- Updated traversal logic to operate on GraphEdge
- Preserved backward-compatible APIs:
    - getDependencies()
    - getDependents()
    - getSubgraph()
    - traverse()

### Improved
- Deterministic traversal ordering
- Semantic graph reasoning
- Explicit FK-based join reconstruction

### Notes
- No breaking public API changes
- Internal graph representation refactored
- Traversal strategies remain compatible