# Traversal Evolution (v0.2)

## Motivation

DFS provided reachability but lacked distance awareness.
To enable relevance-based context prioritization,
a traversal abstraction was introduced.

## Design Decision

Instead of modifying existing APIs:
- TraversalStrategy interface was added.
- Existing DFS logic was extracted into DfsTraversalStrategy.
- BFS was introduced as an additional strategy.

This preserves backward compatibility and keeps the graph layer extensible.