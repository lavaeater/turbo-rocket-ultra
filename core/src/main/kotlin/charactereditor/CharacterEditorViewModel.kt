package charactereditor

import mvvm.ViewModelBase
import mvvm.notifyChanged
import spritesheet.*
import kotlin.properties.Delegates.observable


class CharacterEditorViewModel(spriteSheetCategories: List<LpcSpriteSheetCategoryDefinition>) : ViewModelBase() {
	private var categories = spriteSheetCategories.associateBy { it.name }

    /**
	 * This one stays an observable, we want it to clear
	 * stuff and do "more" than the simple "notify a change has occured"
	 */
	var gender: String by observable("male") {_, _, _->
		selectedSpriteSheets.clear()
		updateCurrentCategory()
		updateRenderableThing()
	}

	private val isFemale: Boolean get() = gender == "female"
	private val selectedSpriteSheets =
        categories.keys.associateWith { SpriteSheet.EmptySpriteSheet() }.toMutableMap()
	private var currentCategoryIndex = 0
	private var currentSpriteSheetIndex = 0

	private val currentCategory: LpcSpriteSheetCategoryDefinition get() = categories.values.elementAt(currentCategoryIndex)

	private var currentSpriteSheets: List<LpcSpriteSheetDefinition> =
		currentCategory.spriteSheets.filter { it.isFemale == isFemale || it.isUnisex }

	private var currentSpriteSheet: LpcSpriteSheetDefinition by notifyChanged(currentSpriteSheets.elementAt(currentSpriteSheetIndex))
	var currentAnim: String by notifyChanged("walksouth")

	var currentTags: String by notifyChanged("")

	var subName: String by notifyChanged("")

	private lateinit var renderableThing : RenderableThing
	private val sheetDef = SheetDef(
			"JustWalkin'",
			listOf(
					TextureRegionDef("walknorth", 8, 9),
					TextureRegionDef("walkwest", 9, 9),
					TextureRegionDef("walksouth", 10, 9),
					TextureRegionDef("walkeast", 11, 9)
			))

	var currentCategoryName: String by notifyChanged(categories.keys.elementAt(currentCategoryIndex))

	var currentSpriteSheetName: String by
	notifyChanged("")

	fun getRenderableThing(): RenderableThing {
		if(!::renderableThing.isInitialized) {
			renderableThing = RenderableThing(
					sheetDef,
					selectedSpriteSheets.values
							.filter { it is SpriteSheet.LoadableSpriteSheet && it.visible })
		}
		return renderableThing
	}

	fun nextCategory() {
		currentCategoryIndex++

		if (currentCategoryIndex >= categories.size)
			currentCategoryIndex = 0

		updateCurrentCategory()
	}

	fun nextAnim() {
		currentAnim = renderableThing.nextAnim()
	}

	fun previousCategory() {
		currentCategoryIndex--

		if(currentCategoryIndex < 0)
			currentCategoryIndex = categories.size - 1

		updateCurrentCategory()
	}

	private fun updateCurrentCategory() {
		currentCategoryName = currentCategory.name

		currentSpriteSheets = currentCategory.spriteSheets.filter { it.isFemale == isFemale || it.isUnisex }


		fixCurrentSpriteSheetIndex()
	}

	fun nextSpriteSheet() {
		currentSpriteSheetIndex++

		if(currentSpriteSheetIndex >= currentSpriteSheets.size)
			currentSpriteSheetIndex = 0

		updateCurrentSpritesheet()
	}

	fun previousSpriteSheet() {
		currentSpriteSheetIndex--

		if (currentSpriteSheetIndex < 0)
			currentSpriteSheetIndex = currentSpriteSheets.size - 1

		updateCurrentSpritesheet()
	}

	private fun updateCurrentSpritesheet() {
		currentSpriteSheet = currentSpriteSheets.elementAt(currentSpriteSheetIndex)
		selectedSpriteSheets[currentCategoryName] = SpriteSheet.LoadableSpriteSheet(currentSpriteSheet.path, currentCategory.renderPriority)
		currentSpriteSheetName = currentSpriteSheet.name
		currentTags = currentSpriteSheet.tags.joinToString("\n")
		subName = currentSpriteSheet.subName
		updateRenderableThing()
	}

	private fun updateRenderableThing() {
		renderableThing.updateSprites(selectedSpriteSheets.values.filter { it is SpriteSheet.LoadableSpriteSheet && it.visible })
	}

	private fun fixCurrentSpriteSheetIndex() {
		val currentSheet = selectedSpriteSheets[currentCategoryName]
		currentSpriteSheetIndex = if(currentSheet is SpriteSheet.LoadableSpriteSheet) {
      currentSpriteSheets.indexOfFirst { it.path == currentSheet.path }
		} else {
			0
		}
	}

	/**
	 * Exports the currently created character as a
	 * spritesheet with a random UUID name.
	 */
	fun exportCharacter() {
		renderableThing.exportSpriteSheet()
	}
}