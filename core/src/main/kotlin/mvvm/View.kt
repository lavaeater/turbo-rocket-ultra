package lavagames.towers.mvvm

import com.badlogic.gdx.utils.Disposable

interface View:Disposable {
	fun update(delta:Float)
	fun show()
	fun hide()
	fun resize(width: Int, height: Int)
}