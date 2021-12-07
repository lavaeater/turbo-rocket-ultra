package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.graphics.Color
import ecs.components.gameplay.AnchorPointsComponent
import ecs.components.gameplay.TransformComponent
import ktx.ashley.allOf
import ktx.graphics.use
import ktx.math.random
import ktx.math.vec2
import physics.anchors
import physics.playerControl
import physics.transform
import tru.Assets




class AnchorPointTransformationSystem(private val debug: Boolean) :
    IteratingSystem(allOf(TransformComponent::class, AnchorPointsComponent::class).get()) {

    private val shapeDrawer by lazy { Assets.shapeDrawer }
    private val colorMap = mutableMapOf("blue" to Color.BLUE, "red" to Color.RED, "green" to Color.GREEN, "yellow" to Color.YELLOW)
    private val r = 0f..1f

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val transform = entity.transform()
        val anchors = entity.anchors()
        for ((key, point) in anchors.points) {
            if (!anchors.transformedPoints.containsKey(key))
                anchors.transformedPoints[key] = vec2()
            /*
            Every point represents an OFFSET from the transform point, with
            rotation around the transform point.
            So, new point position is transform.position + point.

            But what is the rotation?
             */
            val rotatedPoint = if(anchors.useDirectionVector) point.cpy().rotateRad(entity.playerControl().directionVector.angleRad()) else point.cpy().rotateRad(transform.rotation)
            anchors.transformedPoints[key]!!.set(transform.position).add(rotatedPoint.x, rotatedPoint.y / 2)
        }
        if(debug)
            shapeDrawer.batch.use {
                for ((key, point) in anchors.transformedPoints) {
                    if(!colorMap.containsKey(key))
                        colorMap[key] = Color(r.random(), r.random(), r.random(),1f)

                    shapeDrawer.filledCircle(point, 0.2f, colorMap[key]!!)
                }
            }
    }
}