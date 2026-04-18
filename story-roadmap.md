# Story & Facts Engine — Code Review & Roadmap

## What exists today

The "story engine" is really three separate things at different stages of completion:

1. **The fact/rule engine** — solid, actively used for level progression
2. **The conversation system** — fully built UI, but nothing in the game triggers it
3. **The places/world system** — mostly abandoned

---

### The Fact Engine (`core/.../turbofacts/`)

The core of the system. All of it works and is used.

| File | Lines | Purpose |
|------|-------|---------|
| `Factoid.kt` | 19 | Sealed type hierarchy: `BooleanFact`, `IntFact`, `StringFact`, `FloatFact`, `StringListFact` |
| `Factoids.kt` | 50 | Constants for every fact key used in the game |
| `TurboFactsOfTheWorld.kt` | 281 | Central store: `Map<String, Factoid>` with typed getters/setters and a change callback |
| `Criterion.kt` | 234 | Sealed predicate hierarchy — `SingleBoolean`, `SingleInt`, `IntVersusInt`, etc. |
| `TurboRule.kt` + `TurboRuleBuilder.kt` | 97 | A named list of criteria; passes only when ALL criteria pass |
| `TurboStory.kt` + `TurboStoryBuilder.kt` | 53 | A named list of rules + a consequence lambda; fires when all rules pass |
| `TurboStoryManager.kt` | 32 | Holds all stories; checks them each frame when facts change |
| `StoryHelper.kt` | 133 | Five pre-built stories: level start, level complete, level failed, boss+objectives, kill-count win |

**How it wires together:**

```
Gameplay / ECS
    → writes a fact (e.g. EnemyKillCount += 1)
    → TurboFactsOfTheWorld fires onFactUpdated callback
    → sends Message.FactUpdated via MessageHandler
    → TurboStoryManager.needsChecking = true
    → GameScreen.render() calls checkIfNeeded()
    → each TurboStory checks all its TurboRules
    → if all rules pass → consequence lambda fires (e.g. gameState.acceptEvent(LevelComplete))
```

**What the engine is actually used for today:**

| System | What it does with facts |
|--------|------------------------|
| `FactSystem` (IntervalSystem, 1s) | Sets `BossIsDead` and `AllObjectivesAreTouched` by reading entity state |
| `EnemyDeathSystem` | Increments `EnemyKillCount` |
| `EnemySpawnSystem` | Reads `AcceleratingSpawns` and `AcceleratingSpawnsFactor` to scale wave difficulty |
| `MainGame` | Sets `LivingPlayerCount` on player reset |
| `GameScreen` | Activates story manager; reads `GotoNextLevel` fact to advance |
| `Hud` | Reads `EnemyKillCount`, `TargetEnemyKillCount`, `ShowEnemyKillCount`, `CurrentMapName` for HUD display |
| `StoryHelper` | 5 pre-built win/lose stories that drive level state machine |

---

### The Conversation System (`core/.../story/conversation/` + `ui/wastelandui/`)

Fully built infrastructure — nothing triggers it in gameplay.

**Data model:**

```
IConversation (interface)
├── InkConversation      — wraps Bladecoder Ink runtime; reads Ink variables
│                          (MET_BEFORE, PLAYER_NAME, REACTION_SCORE, STEP_OF_STORY)
├── InlineConvo          — simple hardcoded back-and-forth dialogue
├── InternalConversation — step-based graph: List<ConversationStep>, each step has List<ConversationRoute>
└── RuleBasedConversation — STUB: every method throws TODO()

ConversationStep(text: String, routes: List<ConversationRoute>)
ConversationRoute(text: String, type: RouteType)
RouteType: End, Continue, Branch
```

**The UI presenter (`ConversationPresenter.kt`, 220 lines):**
A fully implemented state machine:
```
NotStarted → AntagonistIsSpeaking → ProtagonistChoosing → Ended
```
- Renders NPC portrait and dialogue text in a Scene2D table
- Shows player response options as buttons
- Handles input for choice selection
- `ConversationManager` orchestrates presenter + conversation instance

**The Ink loader (`InkLoader.kt`, 26 lines):** Reads `.json` Ink story files from assets.
Ink support exists because Bladecoder Adventure Engine's Ink runtime is already a dependency.

---

### The Consequence System (`core/.../story/consequence/`)

A consequence interface was designed but stories don't use it — they use raw lambdas instead.

| File | Lines | Status |
|------|-------|--------|
| `Consequence.kt` | 10 | Interface: `apply()`, `type: ConsequenceType` |
| `ConsequenceType.kt` | 7 | Enum: `Simple`, `Conversation`, `Empty` |
| `SimpleConsequence.kt` | 13 | Lambda wrapper — works, never used |
| `EmptyConsequence.kt` | 14 | No-op — works, never used |
| `ConversationConsequence.kt` | 53 | Half-baked: `apply()` is entirely commented out |
| `ProcessInputConsequence.kt` | 4 | Interface only — no implementations |
| `RetrieveConsequence.kt` | 3 | Interface only — no implementations |
| `ApplyConsequence.kt` | 1 | Empty file |

