package screens

import com.badlogic.gdx.scenes.scene2d.ui.Table
import gamestate.Player
import input.ControlMapper
import ktx.scene2d.image
import ktx.scene2d.scene2d
import ktx.scene2d.table
import tru.Assets

class SplashScreen : UserInterfaceScreen() {
    override fun show() {
        initSplash()
        super.show()
    }

    lateinit var rootTable: Table

    private fun initSplash() {

        rootTable = scene2d.table {
            setFillParent(true)
            image(Assets.splashTexture)
            pad(10f)
        }

        stage.addActor(rootTable)
    }
}

object Players {
    val players = mutableMapOf<Player, ControlMapper>()
}

