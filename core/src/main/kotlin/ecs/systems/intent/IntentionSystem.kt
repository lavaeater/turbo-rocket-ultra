package ecs.systems.intent

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.SpriteComponent
import ecs.components.intent.IntendsTo
import ecs.components.intent.IntentComponent
import ecs.components.player.Buildable
import ecs.systems.graphics.CompassDirection
import ecs.systems.tileWorldX
import ecs.systems.tileWorldY
import ktx.ashley.allOf
import ktx.ashley.entity
import ktx.ashley.remove
import ktx.ashley.with
import ktx.math.vec2
import map.grid.GridMapSection
import physics.*

class IntentionSystem: IteratingSystem(allOf(IntentComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        when(entity.intent()) {
            IntendsTo.EnterBuildMode -> enterBuildMode(entity)
            IntendsTo.DoNothing -> {}
            IntendsTo.Build -> build(entity)
            IntendsTo.LeaveBuildMode -> leaveBuildMode(entity)
        }
        entity.remove<IntentComponent>()
    }

    private fun leaveBuildMode(entity:Entity) {
        TODO("Not yet implemented")
    }

    private fun build(entity: Entity) {
        TODO("Not yet implemented")
    }

    private fun enterBuildMode(entity: Entity) {
        val builderTransform = entity.transform()
        val builderPosition  = builderTransform.position
        val control = entity.playerControl()
        val cursorEntity = engine.entity {
            with<CalculatedPositionComponent> {
                calculate = {
                    val cursorOffset = CompassDirection.directionOffsets[control.compassDirection]!!
                    holderVector.x = builderPosition.tileWorldX() + (cursorOffset.x * GridMapSection.scaledWidth)
                    holderVector.y = builderPosition.tileWorldY() + (cursorOffset.y * GridMapSection.scaledHeight)
                    holderVector
                }
            }
            /*
            For now, we will just add a sprite as a
            test to the proceedings
             */
            with<SpriteComponent> {
                sprite = Buildable.MachineGunTower.sprite
            }
        }
    }
}

class CalculatedPositionComponent : Component, Pool.Poolable {
    val holderVector = vec2()
    var calculate: () -> Vector2 = {Vector2.Zero.cpy()}
    override fun reset() {
        calculate = { Vector2.Zero.cpy() }
        holderVector.setZero()
    }
}

class CalculatePositionSystem : IteratingSystem(allOf(CalculatedPositionComponent::class, TransformComponent::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        transform.position.set(entity.getCalculatedPosition())
    }
}