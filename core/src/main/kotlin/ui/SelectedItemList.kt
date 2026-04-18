package ui

import com.badlogic.gdx.scenes.scene2d.ui.Label
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar
import com.badlogic.gdx.scenes.scene2d.ui.Skin
import ktx.actors.txt
import ktx.scene2d.KWidget
import ktx.scene2d.Scene2DSkin
import ktx.scene2d.Scene2dDsl
import ktx.scene2d.actor
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

fun<T> selectedItemListOf(callBack: (T)-> Unit = {}, vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList(callBack, items.toList())
    return list
}

fun<T> selectedItemListOf(vararg items: T): SelectedItemList<T> {
    val list = SelectedItemList({}, items.toList())
    return list
}

class SelectedItemList<T>(val listUpdatedCallback: (T)-> Unit, items: List<T>) : ArrayList<T>() {
    init {
        this.addAll(items)
    }

    val withSelectedItemFirst: List<T> get() {
        val item = get(selectedIndex)
        this.sortBy { it.toString() }
        val newList = mutableListOf<T>()
        var indexToAdd = indexOf(item)
        for(i in indices) {
            indexToAdd = when {
                indexToAdd < 0 -> lastIndex
                indexToAdd > lastIndex -> 0
                else -> indexToAdd
            }
            newList.add(this[indexToAdd])
            indexToAdd += 1
        }
        return newList
    }

    private var selectedIndex: Int = 0
        private set(value) {
            field = when {
                value < 0 -> this.lastIndex
                value > this.lastIndex -> 0
                else -> value
            }
        }
    val selectedItem get () = this[selectedIndex]
    fun nextItem() : T {
        selectedIndex++
        listUpdatedCallback(selectedItem)
        return selectedItem
    }
    fun previousItem() : T {
        selectedIndex--
        listUpdatedCallback(selectedItem)
        return selectedItem
    }
}


@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.boundLabel(
    noinline textFunction: () -> String,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: (@Scene2dDsl BoundLabel).(S) -> Unit = {}
): Label {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(BoundLabel(textFunction, skin), init)
}


open class BoundLabel(private val textFunction: () -> String, skin: Skin = Scene2DSkin.defaultSkin) :
    Label(textFunction(), skin) {
    override fun act(delta: Float) {
        txt = textFunction()
        super.act(delta)
    }
}

open class BoundProgressBar(
    private val valueFunction: () -> Float,
    min: Float,
    max: Float,
    stepSize: Float,
    skin: Skin = Scene2DSkin.defaultSkin
) : ProgressBar(min, max, stepSize, false, skin) {
    override fun act(delta: Float) {
        value = valueFunction()
        super.act(delta)
    }
}

@Scene2dDsl
@OptIn(ExperimentalContracts::class)
inline fun <S> KWidget<S>.boundProgressBar(
    noinline valueFunction: () -> Float,
    min: Float = 0f,
    max: Float = 1f,
    step: Float = 0.01f,
    skin: Skin = Scene2DSkin.defaultSkin,
    init: (@Scene2dDsl BoundProgressBar).(S) -> Unit = {}
): BoundProgressBar {
    contract { callsInPlace(init, InvocationKind.EXACTLY_ONCE) }
    return actor(BoundProgressBar(valueFunction, min, max, step, skin), init)
}