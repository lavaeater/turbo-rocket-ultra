package messaging

import com.badlogic.gdx.math.Vector2
import ecs.components.player.ComplexActionComponent
import ecs.components.player.PlayerControlComponent

sealed class Message {
    class ShowProgressBar(val maxTime: Float, val worldPosition: Vector2, val progress: () -> Float) : Message()
    class ShowToast(val toast: String, val worldPosition: Vector2) : Message()
    class ShowUiForComplexAction(
        val complexActionComponent: ComplexActionComponent,
        val controlComponent: PlayerControlComponent,
        val worldPosition: Vector2
    ) : Message()
    class FactUpdated(val key: String): Message()
    class LevelComplete(val completeMessage: String): Message()
    class LevelFailed(val failMessage: String): Message()
    class LevelStarting(val beforeStartMessage: String): Message()
}