# Turbo Rocket Ultra — State of the Game & What's Next

*Written April 2026. Based on reading all existing roadmap docs, devlogs, and current source.*

---

## What is actually done

### Core game loop
- Twin-stick shooting with keyboard + gamepad support
- Ashley ECS throughout; Box2D physics; LibGDX rendering pipeline
- Level loading from text map files with section metadata
- Enemy spawning that scales with level; kill counter; level progression
- Objectives system: kill N enemies, hold an area for N seconds
- Rule-based story engine (`TurboStory` / `TurboFactsOfTheWorld`) driving win/lose conditions
- Fact persistence — saves and loads facts to disk between sessions
- Post-processing VFX (bloom, CRT, old TV) via VfxManager

### Enemy AI
- Behavior tree system with a fluent Kotlin DSL (`Tree.kt`, `BuilderFunctions.kt`)
- A* pathfinding over a tile graph with path caching
- Utility AI framework with scored considerations and TTL-based memory
- Steering behaviors via Box2D-bridged LibGDX AI steering
- Six new boss/specialty leaf tasks: `Dash`, `SpinAttack`, `ChargeUpLaser`, `ThrowSnowball`, `ThrowTarBall`, `GrabAndThrowPlayer`
- Full BT serialization (JSON round-trip) and a `BehaviorTreeSerializer`/`TaskRegistry`
- Mutator Arena: evolutionary loop that seeds, scores, and mutates behavior trees in a headless simulation — accessible from the setup screen

### Story / conversation
- Ink-based conversation system (`InkConversation`) with variable sync to facts
- `conversationNpc()` factory and trigger system
- `StoryLoader` for JSON-defined story rules per map file
- `StringListContains` / `StringListSize` criteria

### Screens & navigation
- Splash, Setup, Game, Pause (stub), GameOver (stub), MapEditor, AnimEditor, CharacterEditor, MutatorArena
- ScreenManager (libgdx-screenmanager submodule) integrated — screen transitions on every screen change except GameScreen (VFX conflict)
- State machine driving all screen transitions via `GameState`/`GameEvent`

### Character editor
- LPC sprite compositing at runtime with category browsing and PNG export
- LPC spritesheets as a git submodule

### Infrastructure
- `gdx-vfx` and `screenmanager` as local submodules (replacing Maven deps)
- KTX inject for DI; KTX Ashley; KTX math/scene2d throughout
- `FactPersistence` for save/load; `TurboStoryManager` reactive rule engine

---

## What is NOT done — with suggestions

---

### 1. PauseScreen and GameOverScreen are empty stubs

Both classes are one line. The game hangs silently when paused or when you die.

**How to implement:**
- `PauseScreen`: Use `UserInterfaceScreen`'s existing Scene2D stage. Build a centered table with "Resume" (fires `ResumedGame`) and "Quit to Menu" (fires `ExitedGame`) buttons. Show current level and kills. Two hours of work.
- `GameOverScreen`: Same pattern. Display: enemies killed, level reached, time survived (add a timer float to `CounterObject`). "Play Again" and "Main Menu" buttons. This is the biggest gap for game feel right now.

**Status: Done**

---

### 2. No screen transition into/out of GameScreen

We skip the transition because `VfxManager` conflicts with `ScreenManager`'s `NestableFrameBuffer`. The other screens get nice transitions but entering the game is a hard cut.

**How to fix:**
Switch `VfxManager` to use `NestableFrameBuffer` from guacamole (which is already on the classpath via screenmanager). The VFX library is now a submodule — modify `VfxManager`'s FBO creation to use `NestableFrameBuffer` instead of `FrameBuffer`. Then re-enable the push transition for GameScreen.

Alternatively: disable VFX only during the transition frame (check `screenManager.isTransitioning()` in GameScreen.render before applying VFX).

**Status: Done**

---

### 3. Split-screen is implemented but completely unused

`SplitScreenViewport.kt` (330 lines, complete, Apache-licensed) is sitting in `screens/` with zero references.

**How to wire it up:**
- When 2+ players are present, detect in `GameScreen.show()` and activate split-screen mode
- Each player gets their own `SplitViewport` instance, positioned left/right half
- Each viewport follows its own player entity via `CameraFollowComponent`
- The existing two-player setup path in `SetupScreen` already supports multi-player; just switch the viewport class conditionally

This would be genuinely spectacular and is mostly already written.

**Added, super-important notes**

I have always intended this to be super complex. The intention is this: we start with one screen. As long as players are close enough to each other, we use one viewport / screen, which zooms out to some max level of zoom. When the players reach that magical distance, we activate the split-screen mode and zoom back in a bit on each player. And yes, if we have three players and two of them stay close to each other, the get half the screen and the other player gets the other half - and the same goes for four players. 

I am not sure if this is workable, perhaps one would have to create the viewports but not use them until we are zoomed out enough.

OK; I just noticed that you, Claude, had noted this as well. Oh, well. Good. 

**Status: Done**

---

### 4. Dynamic split-screen (players diverge → screen splits)

