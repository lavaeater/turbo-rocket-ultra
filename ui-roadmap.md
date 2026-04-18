# UI System — Code Review & Roadmap

## What exists today

The UI code has three distinct generations layered on top of each other. They coexist without
conflict, but they are not unified. Understanding which system does what is the first step to
deciding what to build next.

---

### Generation 1 — Simple procedural rendering (`ui/simple/`)

Immediate-mode rendering with a lightweight actor tree. No Scene2D involvement.

| File | Purpose |
|------|---------|
| `SimpleActor` / `LeafActor` | Interface + abstract base — `render(batch, parentPosition)` |
| `ContainerBaseActor` / `SimpleContainer` | Parent→child position propagation |
| `SpacedContainer` | Linear layout with configurable per-item offset |
| `TextActor` / `BoundTextActor` | Text rendering; bound variant updates from a lambda |
| `TextureActor` / `RepeatingTextureActor` | Texture rendering with scale/rotation |
| `DataBoundMeter` | Health/progress bar drawn via shapeDrawer |
| `UiBuilders.kt` | DSL builders (`textLabel`, `rootSpacedContainer { }`) |

**Status:** Functional and used in the game HUD. Minimal but battle-tested. The builder DSL is a
thin convenience wrapper. No layout constraints — all positioning is manual.

---

### Generation 2 — Element hierarchy (`ui/new/`)

A retained-mode element tree with parent-relative positioning. Designed for richer interactive UI.

| File | Purpose |
|------|---------|
| `AbstractElement` | Base: position, bounds, computed `actualPosition`, debug rendering |
| `BoundElement<T,V>` | Generic element bound to a data item via `valueFunc` |
| `TextureElement` | Texture with grid-sliced animation support (partly commented out) |
| `BoundTextureElement` / `BoundTextElement` | Data-bound texture and text variants |
| `BindableTextElement` | Alternate text binding (duplicate of BoundTextElement) |
| `BoundAnimationElement` | Animation playback bound to data |
| `ContainerElement` | Container with children |
| `CollectionContainerElement` | Renders a list of items with per-item element instances |
| `Carousel` | Selected-index manager with `nextItem()` / `previousItem()` |
| `BoundGridElement` | Debug grid overlay with adjustable cell size |
| `AnimationEditorElement` | 213-line interactive animation frame selector (most complete piece) |

**Status:** Partially built, inconsistently used. The `AnimationEditorElement` is the most
finished piece. `TextureElement`'s animation rendering is commented out. Two text binding classes
exist that do the same thing (`BindableTextElement` and `BoundTextElement`). The `Carousel` class
exists but its render loop is complex and apparently unused in production. No layout logic — still
manual positioning.

---

### Generation 3 — MVVM + Scene2D (`ui/mvvm/`, `ui/customactors/`)

The most structured system, used by the Character Editor.

| File | Purpose |
|------|---------|
| `ViewBase<VM>` | Abstract view — owns a Stage, wires bindings via reflection in `show()` |
| `View` | Lifecycle interface: `update`, `show`, `hide`, `resize` |
| `ViewModelBase` | Observer list + `propertyChanged(name, value)` trigger |
| `NotifyPropertyChanged` | Interface for the notification contract |
| `ModelNotifyDelegate` | `notifyChanged<T>()` property delegate — intercepts writes, fires notification |
| `Command` / `CommandBase` / `DelegateCommand` | Command pattern with `canExecute` state |
| `Scene2dExtensions` | `BindableLabel`, `CommandTextButton`, KTX DSL builders for both |
| `CustomActors` | `BoundLabel`, `BoundProgressBar`, `AnimatedSpriteImage`, `RepeatingTextureActor` — Scene2D actors that pull data via lambda |

**The binding mechanism (how it actually works):**

1. ViewModel properties declared with `by notifyChanged(initialValue)` — writing to the property
   calls `propertyChanged(propertyName, newValue)`.
2. `ViewBase.show()` calls `dataBind()`, which uses reflection (`viewModel::class.members`) to
   find all ViewModel properties, then for each `BindableWidget` in the stage it matches on
   `widget.propertyName == property.name` and registers an update lambda.
3. When a property fires `propertyChanged`, all registered lambdas for that name are called.

**Status:** Elegant concept, but the reflection binding is fragile (silent mismatch on name
typo), not type-safe at the call site, and slow for large widget trees. The `Command` objects have
`canExecute` and `onCanExecuteChanged` wiring, but nothing in the View actually disables buttons
based on it — the infrastructure exists but is not connected.

---

### Other UI subsystems

| System | Files | Status |
|--------|-------|--------|
| `Hud` | `ui/Hud.kt` (314 lines) | Functional game HUD; uses BoundLabel/BoundProgressBar; toast queue |
| `CrawlDialog` | `ui/CrawlDialog.kt` (78 lines) | Scrolling text dialog with line animation |
| `WastelandUI` | `ui/wastelandui/` (5 files, ~450 lines) | Conversation / inventory / portrait UI; integration unclear |
| `SelectedItemList` | `ui/SelectedItemList.kt` (113 lines) | Generic carousel list with callbacks |

