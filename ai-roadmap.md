# AI System — Code Review & Roadmap

## What exists today

The AI code has four active subsystems and two incomplete/experimental ones. They coexist in
`core/.../ai/` and connect to ECS via components and systems in `ecs/components/` and
`ecs/systems/`.

---

### Subsystem 1 — Behavior Tree (`ai/behaviorTree/`)

Task-driven decision making built on LibGDX AI's `BehaviorTree<Entity>`. Trees are composed
via a fluent Kotlin DSL and defined in `Tree.kt` for enemies and towers.

| File | Purpose |
|------|---------|
| `Tree.kt` | Defines `nowWithAttacks()` and `getTowerBehaviorTree()` |
| `builders/BuilderFunctions.kt` | DSL entry points: `selector`, `sequence`, `guard`, etc. |
| `tasks/leaf/` | 10 concrete tasks: `FindPathTo`, `NextStepOnPath`, `LookForAndStore`, `AttackTarget`, `SelectTarget`, `SelectSection`, `MoveTowardsPositionTarget`, `RotateTask`, `DelayTask` |
| `tasks/EntityComponentTask.kt` | Base class for tasks that add/remove components |
| `tasks/ComponentExistenceGuard.kt` | Guard that passes when a component is present |
| `Mutator.kt` | Experimental evolutionary mutation skeleton — mostly commented out |

**Status:** Functional for enemy ambling, pathfinding, sight-detection, and attacking.
All 13 task files (leaf tasks + base classes) have `copyTo()` returning
`TODO("Not yet implemented")` — this is required by gdx-ai for task cloning and is the most
widespread incomplete contract in the codebase.

---

### Subsystem 2 — Utility AI (`ai/utility/`)

Score-based decision making as an alternative (or complement) to behavior trees. Actions
contain `Consideration` objects; the highest-scoring action is executed each frame.

| File | Purpose |
|------|---------|
| `AiAction` / `Consideration` | Core interfaces |
| `ConsideredAction` / `ConsideredActionWithState<T>` | Stateless and stateful action variants |
| `CanISeeThisConsideration` | FOV + raycast visibility check |
| `behaviors/EnemyBehaviors.kt` (283 lines) | Concrete actions: `panik`, `attackTarget`, `approachTarget`, `amble` |
| `UtilityAiSystem.kt` | Scores all actions, executes the top one |

Each action manages its own state-component lifecycle (added on `init`, removed on `abort`).
This is the most elegantly designed part of the AI codebase.

**Status:** Complete and production-ready.

---

### Subsystem 3 — Steering (`ai/steering/`)

Wraps LibGDX steering behaviors around Box2D bodies via `Box2dSteerable` (256 lines, lives in
`ecs/components/`).

| File | Purpose |
|------|---------|
| `Box2dSteerable.kt` | Steerable component bridging Box2D ↔ LibGDX AI steering |
| `Box2dRadiusProximity` | Circular neighbour detection |
| `Box2dFieldOfViewProximity` | Cone detection (angle-based) |
| `Box2dSquareAABBProximity` | AABB base class |
| `Box2dRaycastCollisionDetector` | Obstacle raycasting for steering |
| `SteerSystem.kt` | Calls `update(deltaTime)` on all Steerables |

**Status:** Complete and solid. Well-abstracted against LibGDX AI interfaces.

---

### Subsystem 4 — Pathfinding (`ai/pathfinding/`)

Lightweight A* over a tile grid, with memoized paths.

| File | Purpose |
|------|---------|
| `TileGraph.kt` | Indexed graph; caches start→goal paths |
| `TileConnection.kt` | Edge cost calculation |
| `CoordinateHeuristic.kt` | Euclidean distance heuristic |

Populated externally by `GridMapManager`, consumed by `FindPathTo` and `EnemyBehaviors.amble()`.

**Status:** Complete. One structural issue: the path cache is never cleared (see gaps below).

---

### Subsystem 5 — Memory (`ecs/components/Memory.kt`)

Tracks entities the AI has "seen" as a `Map<KType, Map<Entity, Float>>` with TTL-based decay.

**Status:** Implemented. Only used by utility AI (`CanISeeThisConsideration`,
`EnemyBehaviors.approachTarget`). Behavior tree tasks don't use it.

---

### Subsystem 6 — Boss AI (`ecs/components/ai/boss/`)

Three component files exist (`BossComponent`, etc.) but are not integrated into `Tree.kt` or
any behavior system.

**Status:** Skeleton only.

---

## Data flow

```
Behavior Tree (event-driven)          Utility AI (score-driven)
  |                                       |
  Tasks add/remove components         AiComponent holds scored actions
  (Path, Waypoint, Stuck, etc.)       Top action executes, manages its
  |                                   own state component lifecycle
  MoveTowards / Rotate / Attack           |
  write to AgentProperties            Memory updated (entity → TTL)
  |
  SteerSystem applies steering
  force/torque to Box2D body
```

Both systems can run simultaneously on the same entity. No documented interaction rules exist.

---

## Gaps and problems to address

### 1. `copyTo()` unimplemented across all 13 behavior tree tasks

Every task returns `TODO("Not yet implemented")` from the `copyTo()` method required by
gdx-ai. This is benign as long as task cloning is never triggered, but it blocks:

