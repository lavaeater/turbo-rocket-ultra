# Mutator Arena — Design & Roadmap

## What this is

A self-contained sandbox for evolving behavior trees. The loop:

```
seed tree → run simulation → score → mutate survivors → repeat
```

Each generation, a population of behavior trees is evaluated against a fixed scenario
(a player bot + arena), scored by a fitness function, the best survivors are kept and
mutated, and the process repeats. Good trees are saved to disk. The intent is to
discover enemy behaviors that are actually challenging without hand-authoring every
nuance.

The `Mutator` class already exists and handles the mutation step. Everything else
needs to be built.

---

## The five problems to solve

### 1. Serialization — trees must survive to disk

A `BehaviorTree<Entity>` is a tree of Kotlin objects with generic type parameters.
gdx-ai has its own XML-based serialization but it requires tasks to be registered
by string name and have no-arg constructors — our tasks use constructor injection
(`KClass<T>` parameters) which breaks that.

**Recommended approach: custom JSON serialization**

Define a sealed class hierarchy:

```kotlin
sealed class SerializedTask {
    data class Branch(val type: BranchType, val children: List<SerializedTask>, val guard: SerializedTask?) : SerializedTask()
    data class Decor(val type: DecorType, val child: SerializedTask, val guard: SerializedTask?) : SerializedTask()
    data class Leaf(val type: LeafType, val params: Map<String, String>) : SerializedTask()
}

enum class BranchType { Selector, Sequence, RandomSelector, RandomSequence, Parallel, DynamicGuard }
enum class DecorType { Invert, AlwaysFail, AlwaysSucceed, Repeat, HasComponent, DoesNotHaveComponent }
enum class LeafType { Attack, LookForAndStore, FindPath, NextStep, SelectSection, SelectTarget,
                      MoveTowards, Rotate, Delay, GrabAndThrow, PlayerIsInGrabRange, RushPlayer }
```

`params` carries constructor args as strings (`"componentClass" → "PlayerComponent"`,
`"delay" → "2.5"`, etc.). Serialize to JSON with kotlinx.serialization or Gson.
Two functions:

```kotlin
fun BehaviorTree<Entity>.serialize(): SerializedTask
fun SerializedTask.toTree(): BehaviorTree<Entity>
```

The component class names are looked up by simple name against a registry
(`mapOf("PlayerComponent" to PlayerComponent::class, ...)`). Adding a new leaf task
means adding it to the registry and to `LeafType`.

**Files to create:**
- `ai/behaviorTree/serialization/SerializedTask.kt`
- `ai/behaviorTree/serialization/BehaviorTreeSerializer.kt`
- `ai/behaviorTree/serialization/TaskRegistry.kt`

---

### 2. Headless / fast-forward simulation

Running a full game frame including rendering, VFX, audio, and UI just to evaluate
one enemy tree is far too slow. We need an engine that runs physics + AI + combat
only, at an accelerated timestep.

**The simulation engine is a stripped Ashley engine with only:**

| Keep | Remove |
|------|--------|
| `PhysicsSystem` | `RenderSystem`, `RenderMiniMapSystem` |
| `BehaviorTreeSystem` | `AnimationSystem`, `EnemyAnimationSystem` |
| `EnemyMovementSystem` | `AudioSystem`, `VfxManager` |
| `UtilityAiSystem` (for player bot) | `CameraUpdateSystem` |
| `EnemyDeathSystem` | `KeyboardInputSystem`, `GamepadInputSystem` |
| `UpdateTimePieceSystem` | `PlayerFlashlightSystem` |
| `DestroyAfterCooldownSystem` | All UI systems |
| `BurningSystem`, `BurningSystem` | `HUD`, `Stage` |

**Fast-forward:** replace `GdxAI.getTimepiece().deltaTime` with a fixed simulated
delta (e.g. `1/30f`) and step the physics world and engine update in a loop without
waiting for real-time frames. A 60-second simulation should run in under a second.

**Player bot:** instead of keyboard/gamepad input, a `SimulatedPlayerInputSystem`
writes synthetic movement and aim vectors directly to `PlayerControlComponent`
(random walk, always aims at nearest enemy, shoots on cooldown). This makes the
scenario repeatable enough to be a fair comparison between trees.

**Files to create:**
- `ai/arena/ArenaSimulation.kt` — builds the stripped engine, loads a fixed map,
  spawns player bot + N enemies with the candidate tree, steps for T seconds,
  returns `SimulationResult`
- `ai/arena/SimulatedPlayerInputSystem.kt` — synthetic player controller

---

### 3. Fitness scoring

A `SimulationResult` collects stats during the run:

```kotlin
data class SimulationResult(
    val enemySurvivalTime: Float,       // seconds alive
    val damageDealtToPlayer: Float,     // total HP removed from player
    val damageTakenByEnemy: Float,      // total HP removed from enemy
    val playerKilled: Boolean,
    val closestApproachToPlayer: Float, // minimum distance achieved
    val timeToFirstContact: Float,      // seconds until first damage dealt
)
```

The fitness function combines these into a single score. A reasonable starting
formula:

