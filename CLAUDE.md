# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Turbo Rocket Ultra is a twin-stick shooter game written in Kotlin using LibGDX. It's a hobby project with no automated test suite — all testing is done manually through gameplay.

## Build & Run Commands

```bash
# Run the game (desktop)
./gradlew lwjgl3:run

# Build JAR
./gradlew lwjgl3:jar

# Run the built JAR directly
java -jar lwjgl3/build/libs/TurboRocketUltra-0.0.1.jar

# Platform-specific distribution builds (use packr + butler for itch.io)
./linux-build.sh
./mac-build.sh
./win-build.sh
./build-all.sh
```

There are no automated tests. Validate changes by running the game.

## Architecture

### Module Structure

- **`core/`** — All game logic (~27K lines). This is where nearly all work happens.
- **`lwjgl3/`** — Desktop backend (LWJGL3). Thin launcher layer only.
- **`gdx-lava/`** — Internal engine utilities: Ashley extensions, physics helpers, input, event bus, story/facts system. Treat as a stable base library.

### ECS (Entity-Component-System)

The game uses [Ashley](https://github.com/libgdx/ashley) ECS throughout `core/`:

- **Components** live in `core/src/main/kotlin/turbo/ecs/components/` — grouped by domain (graphics, gameplay, player, enemy, physics, pickups, etc.)
- **Systems** live in `core/src/main/kotlin/turbo/ecs/systems/` — process entities matching specific component families. 153 system files organized into subdirectories (input, physics, graphics, AI, gameplay, etc.)
- **Factories** in `core/src/main/kotlin/turbo/factories/Factories.kt` — extension functions that create entities with the right components (e.g. `enemy()`, `player()`, `bullet()`)

### Game State Machine

`core/.../gamestate/GameState.kt` defines states: Splash → Setup → Running ⇄ Paused → GameOver/MapEditor/AnimEditor. Managed by `MainGame.kt` (a KtxGame subclass).

### Dependency Injection

`core/.../injection/InjectionContext.kt` provides singleton factories. Systems and screens use `inject<Type>()` (KTX inject) to get cameras, viewports, the Ashley engine, Box2D world, audio, UI, etc. Lazily initialized.

### Physics

Box2D via LibGDX. Collision categories defined in `factories/Box2dCategories.kt`. Collision callbacks handled in `physics/ContactManager.kt`.

### Key Dependencies

| Library | Version | Purpose |
|---|---|---|
| LibGDX | 1.11.0 | Core framework |
| KTX | 1.13.1-rc1 | Kotlin extensions for LibGDX |
| Ashley | 1.7.4 | ECS |
| Box2D | (LibGDX bundled) | Physics |
| GDX AI | 1.8.2 | Behavior trees, steering, pathfinding |
| GDX Controllers | 2.2.2 | Gamepad input |

### Rendering

LibGDX `PolygonSpriteBatch` with orthographic camera. Post-processing via VfxManager (Bloom, CRT, OldTV effects). A dedicated `RenderMiniMapSystem` handles the minimap.

### AI

GDX AI behavior trees under `core/.../ai/`. Enemy pathfinding via `ai/pathfinding/TileGraph.kt`. Multiple AI systems update behavior trees and steering per frame.

### Audio / Messaging

`AudioPlayer` for sound effects and music. Internal event bus in `gdx-lava` (`messaging/Message.kt`, `MessageHandler`) used for decoupled communication between systems.

### Factoids (Story/Facts Engine)

`eater/turbofacts/` in `gdx-lava` — a rule-based fact engine that drives game events and story logic.
