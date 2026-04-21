# Testing Roadmap

The project has no automated tests today. This document prioritises areas by value/effort ratio — pure logic with no LibGDX/Box2D dependencies first, heavier integration concerns later.

## Setup

Add a `core/src/test/kotlin` source set with JUnit 5 + kotlin.test. No LibGDX headless context needed for the first two tiers below.

```kotlin
// core/build.gradle.kts
dependencies {
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.2")
}
tasks.test { useJUnitPlatform() }
```

---

## Tier 1 — Pure Logic (no mocking needed)

### TurboFacts / FactsOfTheWorld
`turbofacts/` — the rule engine is the heart of game-event logic and has zero framework dependencies.

- `TurboFactsOfTheWorld`: set/get/increment facts, check that rules fire exactly when their criteria are met, verify they don't fire twice when `fireOnce = true`.
- `Criterion`: each operator (`equals`, `greaterThan`, `lessThan`, etc.) evaluated in isolation.
- `TurboRule` / `TurboRuleBuilder`: builder produces rules with correct criteria and consequence lists.
- `Factoid` / `Factoids`: serialisation round-trip (write facts → reload → same values).
- `FactPersistence`: save/load cycle produces identical state.

### Story System
`turbofacts/StoryTextParser` + `StoryTextSerializer` — already has a text format, very testable.

- Parser: known story text → expected `TurboStory` structure (nodes, choices, consequences).
- Serializer: `TurboStory` → text → parse back → structural equality.
- Round-trip fuzz: generate a story programmatically, serialise, deserialise, compare.
- `TurboStoryManager`: advancing through a story triggers the right consequences and unlocks the right next nodes.

### Map Generation
`map/grid/GridMapGenerator` and `map/snake/SnakeMapGenerator`.

- Generated map is non-empty and within declared bounds.
- All sections are reachable (connectivity check via BFS on `MapTile` adjacency).
- `Coordinate` arithmetic: add, subtract, direction offsets.
- `TextGridMapDefinition`: parse a hand-written ASCII grid → correct tile types at known positions.
- `MapLoader`: loading a known definition file produces the expected `MapData`.

### Utility Math & Helpers
- `MapExtensions` / `TileAlignment` calculations.
- Any pure extension functions in `gamePlay/weapons/` or `gamePlay/pickups/`.

---

## Tier 2 — Logic with Lightweight Fakes

These touch interfaces but not LibGDX rendering or Box2D. Use simple fake/stub implementations.

### AI Pathfinding
`ai/pathfinding/TileGraph` — A* over a tile graph is deterministic.

- Shortest path on a simple hand-crafted grid returns known result.
- Unreachable target returns empty/null path.
- Walls/obstacles block correctly.

### Utility AI
`ai/utility/` — scoring behaviors given fake world state.

- Each `UtilityBehavior` returns the expected score for a given input.
- Selector picks the highest-scoring behavior.

### Behavior Tree Tasks (leaf tasks)
`ai/behaviorTree/tasks/leaf/` — individual `Task` subclasses that operate on data structs.

- Run each leaf task with a fake `Blackboard`; assert `SUCCEEDED` / `FAILED` / `RUNNING`.

### Stat / Pickup Math
`gamePlay/pickups/` — drop rates, stat scaling formulas.

---

## Tier 3 — Integration / Headless LibGDX

These require `HeadlessApplication` or a mocked Ashley `Engine`. Higher setup cost; do these after Tier 1 is green.

### ECS System Integration
Pick one system at a time, create an `Engine` with only the components that system reads/writes, step one tick, assert component state.

Good candidates:
- `systems/facts/` — fact-checking systems that fire consequences.
- `systems/player/` — health/damage application.
- `systems/pickups/` — pickup collection logic.

### ContactManager
Box2D collision callbacks with fake `Contact` objects — verify correct fact events fire on enemy/bullet contact.

---

## Suggested Order

1. Set up Gradle test source set and CI step (`./gradlew test`).
2. `TurboFacts` core: criterion evaluation + rule firing — highest logic density, zero deps.
3. Story parser/serializer round-trip.
4. Map generation connectivity.
5. `TileGraph` pathfinding.
6. Remaining Tier 1 utilities.
7. Behavior tree leaf tasks (Tier 2).
8. Headless ECS system tests as needed when bugs surface.
