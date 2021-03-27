package gamestate

import com.badlogic.gdx.Screen
import ktx.app.KtxGame
import screens.FirstScreen

class MainGame : KtxGame<Screen>() {

    override fun create() {
        addScreen(FirstScreen())
        setScreen<FirstScreen>()
    }
}