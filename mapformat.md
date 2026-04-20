# Map File Format

Map files live in `assets/text_maps/` and are registered in `screens/MapList.kt`.
They are plain text files parsed by `map/grid/MapLoader.kt`.

---

## Overall structure

```
<grid>
---
name
<map name>
start
<start message>
success
<success message>
fail
<fail message>
facts
<key>:<value>:<type>
...
stories
<story-key>
...
```

The file has two parts separated by a line of dashes (`---` or `---------`, any run of `-`):

1. **Grid** — the section layout, one row per line
2. **Metadata** — keyword-delimited sections in any order after the separator

---

## Part 1 — The Grid

Each character represents one room/section. The grid is read left-to-right, top-to-bottom.
Columns map to X, rows map to Y. Any character that is not `e` or `*` is treated as a
passable section and added to the tile graph.

| Character | Meaning |
|-----------|---------|
| `s` | **Start** — players spawn here |
| `g` | **Goal** — an objective is placed here (hold/reach area) |
| `w` | **Spawner** — enemy spawner entity placed here |
| `l` | **Loot** — random weapon/ammo loot spawned here |
| `b` | **Boss** — a boss enemy spawned here |
| `h` | **Hacking station** — interactive hacking target placed here |
| `t` | **Target station** — target entity placed here |
| `c` | **Corridor** — passable, nothing special |
| `*` | **Empty** — no section (void/wall between rooms) |
| `e` | **Empty** — same as `*`, used in image-converted maps |

Sections that touch each other (horizontally or vertically) are automatically connected
in the pathfinding graph. Diagonal connections are not made.

**Example:**

```
hcc
w*l
l*c
s*g
```

This produces a 3×4 grid. The `*` cells are voids — no section is placed there and
enemies cannot path through them. The `s` at bottom-left is the start, `g` at
bottom-right is a goal, `w` in the middle-left spawns enemies, etc.

---

## Part 2 — Metadata

After the separator, the file is parsed into named sections. Each section starts with
a keyword on its own line; everything that follows (until the next keyword) belongs to
that section.

**Keywords:** `name`, `start`, `success`, `fail`, `facts`, `stories`

---

### `name`

Single line. The display name of the map shown in the HUD and stored in the
`CurrentMapName` fact.

```
name
The entrance
```

---

### `start`

Multi-line text shown to players before the level begins (in the crawl dialog).

```
start
Clean these cursed halls from varmint.
Infected zombie-men roam our future dwellings
and they have to be cleaned out.
```

---

### `success`

Multi-line text shown when the level is completed.

```
success
Families.

This is all about families.
```

---

### `fail`

Multi-line text shown when the level is failed.

```
fail
We don't do this for ourselves, we do it for

the families.
```

---

### `facts`

One fact per line in the format `Key:Value:Type`.

| Type code | Kotlin type | Example |
|-----------|-------------|---------|
| `i` | `Int` | `MaxEnemies:200:i` |
| `f` | `Float` | `AcceleratingSpawnsFactor:1.25:f` |
| `b` | `Boolean` | `AcceleratingSpawns:true:b` |
| *(anything else)* | `String` | `CurrentMapName:Tunnels:s` |

These facts are written into `TurboFactsOfTheWorld` when the map loads, overwriting
any previous values. All story rules and systems read from the same fact store, so
facts set here control the level's difficulty, objectives, and win conditions.

**Commonly used facts:**

| Fact key | Type | Purpose |
|----------|------|---------|
| `MaxEnemies` | `i` | Maximum enemies alive simultaneously |
| `MaxSpawnedEnemies` | `i` | Total enemies that can ever spawn before spawners go quiet |
| `StartingEnemyCount` | `i` | Enemies spawned immediately when the map loads (before cooldown kicks in) |
| `WaveSize` | `i` | Max enemies spawned per spawner trigger (actual count is random 1..WaveSize) |
| `AcceleratingSpawns` | `b` | Whether spawner cooldown shrinks over time |
| `AcceleratingSpawnsFactor` | `f` | Multiplier applied to cooldown each cycle when accelerating (e.g. `1.15` = 15% faster each time) |
| `EnemyKillCount` | `i` | Should always be reset to `0` at map start |
| `TargetEnemyKillCount` | `i` | Kill target for the `enemy-killcount` story; `0` = no kill objective |
| `ShowEnemyKillCount` | `b` | Whether the HUD displays the kill counter |

---

### `stories`

One story key per line. These are the named pre-built stories from `StoryHelper`
that should be active for this map.

```
stories
start-story
level-failed
level-complete
basic-story
```

**Available story keys:**

| Key | What it does |
|-----|-------------|
| `start-story` | Shows the `start` message as a crawl dialog when the level begins |
| `level-failed` | Fires `GameOver` event when all players are dead |
| `level-complete` | Fires `LevelComplete` event when the win condition is met |
| `enemy-killcount` | Win when `EnemyKillCount >= TargetEnemyKillCount` (requires `TargetEnemyKillCount > 0`) |
| `basic-story` | Combines boss-death and objective-touch win conditions |

Stories not listed here are inactive for this map, even if their criteria would otherwise
be satisfied.

---

## Complete example

```
hcc
w*l
l*c
s*g
---
name
The entrance
start
Clean these cursed halls from varmint.
Infected zombie-men roam our future dwellings
and they have to be cleaned out to make
room for healthy, tax-paying people!
success
Families.

This is all about families.
fail
We don't do this for ourselves, we do it for
the families.
facts
MaxEnemies:1:i
MaxSpawnedEnemies:1:i
AcceleratingSpawns:false:b
AcceleratingSpawnsFactor:1.25:f
EnemyKillCount:0:i
TargetEnemyKillCount:0:i
ShowEnemyKillCount:false:b
WaveSize:15:i
StartingEnemyCount:5:i
stories
start-story
level-failed
level-complete
basic-story
```

---

## Notes

- The separator line must appear before any metadata and must contain at least one `-`.
  Its length doesn't matter — `---` and `---------` are both valid.
- Blank lines inside `start`/`success`/`fail` blocks are preserved as paragraph breaks.
- The `stories` and `facts` sections can be omitted if empty, but their keywords must
  still be present if any entries follow.
- Map files are loaded from `localfiles/` at runtime via `Gdx.files.local()`. During
  development, `localfiles/` maps to the project root. In a packaged build it maps to
  the user's app data directory.
- There is no support for comments in map files.