```
fitness = damageDealtToPlayer * 2
        + (if playerKilled then 500 else 0)
        + enemySurvivalTime * 0.5
        - damageTakenByEnemy * 0.3
        - timeToFirstContact * 1.0
```

This rewards aggression and player damage, penalizes dying quickly. The weights
are tunable constants. The formula should live in one place and be easy to
experiment with.

**Important:** run each tree against the same random seed for the player bot so
scores are comparable. Run each tree multiple times (3–5) and average — a single
run is noisy.

**Files to create:**
- `ai/arena/SimulationResult.kt`
- `ai/arena/FitnessFunction.kt`

---

### 4. Evolution loop

A generation is a fixed-size population of `(SerializedTask, Float)` pairs (tree +
score). The loop:

```
1. Evaluate all trees in the population (run simulation, score)
2. Sort by fitness descending
3. Keep top N (elites) unchanged
4. Fill remainder by mutating randomly selected survivors (weighted by rank)
5. Optionally crossover: splice subtrees between two parents
6. Save generation to disk
7. Repeat
```

Crossover (step 5) is optional but powerful: pick a random subtree from parent A
and swap it with a random subtree from parent B. This requires traversal helpers
on `SerializedTask`.

Population size 20–50 is enough to start. Generation count is open-ended; the user
decides when to stop.

**Files to create:**
- `ai/arena/EvolutionLoop.kt` — manages population, calls simulation, applies
  selection + mutation
- `ai/arena/Population.kt` — data class holding the current generation + metadata
  (generation number, best score, timestamp)

Population is saved as a JSON file per generation:
`arena/generation_042.json` containing all trees and their scores. The best tree of
each generation is also copied to `arena/best.json`.

---

### 5. The Arena Screen

A `MutatorArenaScreen` (extends `BasicScreen`) with three panels:

**Left — Population list**
- Current generation number
- Scrollable list of the N trees with their scores
- Highlight the current best
- Load a saved generation from disk

**Center — Live preview (optional)**
- When a tree is selected, runs one simulation with rendering enabled (normal speed)
  so you can watch it play out
- "Run headless generation" button starts the evolution loop for one full generation
- Progress indicator while generation is running (on a background thread or
  coroutine)

**Right — Stats**
- Best score per generation (line chart, or just a scrollable text log)
- Current fitness weights (editable fields so you can retune without recompiling)
- "Export best tree" button — writes `arena/best.json` and also bakes it into
  `Tree.kt` as a named function (manual step, just opens the file)

The screen is accessible from SetupScreen's debug menu (same D-key path as the
other editors).

**Files to create:**
- `screens/MutatorArenaScreen.kt`
- `screens/MutatorArenaViewModel.kt` — holds population state, wires to EvolutionLoop

---

## Build order

These have hard dependencies on each other — do them in order.

```
Phase 1 — Serialization
  SerializedTask + BehaviorTreeSerializer + TaskRegistry
  Goal: round-trip any existing tree through JSON and get an identical tree back

Phase 2 — Headless simulation
  ArenaSimulation + SimulatedPlayerInputSystem
  Goal: run a 60-second sim in under 2 seconds wall-clock time

Phase 3 — Scoring
  SimulationResult + FitnessFunction
  Goal: produce a stable, comparable score for the same tree run 5 times

Phase 4 — Evolution loop
  EvolutionLoop + Population, wired to Mutator
  Goal: run 10 generations headlessly from a unit test or main() and see scores
  improve (or at least change)

Phase 5 — Arena Screen
  MutatorArenaScreen + MutatorArenaViewModel
  Goal: drive the whole loop from in-game UI, watch a live preview of the winner
```

---

## What already exists

| Piece | Status |
|-------|--------|
| `Mutator.getMutatedTree()` | Done — mutates branch types, decorator inversions, leaf params |
| `Mutator.mutateLeaf()` | Done — randomizes Delay/Rotate params, clones others |
| `copyTo()` on all tasks | Done — required for `cloneTask()` which serialization will use |
| `BehaviorTreeViewScreen` | Partial — read-only viewer, useful for inspecting candidates |
| `Tree.nowWithAttacks()` | Done — seed tree for the initial population |

---

## Open questions

**What map do simulations run on?**
A hand-authored small arena map (one room, no exits) is simplest and most
repeatable. The map editor already exists — make one and name it `arena.txt`.

**How many enemies per simulation?**
Start with one. With one enemy vs one player bot the fitness signal is clearest.
Multi-enemy evaluation (flocking behavior) is a phase 2 concern.

**Crossover or mutation only?**
Mutation only is simpler to implement and still produces interesting results. Add
crossover once the basic loop is working — premature crossover produces mostly
broken trees.

**Threading?**
The headless simulation has no rendering or OpenGL calls, so it is safe to run on
a background thread. LibGDX's `Box2D` world is not thread-safe, but if each
simulation creates its own `World` instance (which `ArenaSimulation` should do),
there's no shared state and multiple simulations can run in parallel across threads.
This is a meaningful speedup for larger populations.

**When is a tree "good enough" to ship?**
Define a threshold fitness score before you start. If the best tree in generation N
exceeds the threshold, flag it. Don't let the loop run forever without a stopping
condition.
