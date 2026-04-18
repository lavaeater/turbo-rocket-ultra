# LPC Character Editor — Evolution Roadmap

## Current State

The character editor composes LPC sprite layers at runtime and exports a single flattened PNG.
It finds layer PNGs by recursively scanning a `localfiles/lpc/` directory and matching filenames
against hardcoded category tags. Nothing reads the LPC submodule's JSON metadata.

**Core files:**
- `core/.../screens/CharacterEditorScreen.kt` — screen wiring, hardcoded walk animation rows
- `core/.../characterEditor/CharacterEditorViewModel.kt` — selection state, compositing, export
- `core/.../characterEditor/CharacterEditorView.kt` — UI (gender buttons, category nav, export)
- `core/.../spritesheet/LpcSpriteSheetHelper.kt` — hardcoded category list, file scanning
- `core/.../spritesheet/LpcSpriteSheetDefinition.kt` — wraps a PNG path, tag/gender detection
- `core/.../spritesheet/RenderableThing.kt` — composites layers, animates, exports PNG
- `core/.../animation/LpcCharacterAnimDefinition.kt` — hardcoded animation row→frame mapping
- `core/.../animation/AnimLoader.kt` — loads characters from `sprites/lpc/*.png`

**What the LPC submodule now provides that the game ignores:**
- `sheet_definitions/**/*.json` — layer metadata: name, render priority, supported animations,
  which body-type variants exist (male/female/muscular/teen/child/pregnant), recolor palettes
- `palette_definitions/**/*.json` — per-material color palettes (body, cloth, metal, eye, hair)
- Per-animation separate PNGs (`walk.png`, `idle.png`, `slash.png`, …) instead of a combined sheet
- A full credits database (`CREDITS.csv`, per-sheet `credits` fields)

---

## The Central Structural Mismatch

The game expects one big spritesheet with all animations stacked in rows (the old LPC format).
The new LPC repo gives one PNG **per animation** per layer. The character editor already
composites layers, so it could composite per-animation too — but the rest of the game pipeline
(AnimLoader, LpcCharacterAnimDefinition, SpriteCategory) would need to change.

There are two viable architectural paths:

| Path | Description | Effort |
|------|-------------|--------|
| **A — Keep combined sheet** | Composite per-animation PNGs into a combined sheet on export, same as now | Low |
| **B — Per-animation loading** | Load and composite each animation separately at runtime | High |

Path A preserves the existing game pipeline. Path B gives more flexibility (lazy loading,
runtime recoloring) but requires reworking AnimLoader and all character rendering code.
**This roadmap recommends Path A** unless runtime recoloring becomes a design goal.

---

## Phase 1 — Replace file-scan discovery with JSON-driven discovery

**Goal:** Read `sheet_definitions/**/*.json` instead of scanning filenames and guessing categories.

### 1.1 — Write a JSON parser for sheet definitions

Create `core/.../spritesheet/LpcSheetDefinitionLoader.kt`.

A sheet definition JSON looks like this:

```json
{
  "name": "Leather Armour",
  "priority": 30,
  "layer_1": { "male": "torso/armour/leather/male/", "female": "torso/armour/leather/female/" },
  "animations": ["idle", "walk", "run", "slash", "thrust", "hurt"],
  "match_body_color": false,
  "recolors": [{ "material": "cloth", "palettes": ["ulpc"] }],
  "credits": [...]
}
```

Parse this into a data class:

```kotlin
data class LpcSheetDef(
    val name: String,
    val priority: Int,
    val variants: Map<String, String>,   // gender/type → sprite folder path
    val animations: List<String>,
    val recolors: List<LpcRecolor>
)
```

Walk `lpc/sheet_definitions/` recursively, parse every `.json` that is not a `meta_*.json`, and
build a flat list of `LpcSheetDef`. The file path relative to `sheet_definitions/` gives the
category hierarchy (e.g. `torso/armour/leather.json` → category `torso > armour`).

### 1.2 — Replace LpcSpriteSheetHelper

The current helper hardcodes 12 categories with magic tag strings. Replace it with a loader that:

1. Reads all `LpcSheetDef` objects (from Phase 1.1)
2. Groups them into `LpcSpriteSheetCategoryDefinition` instances using the directory hierarchy
   from `sheet_definitions/`
3. Resolves sprite PNG paths via `variants["male"]` / `variants["female"]` + animation name +
   `.png`, rooted at `lpc/spritesheets/`

The `renderPriority` field comes straight from `priority` in the JSON — no more hardcoded z-values.

### 1.3 — Wire the new loader into CharacterEditorScreen

Replace `LpcSpriteSheetHelper.getCategories()` call with `LpcSheetDefinitionLoader.load(basePath)`.
The category structure that arrives at `CharacterEditorViewModel` stays the same shape, so the
ViewModel and View need no changes in this phase.

**Outcome:** Adding a new LPC layer to the submodule automatically appears in the editor. No more
manual category or tag maintenance.

---

## Phase 2 — Support per-animation PNGs in the compositor

**Goal:** Use the separate `walk.png`, `idle.png`, etc. files from the new LPC layout instead of
expecting all animations in a single combined sheet.

### 2.1 — Update RenderableThing / SpriteCategory to accept animation-per-file