There are **two** `IUserInterface` definitions (`ui/IUserInterface.kt` and
`ui/wastelandui/IUserInterface.kt`) with incompatible shapes.

---

## What the Character Editor uses (and where it falls short)

### Current CharacterEditorView layout

```
Stage (640×480)
├── characterTable (left)
│   ├── BindableLabel  ← subName
│   ├── BindableLabel  ← currentTags  (wrapping, 320px)
│   └── RenderableThing actor (character preview)
│
└── commandTable (bottom, full width)
    ├── Gender table (213px)
    │   ├── "Välj kön" label
    │   ├── "Man" button    → gender = "male"
    │   └── "Kvinna" button → gender = "female"
    │
    ├── Navigation table (160px)
    │   ├── << / >> buttons → previousSpriteSheet / nextSpriteSheet
    │   ├── BindableLabel   ← currentSpriteSheetName
    │   ├── << / >> buttons → previousCategory / nextCategory
    │   ├── BindableLabel   ← currentCategoryName
    │   ├── "Next anim" button → nextAnim
    │   └── BindableLabel   ← currentAnim
    │
    └── Export table
        └── "Export character" button → exportCharacter
```

### Hardcoded things that will need to change

| Location | What is hardcoded | Why it matters |
|----------|-------------------|----------------|
| `CharacterEditorViewModel.kt:34` | Animation `"walksouth"` as default | Doesn't generalize to the full LPC animation set |
| `CharacterEditorViewModel.kt:41–48` | SheetDef with walk rows 8–11, 9 frames | Duplicated in Screen; breaks when LPC layout changes |
| `CharacterEditorScreen.kt:22–29` | Same SheetDef again | Actual duplication |
| `CharacterEditorView.kt:44,49` | "Man" / "Kvinna" buttons | Doesn't show muscular/teen/child variants |
| `LpcSpriteSheetHelper.kt:21–42` | 12 categories with hardcoded tag sets | Should come from JSON (see lpc-roadmap.md Phase 1) |
| `AnimLoader.kt:149–150` | Character name list: boy, girl, blondie… | Adding a new character requires a code change |

---

## Gaps and problems to address

### 1. Reflection binding is fragile
`ViewBase.dataBind()` matches widget property names to ViewModel member names by string comparison
at runtime. A typo silently does nothing. There is no compile-time check. Consider replacing with
explicit registration (`bind(viewModel::myProp) { label.setText(it) }`) or using Kotlin's property
reference type (`KProperty1<VM, T>`) to preserve type safety.

### 2. `canExecute` is wired but disconnected
`CommandBase` has `canExecute: Boolean` and fires `onCanExecuteChanged`, but `CommandTextButton`
doesn't observe `onCanExecuteChanged` to actually disable itself. The infrastructure is there;
it just needs the observer hookup. This would enable "Export" to be disabled while no layers are
selected, for example.

### 3. Two text-binding element classes
`BindableTextElement` and `BoundTextElement` in `ui/new/` do essentially the same thing. One
should be deleted.

### 4. No layout system
Every position in `ui/simple/` and `ui/new/` is set manually. `SpacedContainer` only does linear
spacing. A minimal layout pass (stack, flow, grid) would eliminate the constant manual pixel math
in views.

### 5. Two IUserInterface definitions
`ui/IUserInterface.kt` (31 lines) and `ui/wastelandui/IUserInterface.kt` (21 lines) are different
interfaces that do overlapping things. Clarify which one is the canonical contract and delete or
merge the other.

### 6. `AnimationEditorElement` file-write is TODO
The animation editor (213 lines) lets you select frame ranges and press Enter to save, but the
save path is a TODO. If the animation editor is still a goal, complete the save/load path.
Otherwise the code is dead weight.

### 7. `CharacterEditorScreen` animation counter is unused
`currentFrame` is incremented in `render()` but never read by anything. The `sheetDef` declared in
the Screen duplicates the one in the ViewModel. Clean these up when touching the Screen.

---

## Roadmap

### Phase A — Clean up and consolidate (prerequisite for everything else)

**A1 — Remove duplicate SheetDef**
Delete the `sheetDef` in `CharacterEditorScreen.kt` (lines 22–29) and the unused `currentFrame`
counter. The ViewModel owns this.

**A2 — Merge or delete the duplicate IUserInterface**
Decide which interface is canonical. The one in `ui/` has more lifecycle methods (`addProgressBar`,
`showToast`). Delete `ui/wastelandui/IUserInterface.kt` and make `UserInterface` implement the main
one, or extract a minimal shared contract both can implement.

**A3 — Delete one of BindableTextElement / BoundTextElement**
Read both, pick the more complete one, delete the other. Update any call sites.

**A4 — Connect canExecute to widget disabled state**
In `CommandTextButton` (Scene2dExtensions.kt), add a listener to `command.onCanExecuteChanged`
that calls `setDisabled(!it)` on the button. This makes the existing Command infrastructure
actually useful.

