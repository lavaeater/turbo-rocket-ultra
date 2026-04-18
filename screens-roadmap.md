# screens/ — Code Review & Roadmap

## What's here

29 files spanning actual screens, editor tools, concept experiments, and utility/data
classes that ended up in the package by proximity. The core gameplay loop is solid.
The experimental tools are at varying stages of completeness.

---

## Actual game screens (5)

### `SplashScreen.kt` — 52 lines — Complete
Shows a splash texture, transitions to Setup on SPACE. Clean.

**Next:** Nothing. Done.

---

### `SetupScreen.kt` — 360 lines — Complete
Player/character selection, map picker, keyboard + gamepad support. Debug mode (D key)
unlocks access to the editor screens. Well-structured, clean separation between view
and `SetupViewModel`.

**Next:** Minor — the debug mode entry key (D) is hardcoded. Could wire it to a flag
or cheat code, but low priority.

---

### `GameScreen.kt` — 292 lines — Complete
Main gameplay loop. Manages the Ashley engine, Box2D world, map loading, level
progression, enemy spawning, VFX, and the story manager. The physics accumulator
pattern is correct.

**Next:** `clearCache()` is now called on map load (done in Phase E). Keep an eye on
enemy spawn scaling — it currently grows exponentially with level.

---

### `PauseScreen.kt` — 8 lines — Stub
Extends `UserInterfaceScreen`. No pause UI, no resume/quit options. Just a class
declaration.

**Next:** Implement: resume button (or any key), quit-to-menu button, maybe a brief
summary of the run so far. This is the most glaring missing piece for game feel.

---

### `GameOverScreen.kt` — 8 lines — Stub
Same situation as PauseScreen. No score display, no restart option, nothing.

**Next:** Implement: show enemies killed / level reached / time survived, restart and
quit-to-menu buttons. Right now the game just hangs on game over.

---

## Editor screens (4)

### `MapEditorScreen.kt` — 464 lines — Complete and functional
Full grid-based level editor. Six editing modes (Normal, Paint, Alt, Camera, Command,
Dialog) managed by a clean `EditState`/`EditEvent` state machine. Saves maps as text
files with metadata (name, stories, facts, section types). Max grid 60×60.

**Next:** Refactor the nested command maps — the paint/alt mode command registration
is repetitive. An undo stack would make editing less painful. Otherwise this is solid.

---

### `CharacterEditorScreen.kt` — 34 lines — Thin wrapper
Delegates everything to `CharacterEditorViewModel` and `CharacterEditorView`. The
screen itself is just wiring.

**Next:** Nothing to do here unless the underlying view/viewmodel need work. The
screen boundary is correct.

---

### `AnimEditorScreen.kt` — 108 lines — Experimental, incomplete
Sprite sheet grid viewer and animation frame editor. Switches between grid-adjustment
and anim-preview modes (C key). Has a hardcoded sprite path (`sprites/boy/boy.png`)
and a `TODO` stub for a decrement function. No save/load.

**Next:** Remove the hardcoded path and hook it into the asset pipeline. Implement
the missing decrement. Add save/load for animation definitions. Or decide this is
superseded by the character editor and delete it.

---

### `GraphicsNodeEditorScreen.kt` — 149 lines — Experimental, purpose unclear
Click to create/select nodes, right-click to deselect, drag to reposition. Renders
a node hierarchy with snap-to-grid guides. No save/load. The `GraphicsNode` data
structure it operates on has a comment that says "Intriguing" next to the rotation
math.

**Next:** Decide what this is for. If it's a skeletal rig / attachment point editor,
write that down and add save/load. If it was an experiment that went nowhere, delete
it. Right now it has no path to the game.

---

## Behavior tree viewer (3 files)

### `BehaviorTreeViewScreen.kt` — 91 lines — Experimental, blocked
Visual tree viewer using Scene2D's tree widget. Shows guard conditions with IF/THEN
display. The remove/edit buttons are commented out with the note: *"if we cannot
remove children, there is no point."*