`TurboStory.consequence` is declared as `(TurboStory) -> Unit`, not `Consequence`, so the
interface is bypassed entirely. The infrastructure for typed consequences was started and abandoned.

---

### The Places System (`core/.../story/places/`)

Mostly abandoned.

- `Place.kt` (19 lines): A data class with `name` and `stealth: Int = -5`. Comments in the file
  are open design questions ("What should a place know about?"). Never instantiated.
- `PlacesOfTheWorld.kt` (103 lines): `enterPlace(place)` does nothing. ~70 lines of
  constructor code are commented out — city generation, conversation setup, NPC placement.
  Never instantiated.

---

## What works, what's orphaned, what's dead

| Component | State | Notes |
|-----------|-------|-------|
| `TurboFactsOfTheWorld` | **Working** | Central store, actively written and read |
| `Criterion` / `TurboRule` | **Working** | Used by all 5 pre-built stories |
| `TurboStory` / `TurboStoryManager` | **Working** | Drives the level state machine |
| `StoryHelper` (5 stories) | **Working** | Level start/complete/failed/boss/kill-count |
| `FactSystem` ECS | **Working** | Syncs entity state → facts each second |
| `Hud` fact reads | **Working** | Kill count, map name, show/hide conditions |
| `ConversationPresenter` | **Orphaned** | Full UI state machine, nothing starts it |
| `InkConversation` | **Orphaned** | Ink runtime wired in, no `.ink.json` files loaded |
| `InternalConversation` | **Orphaned** | Functional step-graph, never used |
| `InlineConvo` | **Orphaned** | Works, never used |
| `ConversationConsequence` | **Half-baked** | Implementation commented out |
| `RuleBasedConversation` | **Dead** | Throws TODO() on everything |
| `Consequence` interface | **Dead** | Bypassed — stories use lambdas |
| `PlacesOfTheWorld` | **Dead** | 70% commented out, never instantiated |
| `Place` | **Dead** | Empty data class, never used |
| `ApplyConsequence.kt` | **Dead** | Empty file |
| `AllBooleans`, `AllStrings`, `AllInts` criteria | **Unused** | Implemented but no story uses them |
| `AnyBoolean`, `AnyString`, `AnyInts` criteria | **Unused** | Same |
| `FactsLikeThatMan.waveSize` | **Unused** | Defined, never read |

---

## Gaps in the current design

### 1. No NPC entities trigger conversations
`ConversationManager.startConversation()` exists but there is no ECS component for "NPC with
dialogue," no input system that detects player proximity to an NPC, and no game state that hands
off control to the conversation presenter. The entire conversation UI is built and waiting.

### 2. Stories can't trigger conversations as consequences
`ConversationConsequence.apply()` is commented out. A story rule fires and can only call a
lambda — it cannot cleanly hand off to the dialogue system. `RuleBasedConversation` was the
attempt to unify these two things but never got past the stub phase.

### 3. Facts are reset with no persistence
`StoryHelper.levelStartFacts()` resets everything. There is no save/load of facts between
sessions. The Ink variables (`MET_BEFORE`, `PLAYER_NAME`, `REACTION_SCORE`) suggest persistence
was intended but never implemented.

### 4. Stories have no priority or conflict resolution
If two stories' rules pass on the same frame, both fire. There's no `exclusive` flag or story
priority queue. This is fine for the current five level-progression stories but will become a
problem with more complex narrative.

### 5. The Criterion `All*` and `Any*` variants are unused
Only `Single*` criteria are used in practice. The multi-fact variants (`AllBooleans`, `AnyInts`,
etc.) exist but have no call sites.

### 6. Ink has no content
The loader, runtime, and variable binding are all present. There are no `.ink.json` story files
in assets. Without content, this is infrastructure with nowhere to go.

---

## Roadmap

### Phase 1 — Clean up dead code

Low risk, reduces confusion.

**1.1 — Delete the Places system**
`Place.kt` and `PlacesOfTheWorld.kt` are entirely unused. Delete both.

**1.2 — Delete `RuleBasedConversation`**
Every method throws `TODO()`. Delete it. If the concept reappears, it belongs in `TurboStory`
as a consequence type, not as a `IConversation` implementation.

**1.3 — Delete empty consequence stubs**
Delete `ApplyConsequence.kt` (empty file), `ProcessInputConsequence.kt`, and
`RetrieveConsequence.kt` (interfaces with no implementations). Rename `ConsequenceType` to
remove the `Conversation` variant if `ConversationConsequence` is not going to be implemented.

**1.4 — Remove unused `FactsLikeThatMan`**
The `waveSize` property in `Factoids.kt` is never read. Remove it.

---

### Phase 2 — Wire conversations to the game

The conversation UI is built. What's missing is the trigger chain.

