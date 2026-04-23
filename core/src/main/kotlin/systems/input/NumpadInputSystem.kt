package systems.input

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.Input
import components.TransformComponent
import components.intent.IntendsTo
import gamestate.GameEvent
import gamestate.GameState
import input.NumpadControl
import ktx.ashley.allOf
import physics.getComponent
import physics.intendTo
import statemachine.StateMachine
import dependencies.InjectionContext.Companion.inject

class NumpadInputSystem : IteratingSystem(allOf(NumpadControl::class, TransformComponent::class).get()) {
    private val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }

    override fun processEntity(entity: Entity, deltaTime: Float) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_ENTER) && gameState.currentState.state == GameState.Cutscene) {
            ui.CrawlDialog.skip()
            return
        }
        val control = entity.getComponent<NumpadControl>()

        control.thrust = when {
            Gdx.input.isKeyPressed(Input.Keys.NUMPAD_8) -> 1f
            Gdx.input.isKeyPressed(Input.Keys.NUMPAD_2) -> -1f
            else -> 0f
        }
        control.turning = when {
            Gdx.input.isKeyPressed(Input.Keys.NUMPAD_6) -> 1f
            Gdx.input.isKeyPressed(Input.Keys.NUMPAD_4) -> -1f
            else -> 0f
        }

        val aimSpeed = 150f
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_7)) control.rotateAim(-aimSpeed * deltaTime)
        if (Gdx.input.isKeyPressed(Input.Keys.NUMPAD_9)) control.rotateAim(aimSpeed * deltaTime)

        control.firing = Gdx.input.isKeyPressed(Input.Keys.NUMPAD_ENTER)

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_1)) entity.intendTo(IntendsTo.SelectPreviousWeapon)
        if (Gdx.input.isKeyJustPressed(Input.Keys.NUMPAD_3)) entity.intendTo(IntendsTo.SelectNextWeapon)
    }
}
