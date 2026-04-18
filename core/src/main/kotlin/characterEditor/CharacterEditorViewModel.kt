package characterEditor

import com.badlogic.gdx.Gdx
import ui.mvvm.ViewModelBase
import ui.mvvm.notifyChanged
import spritesheet.*
import kotlin.properties.Delegates.observable

// Swedish display names for LPC variant keys
private val VARIANT_DISPLAY_NAMES = mapOf(
    "male"      to "Man",
    "female"    to "Kvinna",
    "muscular"  to "Muskulös",
    "teen"      to "Tonåring",
    "child"     to "Barn",
    "pregnant"  to "Gravid"
)

class CharacterEditorViewModel(spriteSheetCategories: List<LpcSpriteSheetCategoryDefinition>) : ViewModelBase() {
    private var categories = spriteSheetCategories.associateBy { it.name }

    /**
     * Active variant key (e.g. "male", "female", "muscular").
     * Changing it resets the selection and refilters all category sheets.
     */
    var gender: String by observable("male") { _, _, _ ->
        selectedSpriteSheets.clear()
        updateCurrentCategory()
        updateRenderableThing()
    }

    /**
     * Union of all variant keys present across every sheet in every category.
     * Drives the dynamic variant button list in the View.
     */
    val availableVariants: List<String> = spriteSheetCategories
        .flatMap { it.spriteSheets }
        .mapNotNull { it.variantKey }
        .distinct()
        .sortedWith(compareBy { variantSortOrder(it) })

    /** Display name for each available variant, for use in the UI. */
    fun variantDisplayName(variantKey: String): String =
        VARIANT_DISPLAY_NAMES[variantKey] ?: variantKey.replaceFirstChar { it.uppercase() }

    private fun variantSortOrder(key: String) = when (key) {
        "male" -> 0; "female" -> 1; "muscular" -> 2
        "teen" -> 3; "child" -> 4; "pregnant" -> 5
        else -> 6
    }

    private val isFemale: Boolean get() = gender == "female"
    private val selectedSpriteSheets =
        categories.keys.associateWith { SpriteSheet.EmptySpriteSheet() }.toMutableMap<String, SpriteSheet>()
    private var currentCategoryIndex = 0
    private var currentSpriteSheetIndex = 0

    private val currentCategory: LpcSpriteSheetCategoryDefinition get() = categories.values.elementAt(currentCategoryIndex)

    private var currentSpriteSheets: List<LpcSpriteSheetDefinition> =
        currentCategory.spriteSheets.filter { it.matchesVariant(gender) }

    private var currentSpriteSheet: LpcSpriteSheetDefinition by notifyChanged(currentSpriteSheets.elementAt(currentSpriteSheetIndex))
    var currentAnim: String by notifyChanged("walksouth")

    var currentTags: String by notifyChanged("")
    var subName: String by notifyChanged("")

    private lateinit var renderableThing: RenderableThing
    private val sheetDef = SheetDef(
        "JustWalkin'",
        listOf(
            TextureRegionDef("walknorth", 8, 9, animFileName = "walk"),
            TextureRegionDef("walkwest",  9, 9, animFileName = "walk"),
            TextureRegionDef("walksouth", 10, 9, animFileName = "walk"),
            TextureRegionDef("walkeast",  11, 9, animFileName = "walk")
        ))

    var currentCategoryName: String by notifyChanged(categories.keys.elementAt(currentCategoryIndex))
    var currentSpriteSheetName: String by notifyChanged("")

    /** Formatted attribution text for all currently selected layers. Bound to the credits panel. */
    var currentCredits: String by notifyChanged("")

    fun getRenderableThing(): RenderableThing {
        if (!::renderableThing.isInitialized) {
            renderableThing = RenderableThing(
                sheetDef,
                selectedSpriteSheets.values.filter { it is SpriteSheet.LoadableSpriteSheet && it.visible })
        }
        return renderableThing
    }

    fun nextCategory() {
        currentCategoryIndex = (currentCategoryIndex + 1).clampIndex(categories.size)
        updateCurrentCategory()
    }