**2.1 — Add a `ConversationComponent` to Ashley**
A component that holds an `IConversation` reference and optionally a trigger radius.

```kotlin
data class ConversationComponent(
    val conversation: IConversation,
    val triggerRadius: Float = 64f,
    var hasBeenTriggered: Boolean = false
)
```

**2.2 — Add a `ConversationTriggerSystem`**
An ECS system that detects when the player is within `triggerRadius` of an entity with
`ConversationComponent` and calls `conversationManager.startConversation(entity.conversation)`.
Respect `hasBeenTriggered` if the conversation should only fire once.

**2.3 — Pause game state during conversation**
When a conversation starts, push the game into `Paused` (or a new `Conversation` sub-state) so
enemies stop and input routes to the dialogue presenter instead of the player.

**2.4 — Resume on conversation end**
`ConversationPresenter` already has an `Ended` state. Add a callback that fires
`gameState.acceptEvent(Resume)` and optionally sets a fact (`factoids.setBooleanFact(true, "npc_met_${npcId}")`)
so the conversation doesn't repeat.

---

### Phase 3 — Fact-driven story consequences

Connect the story rule engine to the conversation system as a consequence type.

**3.1 — Implement `ConversationConsequence`**
The file exists with commented-out skeleton code. Implement `apply()` to call
`conversationManager.startConversation(conversation)`. This means a story rule can fire and start
a cutscene or NPC dialogue.

**3.2 — Replace `TurboStory.consequence: (TurboStory) -> Unit` with `Consequence`**
The raw lambda type means `SimpleConsequence`, `EmptyConsequence`, and `ConversationConsequence`
are bypassed. Change the field type to `Consequence`. `StoryHelper` will need its lambda-style
stories wrapped in `SimpleConsequence`. This makes the consequence type inspectable and serializable.

**3.3 — Add story priority**
Add `priority: Int = 0` to `TurboStory`. In `TurboStoryManager`, sort stories by priority
descending before checking. Add an `exclusive: Boolean = false` flag — if an exclusive story
fires, skip checking lower-priority stories that frame.

---

### Phase 4 — Ink content and persistence

If Ink-driven narrative is a real goal (the infrastructure is there).

**4.1 — Write at least one Ink story**
Create `assets/stories/intro.ink.json`. Wire it into a `ConversationComponent` on an NPC entity
in the first level. This validates the full pipeline end to end.

**4.2 — Ink → facts bridge**
When an Ink story reads or writes Ink variables (`MET_BEFORE`, `REACTION_SCORE`, etc.), mirror
those into `TurboFactsOfTheWorld`. This lets story rules react to narrative state.

**4.3 — Fact persistence**
Serialize `TurboFactsOfTheWorld.facts` to a JSON file in `localfiles/save.json` on game exit.
Load it on startup before story manager activation. Gate on a `Factoids.SaveExists` boolean fact
so stories that should only fire on a fresh save can check for it.

---

### Phase 5 — Richer rule authoring

For use with character editor integration or more complex level scripting.

**5.1 — `StringListFact` queries**
`StringListFact` exists in the type hierarchy but there are no `Criterion` implementations that
read string lists. Add `StringListContains(key, value)` and `StringListSize(key, comparison)`
criteria. This enables facts like "player has visited zones A, B, and C."

**5.2 — `IntVersusInt` usage**
`IntVersusInt` (compare two fact values against each other) is implemented but has no story that
uses it. Once more facts exist (health, score, level) this becomes useful for relative conditions
like "score is higher than personal best."

**5.3 — Story authoring via data files**
`StoryHelper.kt` hardcodes five stories in Kotlin. Consider a simple JSON format for story
definitions so level designers can add stories per-map without a code change. The map loading
path (`GridMapGenerator`) already handles per-map story setup.

---

### Character editor integration

The character editor doesn't use facts or stories today, but a few natural hooks exist:

- **Exported character → fact:** After `exportCharacter()`, set a `StringFact` with the export
  UUID. Level-loading code can read this fact to decide which spritesheet to use for the player
  character. This is the simplest form of "character creation affects gameplay."

- **Character as NPC source:** If NPCs get `ConversationComponent` attached (Phase 2), they also
  need spritesheet assignment. The character editor's export pipeline could be reused to define
  NPC appearances, stored as facts (`StringFact("npc_blacksmith_sheet", uuid)`).

---

## Priority summary

| Phase | Value | Effort | Blocks |
|-------|-------|--------|--------|
| 1 — Delete dead code | Medium | Very low | reduces confusion for all future work |
| 2 — Wire conversations to game | High | Medium | makes conversation UI actually useful |
| 3 — Consequence types | Medium | Low | requires Phase 2 |
| 4 — Ink content + persistence | High | Medium | requires Phase 2 + actual writing work |
| 5 — Richer rule authoring | Low | Low | useful once more content exists |
| Character editor integration | Medium | Low | natural once Phase 2 exists |

**Suggested order:** 1 → 2 → 3 → character-editor hook → 4 → 5
