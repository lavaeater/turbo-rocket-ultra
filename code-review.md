# Code Review: Turbo Rocket Ultra

---

## Critical Bugs

### 1. Off-by-one: fires N+1 projectiles
`core/.../ecs/systems/player/PlayerShootingSystem.kt:209`

```kotlin
for (projectile in 0..weapon.numberOfProjectiles)  // BUG: 0..N is inclusive
// Should be:
for (projectile in 0 until weapon.numberOfProjectiles)
```

Shotguns and spread-fire weapons fire one extra projectile every shot.

---

### 2. Off-by-one: melee scan arc is slightly too wide
`PlayerShootingSystem.kt:150`

Same issue — `0..numberOfSteps` generates one extra scan point, widening the FOV.

---

### 3. Force-unwrap chains in ContactManager during physics callbacks
`core/.../physics/ContactManager.kt:328,370`

```kotlin
Assets.newSoundEffects["weapons"]!!["grenade"]!!.random()
inventory.ammo[loot.ammoType]!!
```

Missing asset keys or ammo types crash the game mid-physics-step with a `NullPointerException`. These run inside the Box2D contact callback, which is especially bad timing.

---

### 4. RenderSystem sorts by `position.y` twice
`core/.../ecs/systems/graphics/RenderSystem.kt:58-60`

```kotlin
val x0 = p0.transform().position.y  // should be .x
val x1 = p1.transform().position.y  // should be .x
```

The X-coordinate tie-break is never actually used; sprites at the same layer and Y may flicker or have wrong draw order.

---

## Likely Bugs

### 5. Unsafe `userData` cast in Box2D extensions
`core/.../physics/Box2DExtensions.kt:29`

```kotlin
(userData as Entity)  // ClassCastException if null or wrong type
```

No guard before casting fixture userData.

---

### 6. Duplicate grenade detonation paths

Both `PhysicsSystem` (height ≤ 0 check) and `ContactManager` (collision callback) can trigger grenade detonation. Double-detonation causes duplicate effects or double damage.

---

### 7. Stale entity access in contact callbacks

`ContactManager.kt` contact handlers assume both entities still exist. If an entity is `DestroyComponent`-flagged and removed mid-frame, reading its components in the contact callback is undefined.

---

## Code Smells

### 8. `ContactManager.kt` is a God Object (~407 lines, ~59 contact branches)

All contact logic — damage, audio, UI, game state — lives in one class. Worth decomposing into per-contact-type handlers.

### 9. Magic weapon name strings
`factories/Factories.kt:87-101`

`"Molotov Cocktail"` and `"Grenade"` used for dispatch. A typo silently breaks the weapon with no error.

### 10. ~78 unsafe mapper extensions with no validation layer
`AshleyMappers.kt`

Every mapper does a raw `.get(entity)` call. There's no checked wrapper pattern, so callers have to remember to verify component existence themselves.

### 11. Mutable global state in `FitnessTracker`
`EnemyDeathSystem.kt`

`FitnessTracker.fitnessData` is a global mutable list modified per entity death. Entity-specific state shouldn't live in a static global.

---

## Dependency Versions

| Property                   | Old         | New        |
|----------------------------|-------------|------------|
| `kotlinVersion`            | 1.8.0       | 2.3.10     |
| `gdxVersion`               | 1.11.0      | 1.13.1     |
| `kotlinxCoroutinesVersion` | 1.6.4       | 1.10.2     |
| `gdxControllersVersion`    | 2.2.2       | 2.2.4      |
| `gdxVfxCoreVersion`        | 0.5.1       | 0.5.4      |
| `gdxVfxEffectsVersion`     | 0.5.1       | 0.5.4      |
| `simpleGraphsVersion`      | 3.0.0       | 5.1.1      |
| `colorfulVersion`          | 0.8.4       | 0.10.0     |
| `bladeInkVersion`          | 1.1.2       | 1.3.2      |
| `typingLabelVersion`       | 1.3.0       | 1.4.0      |
| `regExodusVersion`         | 0.1.15      | 0.1.21     |
| `screenManagerVersion`     | 0.6.7       | 0.7.1      |
| `inGameConsoleVersion`     | 1.0.0       | 1.0.1      |
| `hackLightsVersion`        | dd08b55956  | f0ba5deaff |

> **Note:** `gdxVersion` was initially bumped to 1.14.0 but rolled back to 1.13.1 to match `ktxVersion=1.13.1-rc1`.
> KTX follows LibGDX versioning — upgrading to 1.14.0 requires a matching KTX release.

---

## Recommended Priority

1. Fix `0..N` → `0 until N` loops in `PlayerShootingSystem` — easy and clearly wrong
2. Guard the `!!` chains in `ContactManager` with null-safe access or early returns
3. Fix the `position.y`/`position.y` duplicate in `RenderSystem`
4. Upgrade Kotlin to 1.9.x and pin KTX to a stable release
5. Address the stale-entity contact issue defensively (check `engine.getEntity` or a `DestroyComponent` presence before processing)
