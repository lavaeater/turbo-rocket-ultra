package ecs.systems.graphics

import com.badlogic.ashley.core.EntitySystem
import com.badlogic.gdx.graphics.Camera
import com.badlogic.gdx.graphics.g2d.Batch
import ktx.graphics.use
import ktx.math.vec2
import map.grid.GridMapManager
import map.snake.SnakeMapGenerator
import map.snake.SnakeMapManager
import tru.Assets

class RenderMapSystem(
    private val batch: Batch,
    private val camera: Camera,
    private val mapManager: GridMapManager
) : EntitySystem(0) {

    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val pixelsPerMeter = 16f
    private val scale = 1 / pixelsPerMeter
    private var animationStateTime = 0f

    private val cameraCenter = vec2()


    override fun update(deltaTime: Float) {
        cameraCenter.set(camera.position.x, camera.position.y)
        animationStateTime += deltaTime
        batch.use {
            drawMap(deltaTime)
        }
    }

    private fun drawMap(deltaTime: Float) {
        mapManager.render(batch, shapeDrawer, deltaTime)
    }
}