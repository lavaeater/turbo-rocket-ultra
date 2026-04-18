package characterEditor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.utils.Align
import com.badlogic.gdx.utils.Array as GdxArray
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ui.customactors.boundLabel
import ui.mvvm.ViewBase
import ui.mvvm.commandTextButton
import spritesheet.RenderableThing

class CharacterEditorView(
    batch: Batch,
    viewModel: CharacterEditorViewModel
) : ViewBase<CharacterEditorViewModel>(batch, viewModel) {

    private lateinit var renderableThing: RenderableThing
    private val previewActor = LayerPreviewActor()

    override fun initLayout() {
        renderableThing = viewModel.getRenderableThing()
        bind(viewModel::previewVarFolder) { previewActor.variantFolder = it }
        renderableThing.setFillParent(true)

        var subNameLabel: Label?
        var currentTagsLabel: Label?

        val characterTable = scene2d.table {
            label("") { subNameLabel = this }
            row()
            label("") {
                currentTagsLabel = this
                wrap = true
            }
            center()
            width = uiWidth / 2f
            height = width
            add(renderableThing)
        }

        var sheetNameLabel: Label?
        var categoryNameLabel: Label?
        var exportMessageLabel: Label?
        var creditsLabel: Label?

        val commandTable = scene2d.table {
            bottom()

            // Variant selector — dynamic based on what the loaded sheets support
            table {
                label("Välj variant")
                row()
                left()
                val variants = viewModel.availableVariants.ifEmpty { listOf("male", "female") }
                for (variantKey in variants) {
                    commandTextButton(viewModel.variantDisplayName(variantKey), { viewModel.gender = variantKey })
                        .cell(align = Align.left).pad(padding)
                    row()
                }
            }.cell(width = uiWidth / 4f, fillY = true).top()

            // Scrollable category list — click a row to navigate to that category
            val categoryListTable = scene2d.table {
                for (catName in viewModel.categoryNames) {
                    commandTextButton(catName, { viewModel.selectCategoryByName(catName) })
                        .cell(align = Align.left, width = uiWidth / 5f, pad = padding / 2f)
                    boundLabel({ viewModel.selectedSheetNameFor(catName) })
                        .cell(align = Align.left, width = uiWidth / 6f, pad = padding / 2f)
                    commandTextButton("✕", { viewModel.clearLayerFor(catName) })
                        .cell(pad = padding / 2f)
                    row()
                }
                left()
                top()
            }
            val categoryScrollPane = ScrollPane(categoryListTable)
            categoryScrollPane.setScrollingDisabled(true, false)
            add(categoryScrollPane).width(uiWidth / 3f).height(uiHeight * 0.6f).top()

            // Sprite sheet browser for the selected category + animation selector
            table {
                // Thumbnail preview of the currently highlighted layer
                add(previewActor).size(64f).pad(padding).colspan(4)
                row()
                // Sprite sheet navigation
                commandTextButton("<<", viewModel::previousSpriteSheet)
                    .cell().pad(padding).left()
                label("") {
                    sheetNameLabel = this
                    setAlignment(Align.center)
                }.cell(width = uiWidth / 5f)
                commandTextButton(">>", viewModel::nextSpriteSheet)
                    .cell().pad(padding).right()
                commandTextButton("✕", viewModel::clearCurrentLayer)
                    .cell().pad(padding)
                row()
                // Active category label (read-only — use the list on the left to change category)
                label("") {
                    categoryNameLabel = this
                    setAlignment(Align.center)
                }.cell(width = uiWidth / 4f, colspan = 4)
                row()
                // Animation selector — SelectBox listing all available animations
                val animBox = SelectBox<String>(Scene2DSkin.defaultSkin)
                val animItems = GdxArray<String>()
                viewModel.availableAnims.forEach { animItems.add(it) }
                animBox.items = animItems
                animBox.selected = viewModel.currentAnim
                animBox.addListener(object : ChangeListener() {
                    override fun changed(event: ChangeEvent, actor: Actor) {
                        viewModel.selectAnim(animBox.selected)
                    }
                })
                add(animBox).pad(padding).colspan(2)
            }.cell().top().padLeft(padding)

            // Export column
            table {
                label("") {
                    exportMessageLabel = this
                    wrap = true
                    setAlignment(Align.center)
                }.cell(width = uiWidth / 4f)
                row()
                commandTextButton("Export character", viewModel.exportCommand)
                    .cell().pad(padding)
                row()
                // Credits panel — scrollable, shows attribution for all selected layers
                val credits = label("") {
                    creditsLabel = this
                    wrap = true
                    setAlignment(Align.topLeft)
                }
                val creditsScrollPane = ScrollPane(credits)
                creditsScrollPane.setScrollingDisabled(true, false)
                add(creditsScrollPane).width(uiWidth / 4f).height(uiHeight / 3f).top()
            }.cell().top().padLeft(padding)

            setFillParent(true)
        }

        stage.addActor(commandTable)
        stage.addActor(characterTable)

        bindLabel(viewModel::subName, subNameLabel!!)
        bind(viewModel::currentTags) { currentTagsLabel!!.setText(it) }
        bindLabel(viewModel::currentSpriteSheetName, sheetNameLabel!!)
        bindLabel(viewModel::currentCategoryName, categoryNameLabel!!)
        bind(viewModel::exportMessage) { exportMessageLabel!!.setText(it) }
        bindLabel(viewModel::currentCredits, creditsLabel!!)
    }

    override fun hide() {}
    override fun dispose() { previewActor.dispose() }
}
