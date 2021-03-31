package ecs.systems.ai

import ecs.components.ai.SeekPlayer
import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.BodyComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.ai.ChasePlayer
import ecs.components.ai.TrackingPlayerComponent
import factories.world
import ktx.ashley.allOf
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.math.random
import ktx.math.vec2
import physics.*

class SeekingPlayerSystem : IteratingSystem(allOf(SeekPlayer::class).get()) {
    private val mapper = mapperFor<SeekPlayer>()
    private val bodyMapper = mapperFor<BodyComponent>()
    private val eMapper = mapperFor<EnemyComponent>()

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val component = mapper.get(entity)
        if (component.status == Task.Status.RUNNING) {
            eMapper[entity].directionVector.set(Vector2.Zero)
            seek(entity, component, bodyMapper[entity])
        }
    }

    @ExperimentalStdlibApi
    private fun seek(entity: Entity, seekComponent: SeekPlayer, bodyComponent: BodyComponent) {
        //Pick a random direction
        if (seekComponent.needsScanVector) {
            val unitVectorRange = -1f..1f
            seekComponent.scanVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
            seekComponent.scanVector.setAngleDeg(seekComponent.scanVector.angleDeg() - 45f)
            seekComponent.needsScanVector = false
            seekComponent.keepScanning = true
        }
        /*
        Do some intricate raycasting in that direction to see if we find the player there...
        like, simply create a second vector starting with the unit vector, then rotate it first slightly left,
        then iterate over increments of angles until we have surpassed 90 degrees, then we're done
        Hey, and also, we do this every update, not all in one go, so that we can actually see it happening
         */

        seekComponent.scanVectorStart.set(bodyComponent.body.position)
        var lowestFraction = 1f
        var foundPlayer = false
        lateinit var closestFixture: Fixture
        val pointOfHit = vec2()
        val hitNormal = vec2()

        if (seekComponent.keepScanning) {
            seekComponent.scanCount++
            if (seekComponent.scanCount > seekComponent.maxNumberOfScans) {
                seekComponent.keepScanning = false
                seekComponent.scanCount = 0
            }

            seekComponent.scanVectorEnd.set(seekComponent.scanVectorStart)
                .add(seekComponent.scanVector)
                .sub(seekComponent.scanVectorStart)
                .scl(30f)
                .add(seekComponent.scanVectorStart)
                .add(seekComponent.scanVector)

            world().rayCast(
                seekComponent.scanVectorStart,
                seekComponent.scanVectorEnd
            ) { fixture, point, normal, fraction ->
                if (fraction < lowestFraction && !fixture.isSensor) {
                    lowestFraction = fraction
                    closestFixture = fixture
                    pointOfHit.set(point)
                    hitNormal.set(normal)
                }
                RayCast.CONTINUE
            }
            if (lowestFraction < 1f) {
                if (closestFixture.isEntity() && closestFixture.body.isPlayer()) {
                    seekComponent.keepScanning = false
                    entity.add(
                        engine.createComponent(TrackingPlayerComponent::class.java)
                            .apply { player = closestFixture.body.player() })
                    foundPlayer = true
                    seekComponent.status = Task.Status.SUCCEEDED

                } else if (
                    closestFixture.isEntity() &&
                    closestFixture.body.isEnemy() &&
                    closestFixture.getEntity().hasComponent<ChasePlayer>() &&
                    closestFixture.getEntity().hasComponent<TrackingPlayerComponent>()
                ) {

                    entity.add(engine.createComponent(TrackingPlayerComponent::class.java).apply {
                        player = closestFixture.getEntity().getComponent<TrackingPlayerComponent>().player
                    })
                    seekComponent.keepScanning = false
                    foundPlayer = true
                    seekComponent.status = Task.Status.SUCCEEDED
                }
            }
            seekComponent.scanVector.setAngleDeg(seekComponent.scanVector.angleDeg() + seekComponent.scanResolution)
        }
        if (!foundPlayer && !seekComponent.keepScanning) {
            entity.remove(TrackingPlayerComponent::class.java)
            seekComponent.status = Task.Status.FAILED
        }
    }

}

