package twodee.ecs.ashley.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.physics.box2d.Body
import twodee.ecs.ashley.components.Box2d
import twodee.ecs.ashley.components.CameraFollow
import twodee.world.ITileManager
import twodee.world.tileX
import twodee.world.tileY
import ktx.ashley.allOf

fun Entity.body(): Body {
    return Box2d.get(this).body
}

class CurrentChunkSystem(private val seaManager: ITileManager, private val tileSize: Float): IteratingSystem(allOf(
    CameraFollow::class).get()) {
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val body = entity.body()
        val tileX = body.tileX(tileSize)
        val tileY = body.tileY(tileSize)
        seaManager.updateCurrentChunks(tileX, tileY)
    }
}