Currently `TextureRegionDef` points at a row inside one sheet. Add a parallel path where the
"sheet" is a directory and each animation is a separate PNG inside it. The frame dimensions and
count still come from the definition JSON.

```kotlin
sealed class AnimSource {
    data class Row(val sheet: String, val row: Int, val frames: Int) : AnimSource()
    data class File(val dir: String, val animName: String, val frames: Int) : AnimSource()
}
```

`SpriteCategory.getTextureRegions()` handles both cases.

### 2.2 — Update the export path

`RenderableThing.exportSpriteSheet()` must composite across animation files rather than rows.
For each animation in `LpcCharacterAnimDefinition.definitions`:

1. Look up the source frames for each selected layer (from `AnimSource.File` paths)
2. Composite frames left-to-right per direction row
3. Stack animation rows in the same order the game's `LpcCharacterAnimDefinition` expects

The exported PNG format stays identical — the game's `AnimLoader` does not change.

### 2.3 — Graceful fallback for missing animations

A given layer may not support all animations (e.g. a hat has no `slash.png`). When a layer
file is missing for an animation, composite transparent frames for that layer only. The
body layer always provides the full set.

**Outcome:** The editor can now use the full new LPC spritesheet library without needing any
manually pre-merged PNGs.

---

## Phase 3 — Expand animation support

**Goal:** Map more of the LPC animation set to in-game `AnimState` values.

### 3.1 — Audit what is available vs what the game uses

Current `AnimState` values that have no sprite row mapping: `Run`, `Hurt`, `WalkWithGun`,
`RunWithGun`, `PickUp`, `Climb`, `Roll`, `Shoot`.

The LPC submodule provides: `walk`, `run`, `idle`, `hurt`, `slash`, `thrust`, `shoot`, `spellcast`,
`climb`, `jump`, `sit`, `emote`, `combat`, `1h_slash`, `1h_backslash`, `1h_halfslash`.

Good mapping candidates:
- `Run` → `run.png`
- `Hurt` → `hurt.png`
- `Shoot` / `Aiming` → `shoot.png`
- `Climb` → `climb.png`
- `Slash` → `slash.png` or `1h_slash.png`

### 3.2 — Extend LpcCharacterAnimDefinition

Add a row entry for each newly supported animation. Keep the existing row numbers for
backward-compatibility with already-exported character PNGs (or version-stamp exported files).

### 3.3 — Update the export compositor

Make `exportSpriteSheet()` include the new animation rows. Characters exported after this change
will support more animations; characters exported before will still work (missing rows render as
missing frames, which the engine already handles gracefully for non-looping states).

---

## Phase 4 — Gender / body-type selector

**Goal:** Surface the full LPC variant list (male, female, muscular, teen, child) in the editor.

### 4.1 — Extend gender model

`CharacterEditorViewModel` currently uses a plain `String` gender field defaulting to `"male"`.
The sheet definition JSON's `layer_1` keys give the valid variant names per layer. Build the
union of all variant keys across all selected layers as the list of available genders.

### 4.2 — Update the UI

`CharacterEditorView` has two hardcoded buttons ("Man" / "Kvinna"). Replace with a dynamic list
driven by the ViewModel's available variants. Add Swedish display names for the new options.

### 4.3 — Handle layers that lack a variant

If a selected layer doesn't have a `"teen"` variant, fall back to `"male"`, then omit the layer
entirely (show transparent). Document this fallback in the UI (greyed-out indicator).

---

## Phase 5 — Palette recoloring (optional / future)

**Goal:** Allow the player to recolor hair, skin, cloth, and metal at runtime.

The LPC `palette_definitions/` JSONs provide full color-ramp arrays per material. Each sheet
definition declares which materials it uses via `recolors`.

This is a significant graphics feature and a separate project. High-level approach:
- At export or render time, apply a palette swap: for each pixel matching a source color in the
  original palette, substitute the corresponding color from the chosen palette.
- Implement as a LibGDX `Shader` for runtime use, or as a pixel-manipulation pass in the
  export compositor for baked PNGs.
- The ViewModel would hold a `Map<String, String>` of material → chosen palette name.
- The View adds a color-picker per material present in the current character's layers.

---

## Phase 6 — Credits tracking

**Goal:** Surface attribution for the selected LPC assets.

Each sheet definition JSON has a `credits` array with author names, licenses, and URLs. When a
player exports a character, write a companion `{UUID}-credits.txt` alongside the PNG listing all
contributing authors and licenses. Display a "Credits" tab in the editor showing the current
character's attribution list.

---

## Suggested Sequencing

| Phase | Value | Effort | Dependencies |
|-------|-------|--------|--------------|
| 1 — JSON-driven discovery | High — removes all manual maintenance | Medium | none |
| 2 — Per-animation PNGs | High — unlocks full LPC library | Medium | Phase 1 |
| 3 — More animations | Medium — richer gameplay | Low | Phase 2 |
| 4 — Body-type variants | Medium — more character variety | Low | Phase 1 |
| 5 — Palette recoloring | High UX value | High | Phase 2 |
| 6 — Credits | Low gameplay value, good practice | Low | Phase 1 |

**Recommended order:** 1 → 2 → 4 → 3 → 6 → 5