The devlog mentions this as "the coolest feature ever". The idea: when players are close, single-screen; when they diverge past a threshold, screen splits dynamically.

**How to implement:**
- Each frame, measure the max distance between any two players
- If distance > threshold: lerp toward split-screen (two viewports animating apart)
- If distance < threshold: lerp back to single viewport following the centroid
- The lerp target positions and sizes drive the `SplitViewport` bounds
- Trigger condition stored as a fact: `ScreenIsSplit: Boolean` for any UI/story reactions

Depends on item 3 above being done first.

**Status: Done**

---

### 5. PauseScreen has no visual — the game is invisible while paused

Currently pausing hides the GameScreen and shows an empty screen. A nice pause effect: blur/dim the game world behind the pause menu.

**How to implement:**
When `GameState.Paused` is entered, instead of pushing PauseScreen as a replacement, keep the GameScreen rendered in the background using ScreenManager's transition FBO trick — or simply render a dark overlay on top. The cleanest approach: render GameScreen to a texture on pause entry, display that texture as a blurred background in PauseScreen using an existing VFX shader. The `BlendingTransition` already demonstrates the technique.

**State: Done**

---

### 6. Enemies can't handle walls — they wedge and thrash

`MoveTowardsPositionTarget` detects stuck but only fails the task (which causes the behavior tree to restart and try the same thing again). Enemies get stuck in corners and vibrate.

**How to fix:**
When stuck is detected, request a fresh A* path from the current position to the goal instead of just failing. The `TileGraph.findPath()` already exists; call it again with the current entity position as the new start. If the re-path also gets stuck within N seconds, then fail. This breaks the thrash loop.

**State: Sliding Implemented**

---

### 7. Conversation system exists but nothing triggers it

`InkConversation`, `conversationNpc()`, `ConversationPresenter` are all built. No actual NPC in any map triggers a conversation.

**How to wire it up:**
- Add a `conversationNpc()` entity call in a map's factory or load hook, placed near the start area
- Give it an Ink story file (`.ink` compiled to `.json`) with a few lines of dialogue
- The `ConversationTriggerSystem` already handles the rest when the player approaches

This requires writing actual dialogue content, which is the real blocker.

---

### 8. Boss AI is stubs only

Three boss component files exist. `Tree.kt` has no boss behavior tree. The new boss leaf tasks (`ChargeUpLaser`, `GrabAndThrowPlayer`, `SpinAttack`, `Dash`) exist in the serializer but no tree uses them in the actual game.

**How to implement:**
Add a `bossBehaviorTree()` function in `Tree.kt` that uses a `DynamicGuardSelector` with:
- Guard: `health > 70%` → `nowWithAttacks()` (normal behavior)
- Guard: `health > 40%` → add `SpinAttack` and `Dash` to the mix
- Guard: always → `ChargeUpLaser` + `GrabAndThrowPlayer` desperate phase

Mark one enemy archetype as a boss (via a `BossComponent`) and assign this tree in the spawner.

**Status: Todo**

---

### 9. Wave spawning not implemented

The devlog has `[ ] Enemy wave spawning` checked off as TODO. Enemies spawn one at a time from a Poisson-like process; there's no "10 at once" wave pattern.

**How to implement:**
Add a `waveSize: Int` field to `SpawnComponent`. When `waveSize > 1`, the `EnemySpawnSystem` spawns that many enemies in rapid succession (across N frames or all at once) then waits for the full cooldown. Map definitions can configure: `normalWave: 3, bossWave: 8`. The accelerating spawns fact system already provides the timing ramp.

**Status: Todo**

---

### 10. Hardpoint / arm IK system is 70% done but not integrated

The concept screen (`HardPointConceptScreen`) shows working arm IK and skewed torso rendering. The `CharacterComponent` with named hardpoints exists. It's never wired into the actual game character.

**How to integrate:**
- Replace the current character sprite rendering (simple `SpriteBatch.draw`) with `RenderableType.CharacterWithArms`
- Wire `CharacterComponent.aimVector` to the mouse aim vector from `KeyboardInputSystem`
- The hardpoint positions then drive weapon attachment (`AttackTarget` → fire from shoulder hardpoint rather than entity center)
- Walk animations coexist because the IK arms overlay on top of the walking body sprite

The concept screen is the integration test — get it rendering identically in GameScreen and the hard part is done.

**Status: Todo**

#### Notes:

I want to do this differently by simply redesigning the player graphics in an interesting way. I want to use Inverse Kinematics and skewing of sprites when drawing
to achieve cool effects. More on that later!

---

### 11. Vehicles

The original vision for the game. Players enter a vehicle, control different stations (driver, gunner). Currently zero implementation.