- Behavior tree mutation (see `Mutator.kt`)
- Any gdx-ai debugging tools that clone trees
- Future multi-instance behavior trees sharing the same task definition

**Files affected:** All 10 leaf tasks + `EntityComponentTask`, `ComponentExistenceGuard`,
`BehaviorTreeMarker`.

### 2. Duplicated raycasting / vision logic

~40 lines of nearly identical raycasting code appear in both:
- `CanISeeThisConsideration` (100 lines total)
- `LookForAndStore` (124 lines total)

Both iterate Box2D fixtures in a radius, filter by FOV angle, raycast to confirm line-of-sight,
and check if the nearest hit is the target entity. A shared `VisionService` or
`RaycastSightCheck(from, aimVec, to, fovDeg, world)` function would eliminate this.

The standalone helper `canISeeYouFromHere()` already exists in `ai/Extensions.kt` for the
angle part — the raycast confirmation needs the same treatment.

### 3. Unbounded path cache

`TileGraph` caches every `findPath(start, goal)` call and never evicts entries. For static
maps with a fixed enemy count this is fine. For dynamic maps (destructible tiles, moving
waypoints) or long sessions, the cache grows unbounded.

Fix: clear on map load, or use an LRU with a reasonable cap.

### 4. Two AI systems with no interaction rules

An entity can have both `BehaviorComponent` (behavior tree) and `AiComponent` (utility AI)
active simultaneously. There is no documented or enforced priority. At minimum, the
conventions for which system is used for which entity type should be written down. At best,
a single `AiMode` tag component could make the contract explicit.

### 5. Stuck recovery is incomplete

`MoveTowardsPositionTarget` detects stuck (distance not decreasing past `STUCK_DISTANCE`) but
the only recovery is to remove the target and fail the task. This can cause behavior tree
loops where the enemy repeatedly tries and fails to navigate the same tile. A re-path request
or short wait before retry would fix the loop.

### 6. `Mutator.kt` is a non-functional skeleton

The evolutionary AI mutator (randomly modifying behavior tree task parameters) is ~70 lines,
mostly commented out. Either complete it or delete it — as-is it is dead weight that confuses
the picture of what's active.

### 7. Boss AI not integrated

Three boss component files exist but `Tree.kt` has no boss behavior tree. If boss enemies are
a future goal, this needs work. If not, the component stubs should be deleted.

---

## What's well-built

- ✓ Behavior tree DSL is clean and composable — easy to define new enemy archetypes
- ✓ Utility AI consideration framework is elegant (pure scoring functions, no side effects)
- ✓ `EnemyBehaviors.kt` shows both systems working at their best
- ✓ Steering proximity classes properly abstracted against LibGDX AI interfaces
- ✓ Memory with TTL decay is a solid foundation for "aware" AI behaviour
- ✓ Component lifecycle management in utility AI actions (`init`/`abort`/`act`) is clean

---

## Roadmap

### Phase A — Clean up dead code (low effort, low risk)

**A1 — Delete or complete `Mutator.kt`**
It adds noise and implies an evolutionary AI system that doesn't exist. If mutation isn't a
near-term goal, delete it. If it is, design it properly and implement.

**A2 — Delete or integrate boss AI stubs**
`components/ai/boss/` files either need a behavior tree entry in `Tree.kt` or should be
removed.

---

### Phase B — Extract shared vision / raycasting logic

Create a `VisionService` (or top-level function) that accepts a Box2D `World`, source
position/direction, target entity, and FOV angle, and returns a `VisibilityResult`. Replace
the duplicated code in `CanISeeThisConsideration` and `LookForAndStore` with calls to it.

The angle helper already lives in `Extensions.kt` — the raycast confirmation just needs
extracting alongside it.

---

### Phase C — Implement `copyTo()` in all behavior tree tasks

Straightforward but repetitive: each task's `copyTo()` just needs to construct a new instance
with the same configuration parameters and call `super.copyTo(task)`. Doing this:

- Satisfies the gdx-ai contract
- Enables multiple enemy instances to share a tree prototype without state cross-contamination
- Unblocks any future use of `Mutator.kt`

---

### Phase D — Stuck recovery in `MoveTowardsPositionTarget`

Replace the bare "fail and remove target" with:
1. Request a fresh path on first stuck detection (dynamic re-path)
2. If still stuck after a second threshold, fail cleanly

This prevents behavior tree thrash loops when enemies get wedged.

---

### Phase E — Fix path cache unboundedness

Add a `clearCache()` method to `TileGraph` and call it from `GridMapManager` on map load.
Optionally cap the cache to the N most recently used paths (LRU).

---

### Phase F — Document AI system interaction rules

Write a comment block at the top of `AiComponent` and `BehaviorComponent` explaining:
- Which entity archetypes use which system
- Whether simultaneous use is intentional on any entity type
- What the priority/override contract is if both fire in the same frame

No code change required, just clarifying a contract that currently exists only implicitly.

---

## Priority summary

| Phase | Value | Effort | Risk |
|-------|-------|--------|------|
| A — Delete dead code | Low | Very low | None |
| B — Extract vision logic | Medium | Low | Low |
| C — Implement copyTo() | Medium | Medium | Low |
| D — Stuck recovery | High | Low | Low |
| E — Path cache fix | Low | Very low | None |
| F — Document AI interaction | High | Very low | None |

**Suggested order:** A → F → D → E → B → C
