package ui.mvvm

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.graphics.OrthographicCamera
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.scenes.scene2d.Actor
import com.badlogic.gdx.scenes.scene2d.Group
import com.badlogic.gdx.scenes.scene2d.Stage
import com.badlogic.gdx.scenes.scene2d.ui.Button
import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener
import com.badlogic.gdx.utils.viewport.ExtendViewport
import kotlin.reflect.KProperty0

abstract class ViewBase<VM: ViewModelBase>(private val batch: Batch, val viewModel:VM, private val handleInput: Boolean = true):
	View {
	private var needsLayout = true
	private var needsDataBinding = true
	private val bindingMap = mutableMapOf<String, (Any) -> Unit>()
	private val commandMap = mutableMapOf<String, () -> Unit>()
	private val changeListener = object: ChangeListener() {
		override fun changed(event: ChangeEvent, actor: Actor) {
			when(actor) {
				is CommandWidget -> actor.command.invoke()
			}
		}
	}
	private val camera = OrthographicCamera()
	private val hudViewPort = ExtendViewport(uiWidth, uiHeight, camera)
	protected val stage = Stage(hudViewPort, batch)

	abstract fun initLayout()
	private fun dataBind() {
		if(needsDataBinding) {
			for (actor in stage.actors) {
				wireActor(actor)
			}
			viewModel.addPropertyChangedHandler { propertyName, newValue -> updateWidgetValue(propertyName, newValue) }
			needsDataBinding = false
		}
	}

	private fun wireActor(actor: Actor) {
		when (actor) {
			is BindableWidget -> bindViewModelProperty(actor)
			is CommandWidget -> actor.addListener(changeListener)
		}
		if(actor is Group) {
			for (c in actor.children) {
				wireActor(c)
			}
		}
	}

	override fun show() {
		if(handleInput)
			Gdx.input.inputProcessor = stage

		layout()
		dataBind()
	}

	private fun layout() {
		if(needsLayout) {
			initLayout()
			needsLayout = false
		}
	}

	private fun updateWidgetValue(propertyName: String, newValue: Any) {
		bindingMap[propertyName]?.invoke(newValue) //null safe call - we don't bind to all properties!
	}

	private fun bindViewModelProperty(actor: BindableWidget) {
		bindingMap[actor.propertyName] = actor::updateValue
		viewModel::class.members.first {it.name == actor.propertyName}.call((viewModel))?.let { actor.updateValue(it) }
	}

	override fun update(delta: Float) {
		batch.projectionMatrix = stage.camera.combined
		stage.act(delta)
		stage.draw()
	}

	override fun resize(width: Int, height: Int) {
		hudViewPort.update(width, height)
		hudViewPort.apply()
		batch.projectionMatrix = stage.camera.combined
	}

	/**
	 * Type-safe property binding. Registers [update] to be called immediately with the
	 * current value and again whenever the ViewModel property changes.
	 *
	 * Call this from [initLayout] as an alternative to [BindableLabel] when you want
	 * compile-time safety over the reflection-based approach.
	 *
	 * Example:
	 *   bind(viewModel::currentCategoryName) { categoryLabel.setText(it) }
	 */
	protected fun <T : Any> bind(prop: KProperty0<T>, update: (T) -> Unit) {
		update(prop.get())
		viewModel.addPropertyChangedHandler { name, value ->
			@Suppress("UNCHECKED_CAST")
			if (name == prop.name) update(value as T)
		}
	}

	/** Convenience: bind a String ViewModel property to a Scene2D [Label]'s text. */
	protected fun bindLabel(prop: KProperty0<String>, label: Label) =
		bind(prop) { label.setText(it) }

	/** Convenience: bind a Boolean ViewModel property to a [Button]'s enabled state. */
	protected fun bindEnabled(prop: KProperty0<Boolean>, button: Button) =
		bind(prop) { button.isDisabled = !it }

	companion object {
		private const val aspectRatio = 16 / 9
		const val uiWidth = 640f
		const val padding = uiWidth / 100

		const val uiHeight = 480f
	}
}