**How to implement (rough sketch):**
- `VehicleComponent`: seats (list of slots: driver, gunner, passenger), current speed, turning rate, `Box2d` body
- `VehicleEnterSystem`: when player is within range and presses interact, sets `PlayerComponent.inVehicle = vehicleEntity`; disables normal movement; attaches `CameraFollowComponent` to vehicle
- `VehicleMovementSystem`: reads the driver's aim/move input and applies force/torque to the vehicle body
- `VehicleGunnerSystem`: reads a gunner player's aim and spawns bullets from a hardpoint
- Rendering: vehicle sprite + player sprites rendered at seat offsets
- "Moving level" variant: vehicle body moves through a scrolling environment — just make the vehicle body move on rails and everything else follows

This is the holy grail and probably 2-3 weeks of focused work.

**Status: Todo**

---

### 12. Towers are half-built

A `getTowerBehaviorTree()` exists in `Tree.kt`. Tower factories exist. Building them in the game doesn't happen yet, and there's no UI or resource system.

**How to implement:**
- Add a `BuildMode` state to `GameState` triggered from GameScreen (B key)
- In build mode, show a ghost of the selected tower following the cursor; clicking places it if the player has enough "scrap" (a new fact: `ScrapCount`)
- Towers earn scrap on kill; `ScrapCount` decrements on build
- Two tower types initially: stationary shooter (existing BT), area slow (aura effect using `ThrowSnowball` logic)
- HUD shows scrap count during gameplay

**Status: Todo**


#### Notes:

I think towers are buildable. Needs work, that's for sure.

---

### 13. Gibs / blood trails

`GibComponent` and some gib rendering exists; blood trails on the floor don't.

**How to implement:**
- When an entity dies, spawn N `GibEntity` instances at random velocities (small textured polygons, dampened Box2D bodies)
- A `BloodTrailSystem` drops a `DecalComponent` at the gib's position every N frames as it moves; decals fade over 30 seconds
- Decals are rendered as a separate pass below all entities (lowest z-order)
- The VFX submodule now being local makes it easy to add a blood splat shader pass

**Status: Todo**

---

### 14. Zombies with guns / ranged enemies

No enemies currently shoot back. All are melee or throw slow-moving projectiles.

**How to implement:**
- New leaf task `FireProjectile(damage, speed, range)` — creates a `BulletEntity` (the player bullet factory can be reused with different collision categories) aimed at the player
- New behavior tree `rangedEnemyTree()` using a `DynamicGuardSelector`: if player in range → `FireProjectile`, else → approach
- Enemy archetype registered in the spawner with a "gunner" tag
- Existing `ContactManager` handles bullet-player collision; just change the category bits to enemy-bullet

---

### 15. AnimEditorScreen is incomplete

Hardcoded to `sprites/boy/boy.png`, has a TODO stub, and can't save/load animation definitions.

**How to fix:**
1. Add a file picker (or use the existing map list pattern) to choose any PNG
2. Implement the missing decrement function (it's the same as the increment, reversed)
3. Save animation definitions to `localfiles/anims/{name}.json` using `JsonWriter`
4. Load overrides in `AnimLoader` — check `localfiles/anims/` before using hardcoded defs

Or delete it if the character editor supersedes it.

**Status: Todo**

---

### 16. GraphicsNodeEditorScreen has no purpose documented

149 lines of node drag/snap editor that goes nowhere.

**Decision:** Either declare it as the hardpoint/attachment-point editor (rename it `HardpointEditor`, add save/load of `CharacterComponent` hardpoint offsets) or delete it. As-is it's confusing.

#### Notes:

We'll do something for this when we re-design character graphics. 

---

### 17. The Mutator Arena has no live preview

You can evolve trees but can't watch the winner fight. The design doc mentions a live preview pane.

**How to implement:**
Add a "Watch Best" button in `MutatorArenaScreen`. When pressed, load the best tree from the current population, push a `LiveArenaScreen` (or reuse GameScreen in a limited mode) that runs a single simulation at normal speed with rendering enabled, then returns to the arena screen. The `ArenaSimulation` already creates entities — just skip the fast-forward and let the normal render loop drive it.

**Status: Todo**

---

## Priority order (my suggestion)

| # | Feature | Why now | Effort | Status         |
|---|---------|---------|--------|----------------|
| 2 | Towers — build mode + resource system | Fun feature, partial code exists | 3 days | Partially Done |
| 3 | Vehicles | The original vision — do it when the rest feels solid | 2-3 weeks | Todo           |
| 4 | Hardpoint integration into GameScreen | Leads to much better character rendering | 2 days | Redesign       |
| - | PauseScreen + GameOverScreen | Biggest gap for basic game feel | 1 day | Done           |
| - | GameScreen transition (fix VFX/NestableFrameBuffer) | Polish, already partially solved | Half day | Done           |
| - | Enemy stuck recovery (re-path on stuck) | Gameplay quality, embarrassing AI bug | Half day | Partially Done |
| - | Boss behavior tree using existing tasks | New enemy type, zero new infrastructure | 1 day | Partially Done |
| - | Wave spawning | Game feel, already designed | Half day | Todo           |
| - | Ranged enemy (FireProjectile task) | Makes the game harder and more interesting | 1 day | Done?          |
| - | Wire SplitScreenViewport (static split) | Big payoff, code already written | 1 day | Done           |