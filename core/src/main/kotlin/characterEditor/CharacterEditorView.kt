package characterEditor

import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.utils.UIUtils.middle
import com.badlogic.gdx.utils.Align
import ktx.scene2d.label
import ktx.scene2d.scene2d
import ktx.scene2d.table
import ui.mvvm.ViewBase
import ui.mvvm.bindableLabel
import ui.mvvm.commandTextButton
import spritesheet.RenderableThing

class CharacterEditorView(
    batch: Batch,
    viewModel: CharacterEditorViewModel
) : ViewBase<CharacterEditorViewModel>(batch, viewModel) {

    private lateinit var renderableThing: RenderableThing

    override fun initLayout() {
        renderableThing = viewModel.getRenderableThing()
        renderableThing.setFillParent(true)

        val characterTable = scene2d.table {
            bindableLabel(viewModel::subName.name)
            row()
            bindableLabel(viewModel::currentTags.name) {
                wrap = true
            }
            center()
            width = uiWidth / 2f
            height = width
            add(renderableThing)
        }

        val commandTable = scene2d.table {
            bottom()
            middle()

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
            }.cell(width = uiWidth / 3f)

            table {
                commandTextButton("<<", viewModel::previousSpriteSheet)
                    .cell().pad(padding).left()
                bindableLabel(viewModel::currentSpriteSheetName.name) {
                    setAlignment(Align.center)
                }.cell(width = uiWidth / 4f)
                commandTextButton(">>", viewModel::nextSpriteSheet)
                    .cell().pad(padding).right()
                row()
                commandTextButton("<<", viewModel::previousCategory)
                    .cell().pad(padding).left()
                bindableLabel(viewModel::currentCategoryName.name) {
                    setAlignment(Align.center)
                }.cell(width = uiWidth / 5f)
                commandTextButton(">>", viewModel::nextCategory)
                    .cell().pad(padding).right()
                row()
                commandTextButton("Next anim", viewModel::nextAnim)
                    .cell().pad(padding).left()
                bindableLabel(viewModel::currentAnim.name) {
                    setAlignment(Align.center)
                }.cell(width = uiWidth / 5f)
            }

            table {
                commandTextButton("Export character", viewModel::exportCharacter)
                bottom()
            }

            setFillParent(true)
        }

        stage.addActor(commandTable)
        stage.addActor(characterTable)
    }

    override fun hide() {}
    override fun dispose() {}
}
