package characterEditor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ScrollPane
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
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

    private val previewSize = 96f
    private val navButtonWidth = 24f
    private val catNameWidth = uiWidth * 0.14f
    private val sheetNameWidth = uiWidth * 0.18f

    override fun initLayout() {
        renderableThing = viewModel.getRenderableThing()
        bind(viewModel::previewVarFolder) { previewActor.variantFolder = it }

        var subNameLabel: Label?
        var currentTagsLabel: Label?
        var exportMessageLabel: Label?
        var creditsLabel: Label?

        val rootTable = scene2d.table {
            setFillParent(true)
            top().pad(padding)

            // ── Left column: character preview ───────────────────────────
            table {
                label("") { subNameLabel = this }.cell(align = Align.left)
                row()
                add(renderableThing).size(previewSize).pad(padding / 2f)
                row()
                label("") {
                    currentTagsLabel = this
                    wrap = true
                }.cell(width = previewSize + padding, align = Align.topLeft)
            }.cell(width = previewSize + padding * 2, fillY = true).top()

            // ── Middle column: variant buttons + category list + anim ─────
            table {
                // Variant selector (horizontal)
                table {
                    label("Variant: ").cell(pad = padding / 2f)
                    val variants = viewModel.availableVariants.ifEmpty { listOf("male", "female") }
                    for (variantKey in variants) {
                        commandTextButton(viewModel.variantDisplayName(variantKey), { viewModel.gender = variantKey })
                            .cell(pad = padding / 2f)
                    }
                    left()
                }.cell(colspan = 1, align = Align.left)
                row()

                // Category list with inline << >> navigation
                val categoryListTable = scene2d.table {
                    for (catName in viewModel.categoryNames) {
                        commandTextButton("<<", { viewModel.previousSpriteSheetFor(catName) })
                            .cell(width = navButtonWidth, pad = padding / 4f)
                        label(catName) {
                            setAlignment(Align.left)
                            setEllipsis(true)
                        }.cell(width = catNameWidth, align = Align.left, pad = padding / 4f)
                        boundLabel({ viewModel.selectedSheetNameFor(catName) }) {
                            setAlignment(Align.left)
                            setEllipsis(true)
                        }.cell(width = sheetNameWidth, align = Align.left, pad = padding / 4f)
                        commandTextButton(">>", { viewModel.nextSpriteSheetFor(catName) })
                            .cell(width = navButtonWidth, pad = padding / 4f)
                        commandTextButton("✕", { viewModel.clearLayerFor(catName) })
                            .cell(width = navButtonWidth, pad = padding / 4f)
                        row()
                    }
                    left().top()
                }
                val categoryScrollPane = ScrollPane(categoryListTable)
                categoryScrollPane.setScrollingDisabled(true, false)
                val listWidth = navButtonWidth * 3 + catNameWidth + sheetNameWidth + padding * 4
                add(categoryScrollPane).width(listWidth).height(uiHeight * 0.7f).top().left()
                row()

                // Animation selector
                table {
                    label("Anim: ").cell(pad = padding / 2f)
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
                    add(animBox).pad(padding / 2f)
                    left()
                }.cell(align = Align.left)
            }.cell(fillY = true).top().padLeft(padding)

            // ── Right column: thumbnail preview + export ──────────────────
            table {
                add(previewActor).size(64f).pad(padding)
                row()
                label("") {
                    exportMessageLabel = this
                    wrap = true
                    setAlignment(Align.center)
                }.cell(width = uiWidth * 0.2f)
                row()
                commandTextButton("Export", viewModel.exportCommand).cell(pad = padding)
                row()
                val credits = label("") {
                    creditsLabel = this
                    wrap = true
                    setAlignment(Align.topLeft)
                }
                val creditsScrollPane = ScrollPane(credits)
                creditsScrollPane.setScrollingDisabled(true, false)
                add(creditsScrollPane).width(uiWidth * 0.2f).height(uiHeight * 0.45f).top()
            }.cell(fillY = true).top().padLeft(padding)
        }

        stage.addActor(rootTable)

        bindLabel(viewModel::subName, subNameLabel!!)
        bind(viewModel::currentTags) { currentTagsLabel!!.setText(it) }
        bind(viewModel::exportMessage) { exportMessageLabel!!.setText(it) }
        bindLabel(viewModel::currentCredits, creditsLabel!!)
    }

    override fun hide() {}
    override fun dispose() { previewActor.dispose() }
}