### `BehaviorTreeViewBuilder.kt` — 57 lines — Experimental, usable read-only
Recursive builder that populates the Scene2D tree from a `BehaviorTree<Entity>`.
Guards render correctly.

### `TaskNode.kt` — 42 lines — Stub
Scene2D tree node wrapper. `buildNodeForTask()` doesn't fully populate the actor.

**Next (all three):** The viewer is read-only and that's actually fine for debugging.
Complete `TaskNode.buildNodeForTask()` so all task types render their label properly.
Remove the commented-out edit buttons if you're not planning to implement them — the
viewer as a pure inspector is useful and honest.

---

## Concept / test screens (3)

### `ConceptScreenDetection.kt` — 118 lines — Active experiment
Field-of-view detection visualizer: color-coded detection zones, mouse tracking, FOV
sweep. The most recent of the concept screens. Has a stray `4` literal on line 38
that should be removed.

**Next:** Fix the stray `4`. Keep as a debugging aid for the vision system.

---

### `ConceptScreen.kt` — 111 lines — Stale sandbox
Interpolation / easing function visualizer. Hardcoded score arrays, SPACE opens a
scrolling dialog. No longer connected to anything active.

**Next:** Delete or repurpose. If you need an easing visualizer again, revive it.
Right now it's noise in the debug menu.

---

### `ConceptScreenOld.kt` — 160 lines — Dead
Earlier vision detection prototype, fully superseded by `ConceptScreenDetection`.
Has commented-out code throughout.

**Next:** Delete it.

---

## Utility / data classes in this package

These aren't screens but ended up here. They're all fine where they are.

| File | Lines | What it is |
|------|-------|-----------|
| `BasicScreen.kt` | 64 | Abstract base: input, camera, render pipeline |
| `UserInterfaceScreen.kt` | 15 | Adds Scene2D stage to BasicScreen |
| `CommandMap.kt` | 37 | Key → callback mapping used by editor screens |
| `EditState.kt` / `EditEvent.kt` | 10/14 | State machine states for MapEditorScreen |
| `GraphicsNode.kt` | 49 | Hierarchical node with local/global positioning |
| `MousePosition.kt` | 31 | Screen-to-world coordinate unprojection |
| `PlayerModel.kt` | 32 | Keyboard/gamepad selection state for SetupScreen |
| `SetupViewModel.kt` | 13 | Collects available controllers for SetupScreen |
| `SectionDefinition.kt` | 14 | Map section types (Boss, Loot, Start, etc.) |
| `CounterObject.kt` | 35 | Singleton: enemy count, level, bullets, objectives |
| `MapList.kt` | 12 | Global list of map files, used by Setup and Game |
| `KeyPress.kt` | 6 | Sealed class: Up/Down key states |
| `SplitScreenViewport.kt` | 330 | Full split-screen viewport implementation — **unused** |

`SplitScreenViewport.kt` is a complete, Apache-licensed viewport implementation that
nothing in the codebase references. Either wire it up (co-op split screen?) or delete
it.

---

## Priority summary

| Action | Effort | Impact |
|--------|--------|--------|
| Implement `PauseScreen` | Low | High — game feel |
| Implement `GameOverScreen` | Low | High — game feel |
| Delete `ConceptScreenOld` | Trivial | Low clutter |
| Fix stray `4` in `ConceptScreenDetection` | Trivial | Correctness |
| Complete `TaskNode` / BT viewer read-only | Low | Useful debug tool |
| Decide fate of `GraphicsNodeEditorScreen` | Low | Reduces confusion |
| Delete or finish `AnimEditorScreen` | Medium | Reduces confusion |
| Delete or use `SplitScreenViewport` | Trivial | Reduces confusion |
| Refactor `MapEditorScreen` command maps | Medium | Maintainability |

**Suggested order:** PauseScreen → GameOverScreen → delete dead code (ConceptScreenOld,
stray `4`, SplitScreenViewport decision) → complete BT viewer → decide GraphicsNode
editor fate.
