# data.graph — Code Review & Roadmap

## What it is

Three files (162 lines total) sketching a property graph in the style of Neo4j:
nodes hold typed data, carry named labels, store key-value properties, and link to other
nodes via named directed edges (relations). The `Graph<T>` class is a container over a
`Set<Node<T>>` with convenience factory methods and a `connect()` helper.

```
data/graph/
├── Graph.kt    (95 lines — ~55 are commented out)
├── Node.kt     (69 lines)
└── Property.kt (13 lines)
```

---

## Current state of each file

### `Node.kt` — 75% usable

The core is sound. Relations work: you can add typed edges (`addRelation(name, node)`),
query by name (`neighbours(relation)`), and check existence (`hasRelation`). Labels work.
`allNeighbours` and the three `neighbours()` overloads are clean.

What's missing:
- No `removeRelation`. You can add edges but never remove them.
- `properties` is a public `mutableMapOf` with no access control — any caller can mutate it
  directly, bypassing `addProperty`/`removeProperty`.
- `connect()` is defined both as a method on `Graph` (line 82) **and** as a top-level
  extension on `Node` (line 66 of Node.kt). They do the same thing. One should be deleted.

### `Graph.kt` — ~30% usable, 55 lines commented out

What's live:
- `nodes: MutableSet<Node<T>>` — the store
- `addNode`, `addAll`, `removeNodes` — basic mutations
- `withLabels(vararg labels)` — filter by label set (works, O(n) scan)
- `node(data)` / `node(data, label)` — factory shortcuts
- `connect(from, to, relation, twoWay)` — delegates to `Node.addRelation`

What's commented out (and therefore absent):
- `propertyMap` — the reverse index from property name to node set (enables fast property
  queries like "all nodes that have property X")
- `thatHaveProperties()` — query by property (commented, body also left blank)
- Label reverse index (`labels: Map<String, Set<Node>>`) — `withLabels` currently does a
  full scan of `nodes` because this index is gone
- `addPropertyToNode` / `removePropertyFromNode` — coordinated property+index mutations

**Result:** without the reverse indices, every query is O(n). The graph is currently a
glorified `HashSet<Node<T>>` with a connect helper.

### `Property.kt` — completely unusable

```kotlin
sealed class Property {
    sealed class GenericTypedProperty<T>(...) : Property() {
        sealed class StringProperty(...) : GenericTypedProperty<String>(...)
        sealed class IntProperty(...)    : GenericTypedProperty<Int>(...)
        sealed class VectorProperty(...) : GenericTypedProperty<ImmutableVector2>(...)
        sealed class BoolProperty(...)   : GenericTypedProperty<Boolean>(...)
    }
}
```

Every leaf class is `sealed`. `sealed` classes can only be subclassed in the same package,
and none are subclassed anywhere. **You cannot instantiate any `Property` at all.** The type
hierarchy compiles but produces zero usable values. This is either a mistake (should be
`data class`) or an abandoned design that was never completed.

---

## Relation to TurboFactsOfTheWorld

`TurboFactsOfTheWorld` (`turbofacts/TurboFactsOfTheWorld.kt`, 281 lines) is the production
facts system. It is a flat key-value store keyed by dot-joined strings:

```kotlin
factsOfTheWorld().setBooleanFact(true, "player", "met", "captain")
// stored as key "player.met.captain"
```

Supported types: `Boolean`, `Int`, `Float`, `String`, `StringList`.  
Supports wildcard queries (`factsFor("player", "*", "captain")`).  
Has an `onFactUpdated` callback for reactive updates and a `silent {}` block for bulk writes.  
Used throughout `story/`, `turbofacts/`, `screens/`.

**The graph is not a replacement for TurboFactsOfTheWorld — they solve different problems:**

| | TurboFactsOfTheWorld | data.graph |
|---|---|---|
| Shape | Flat key → value store | Nodes + named edges |
| Query | String prefix/wildcard | Traversal + label filter |
| Use case | "Did player do X?", "How many kills?" | "Who does the player know?", "What's connected to this place?" |
| Status | Active, used everywhere | Dead code, zero usages |

The graph would be complementary, not a replacement. Facts are global state signals. A
graph would represent entity relationships (NPCs who know each other, locations connected by
roads, factions owning districts). These are different data shapes.

---

## Problems to fix before using this

### 1. `Property` is instantiable-zero — change `sealed` to `data class`

```kotlin
// Current (broken):
sealed class StringProperty(override val name: String, override var value: String)
    : GenericTypedProperty<String>(name, value)

// Should be:
data class StringProperty(override val name: String, override var value: String)
    : GenericTypedProperty<String>(name, value)
```

Same for `IntProperty`, `VectorProperty`, `BoolProperty`. Until this is fixed, the entire
Property system is compile-time decoration with no runtime meaning.

### 2. Restore or delete the commented-out indices

The commented-out `propertyMap` and `labels` reverse indices are what make a property graph
useful. Without them, `withLabels` is O(n) and property queries don't exist at all.

Either restore them (consistent `add`/`remove` paths, coordinated with `addNode`/`removeNode`)
or delete the comments and accept that this is a minimal label-graph with no property queries.

### 3. Remove the duplicate `connect()` 

`Graph.connect(from, to, ...)` and the extension `Node<T>.connect(to, ...)` do the same
thing. Pick one and delete the other. The Graph-level version is more discoverable; the
Node-level extension is more ergonomic for chaining. Either is fine.

### 4. Add `removeRelation`

You can add edges but never remove them. For a graph representing dynamic world state
(relationships that change as the story progresses), this is a significant gap.

### 5. Protect `Node.properties`

`properties` is `val properties = mutableMapOf<String, Property>()` — fully public and
mutable. Callers can bypass `addProperty`/`removeProperty` (and any future index sync).
Change to `private val _properties` with a read-only `val properties: Map<String, Property>`
accessor.

---

## Roadmap

### Option A — Complete it as a relationship graph

If the intent is to represent world relationships (NPC knows player, faction owns district,
road connects two places), this is worth building out:

1. Fix `Property` leaf classes (`sealed` → `data class`)
2. Restore `propertyMap` and `labels` reverse indices in `Graph`
3. Add `removeRelation` to `Node`
4. Protect `properties` behind a read-only accessor
5. Remove duplicate `connect()`
6. Write one concrete usage (e.g. `WorldGraph` wrapping `Graph<String>` for NPC relationships)

This is ~2–3 hours of work and produces something genuinely useful alongside
TurboFactsOfTheWorld.

### Option B — Delete it

If relationships between entities are currently handled by other means (ECS components,
TurboFacts string keys), and there's no near-term story/world feature that needs graph
traversal, delete all three files. The code is unused, `Property` is broken, and half of
`Graph` is commented out. It costs more in confusion than it saves.

---

## Recommendation

**Delete it unless you have a specific feature in mind.** The bones are reasonable but the
code is in a broken intermediate state (unusable Property type, commented-out indices, no
usages). If a future story feature needs NPC relationship tracking or world connectivity
("connected to", "allied with", "aware of"), revive it then — the `Node`/`Graph` shape is
a good starting point and would take only a few hours to make production-ready.