    fun previousCategory() {
        currentCategoryIndex = (currentCategoryIndex - 1).clampIndex(categories.size)
        updateCurrentCategory()
    }

    fun nextAnim() {
        currentAnim = renderableThing.nextAnim()
    }

    fun nextSpriteSheet() {
        currentSpriteSheetIndex = (currentSpriteSheetIndex + 1).clampIndex(currentSpriteSheets.size)
        updateCurrentSpritesheet()
    }

    fun previousSpriteSheet() {
        currentSpriteSheetIndex = (currentSpriteSheetIndex - 1).clampIndex(currentSpriteSheets.size)
        updateCurrentSpritesheet()
    }

    private fun updateCurrentCategory() {
        currentCategoryName = currentCategory.name
        currentSpriteSheets = currentCategory.spriteSheets.filter { it.matchesVariant(gender) }
        fixCurrentSpriteSheetIndex()
    }

    private fun updateCurrentSpritesheet() {
        currentSpriteSheet = currentSpriteSheets.elementAt(currentSpriteSheetIndex)
        selectedSpriteSheets[currentCategoryName] = SpriteSheet.LoadableSpriteSheet(
            currentSpriteSheet.path,
            currentCategory.renderPriority,
            currentSpriteSheet.variantFolder
        )
        currentSpriteSheetName = currentSpriteSheet.name
        currentTags = currentSpriteSheet.tags.joinToString("\n")
        subName = currentSpriteSheet.subName
        updateRenderableThing()
    }

    private fun updateRenderableThing() {
        renderableThing.updateSprites(selectedSpriteSheets.values.filter { it is SpriteSheet.LoadableSpriteSheet && it.visible })
        currentCredits = buildCreditsText()
    }

    private fun buildCreditsText(): String {
        val allCredits = selectedSpriteSheets.values
            .filterIsInstance<SpriteSheet.LoadableSpriteSheet>()
            .flatMap { sheet ->
                categories.values
                    .flatMap { it.spriteSheets }
                    .filter { it.path == sheet.path }
                    .flatMap { it.credits }
            }
            .distinctBy { it.file }

        if (allCredits.isEmpty()) return ""

        return allCredits.joinToString("\n\n") { credit ->
            buildString {
                append(credit.file)
                if (credit.authors.isNotEmpty()) append("\n  Av: ${credit.authors.joinToString(", ")}")
                if (credit.licenses.isNotEmpty()) append("\n  Licens: ${credit.licenses.joinToString(", ")}")
                if (credit.urls.isNotEmpty()) credit.urls.forEach { append("\n  $it") }
                if (credit.notes.isNotBlank()) append("\n  ${credit.notes}")
            }
        }
    }

    private fun fixCurrentSpriteSheetIndex() {
        val currentSheet = selectedSpriteSheets[currentCategoryName]
        currentSpriteSheetIndex = if (currentSheet is SpriteSheet.LoadableSpriteSheet) {
            currentSpriteSheets.indexOfFirst { it.path == currentSheet.path }.coerceAtLeast(0)
        } else {
            0
        }
    }

    fun exportCharacter() {
        val uuid = renderableThing.exportSpriteSheet()
        val creditsText = buildCreditsText()
        if (creditsText.isNotBlank()) {
            val outDir = Gdx.files.local("localfiles/created")
            if (!outDir.exists()) outDir.mkdirs()
            Gdx.files.local("localfiles/created/$uuid-credits.txt").writeString(
                "Credits for character $uuid\n\n$creditsText\n", false, "UTF-8"
            )
        }
    }

    private fun Int.clampIndex(size: Int): Int = if (size == 0) 0 else ((this % size) + size) % size
}

/**
 * Returns true if this sheet should be shown for the given [variantKey].
 *
 * Matching rules (in order):
 * 1. Sheet has an explicit variantKey → must equal [variantKey]
 * 2. No variantKey (legacy/unisex) → always shown
 */
private fun LpcSpriteSheetDefinition.matchesVariant(variantKey: String): Boolean {
    return this.variantKey == null || this.variantKey == variantKey
}