---

### Phase B — Make binding explicit and type-safe

The reflection binding in `ViewBase` is the biggest architectural risk. Replace it with an
explicit registration API that still allows ergonomic one-line binding but fails at compile time
on type mismatches.

Proposed API (stays inside `ViewBase`):

```kotlin
// In initLayout():
bindLabel(viewModel::currentCategoryName, categoryLabel)
bindLabel(viewModel::currentSpriteSheetName, sheetLabel)
bindEnabled(viewModel::canExport, exportButton)
```

Implementation: `bindLabel` calls `addPropertyChangedHandler { name, value -> if (name ==
prop.name) label.setText(value.toString()) }`. No reflection on the ViewModel members; the
property reference captures the name at call time.

This is a drop-in replacement — the BindableLabel / CommandTextButton Scene2D widgets can remain
for places that prefer the declarative style, but the manual `bindingMap` reflection loop in
`dataBind()` can be retired.

---

### Phase C — Character Editor UX improvements

These directly enable the LPC migration work (see lpc-roadmap.md).

**C1 — Layer visibility toggles**
Each category row should have a checkbox/toggle. Currently the only way to remove a layer is to
navigate back to it and cycle to "none". Add a `selectedSpriteSheets[category] = EmptySpriteSheet`
shortcut exposed as a toggle button per category.

**C2 — Category list as a scrollable panel instead of one-at-a-time navigation**
The current << / >> navigation through categories is slow when there are 30+ JSON-driven
categories (after lpc-roadmap Phase 1). Replace with a `ScrollPane` containing a `Table` of
category rows — each row shows the category name, the currently selected sheet name, and a toggle.
Clicking a row expands it to show a thumbnail carousel of available options.

**C3 — Body-type selector (replaces hardcoded gender buttons)**
After lpc-roadmap Phase 4, the available body types come from the JSON variant keys. The view
should build the selector buttons dynamically from `viewModel.availableBodyTypes: List<String>`
rather than hardcoding "Man" / "Kvinna". Display name localization can remain a simple map.

**C4 — Thumbnail previews**
The sprite sheet name label tells you nothing about what a layer looks like. Add an
`AnimatedSpriteImage` (already exists in `CustomActors.kt`) that shows the walk-south animation
of the currently highlighted (not yet selected) layer as a preview before committing.

**C5 — Animation selector (replaces "Next anim" cycle)**
After lpc-roadmap Phase 3 adds more animations, cycling through them one at a time is tedious.
Replace with a horizontal tab bar or a `SelectBox` showing available animations by name.

**C6 — Export feedback**
`exportCharacter()` fires and nothing visible happens. Add a toast (the `Hud.showToast()` pattern
already exists) or a modal label that confirms the export path for 2 seconds.

---

### Phase D — Minimal layout primitives

Adding scrollable panels (Phase C2) and responsive layouts requires at least a basic layout pass.
Rather than pulling in a full layout library, add three small layout helpers to the `ui/new/`
element system:

- **StackLayout** — children stacked vertically or horizontally, each sized to its preferred
  dimensions, total size computed from children.
- **ScrollRegion** — clips its child to a viewport rect; draggable or scrollbar-driven offset.
- **FlowLayout** — wraps children into rows when they exceed a max width.

These three cover the character editor's needs (scrollable category list, wrapped tag display)
without over-engineering.

---

### Phase E — AnimationEditorElement completion (optional)

The `AnimationEditorElement` (213 lines) is a frame-range selector for the animation editor screen
(`AnimEditor` game state). It's the most complete piece of Gen 2 but has a TODO where it should
write the resulting `AnimDef` to disk. If the animation editor is still a feature goal:

1. Implement the save path: serialize the `AnimDef` to JSON in `localfiles/anims/{name}.json`.
2. Add a load path: `AnimLoader` checks `localfiles/anims/` for overrides before using its
   hardcoded definitions.
3. Wire a name-entry field into the editor so the user can name the animation before saving.

If the animation editor is not a near-term goal, extract the frame-selection logic into a simpler
widget and delete the rest.

---

## Priority summary

| Phase | Value | Effort | Blocks |
|-------|-------|--------|--------|
| A — Clean up duplicates | Medium | Very low | nothing, but reduces confusion |
| B — Type-safe binding | High | Low | makes C safer to build |
| C1 — Layer toggles | High | Low | better UX immediately |
| C2 — Scrollable category list | High | Medium | required after LPC Phase 1 |
| C3 — Dynamic body-type selector | Medium | Low | requires LPC Phase 4 |
| C4 — Thumbnail previews | High | Medium | big UX improvement |
| C5 — Animation selector | Medium | Low | requires LPC Phase 3 |
| C6 — Export feedback | Low | Very low | polish |
| D — Layout primitives | Medium | Medium | required for C2, C4 |
| E — AnimEditor completion | Low | Medium | standalone |

**Suggested order:** A → B → C1 → C6 → D → C2 → C4 → C3 → C5 → E
