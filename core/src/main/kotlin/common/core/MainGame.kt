package common.core

import ktx.app.KtxGame
import ktx.app.KtxScreen

abstract class MainGame : KtxGame<KtxScreen>() {
    abstract fun goToGameSelect()

    abstract fun goToGameScreen()
    abstract fun goToGameOver()
    abstract fun gotoGameVictory()
}