package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.ai.steer.behaviors.Seek
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.BodyComponent
import ecs.components.ai.ChasePlayer
import ecs.components.ai.NoticedSomething
import ecs.components.ai.SeekPlayer
import ecs.components.ai.TrackingPlayerComponent
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.PlayerWaitsForRespawn
import factories.enemy
import factories.player
import factories.world
import gamestate.Players
import injection.Context.inject
import ktx.ashley.allOf
import ktx.ashley.remove
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.graphics.use
import ktx.math.random
import ktx.math.vec2
import physics.*
import space.earlygrey.shapedrawer.ShapeDrawer
import tru.Assets
import kotlin.math.acos

class SeekingPlayerDotProductSystem : IteratingSystem(allOf(SeekPlayer::class).get()) {
    val players by lazy { Players.players.values.map { it.entity } }

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val seekComponent = entity.getComponent<SeekPlayer>()
        val enemyComponent = entity.getComponent<EnemyComponent>()
        val enemyPosition = entity.getComponent<TransformComponent>().position
        enemyComponent.directionVector.set(Vector2.Zero)
        val playersInRange =
            players.filter { it.getComponent<TransformComponent>().position.dst(enemyPosition) < enemyComponent.viewDistance }
        if (!playersInRange.any()) {
            seekComponent.status = Task.Status.FAILED
            return
        }
        if (seekComponent.needsScanVector) {
            if (entity.has<NoticedSomething>()) {
                val noticeVector = entity.getComponent<NoticedSomething>().noticedWhere
                seekComponent.scanVector.set(noticeVector).sub(seekComponent.scanVectorStart).nor()
            } else {
                val unitVectorRange = -1f..1f
                seekComponent.scanVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
            }
            //set the scanvector angle to itself minus half the field of view
            seekComponent.scanVector.setAngleDeg(seekComponent.scanVector.angleDeg() - seekComponent.fieldOfView / 2)
            seekComponent.needsScanVector = false
        }

        var lowestFraction = 1f
        lateinit var closestFixture: Fixture
        val pointOfHit = vec2()
        val hitNormal = vec2()

        seekComponent.scanCount++
        for (player in playersInRange) {
            if (!seekComponent.foundAPlayer) {
                val playerPosition = player.getComponent<TransformComponent>().position
                val relativePosNormalized = playerPosition.cpy().sub(enemyPosition).nor()
                val dotProduct = seekComponent.scanVector.dot(relativePosNormalized)
                if (acos(dotProduct) < seekComponent.fieldOfView / 2) {

                    world().rayCast(
                        enemyPosition,
                        playerPosition
                    ) { fixture, point, normal, fraction ->
                        if (fraction < lowestFraction && !fixture.isSensor) {
                            lowestFraction = fraction
                            closestFixture = fixture
                            pointOfHit.set(point)
                            hitNormal.set(normal)
                        }
                        RayCast.CONTINUE
                    }
                    if (closestFixture.isPlayer()) {
                        seekComponent.foundAPlayer = true
                        entity.add(
                            engine.createComponent(TrackingPlayerComponent::class.java)
                                .apply { this.player = player.getComponent<PlayerComponent>().player })
                    }
                }
            }
        }

        seekComponent.status = if (seekComponent.foundAPlayer) Task.Status.SUCCEEDED else Task.Status.FAILED
    }

}

class SeekingPlayerSystem : IteratingSystem(allOf(SeekPlayer::class).get()) {
    val debug = false
    val shapeDrawer by lazy { Assets.shapeDrawer }

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val seekComponent = entity.getComponent<SeekPlayer>()
        if (seekComponent.status == Task.Status.RUNNING) {
            val enemyComponent = entity.getComponent<EnemyComponent>()

            seekComponent.fieldOfView = enemyComponent.fieldOfView
            seekComponent.viewDistance = enemyComponent.viewDistance
            enemyComponent.directionVector.set(Vector2.Zero)
            seek(entity, seekComponent, entity.getComponent())
        }
    }

    @ExperimentalStdlibApi
    private fun seek(entity: Entity, seekComponent: SeekPlayer, bodyComponent: BodyComponent) {
        //Pick a random direction
        seekComponent.scanVectorStart.set(bodyComponent.body!!.position)
        if (seekComponent.needsScanVector) {

            if (entity.has<NoticedSomething>()) {
                val noticeVector = entity.getComponent<NoticedSomething>().noticedWhere
                seekComponent.scanVector.set(noticeVector).sub(seekComponent.scanVectorStart).nor()
            } else {
                val unitVectorRange = -1f..1f
                seekComponent.scanVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
            }
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
                .scl(seekComponent.viewDistance)
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
                if (closestFixture.isEntity() && closestFixture.body.isPlayer() && !closestFixture.getEntity()
                        .has<PlayerWaitsForRespawn>()
                ) {
                    seekComponent.keepScanning = false
                    entity.add(
                        engine.createComponent(TrackingPlayerComponent::class.java)
                            .apply { player = closestFixture.body.player() })
                    foundPlayer = true
                    seekComponent.status = Task.Status.SUCCEEDED

                } else if (
                    closestFixture.isEntity() &&
                    closestFixture.body.isEnemy() &&
                    closestFixture.getEntity().has<ChasePlayer>() &&
                    closestFixture.getEntity().has<TrackingPlayerComponent>()
                ) {

                    entity.addComponent<TrackingPlayerComponent> {
                        player = closestFixture.getEntity().getComponent<TrackingPlayerComponent>().player
                    }
                    seekComponent.keepScanning = false
                    foundPlayer = true
                    seekComponent.status = Task.Status.SUCCEEDED
                }
            }
            if (debug) {
                shapeDrawer.batch.use {
                    shapeDrawer.line(seekComponent.scanVectorStart, seekComponent.scanVectorEnd, Color.RED)
                    shapeDrawer.line(seekComponent.scanVectorStart, pointOfHit, Color.GREEN)
                }
            }
            seekComponent.scanVector.setAngleDeg(seekComponent.scanVector.angleDeg() + seekComponent.scanResolution)
        }
        if (!foundPlayer && !seekComponent.keepScanning) {
            entity.remove<TrackingPlayerComponent>()
            seekComponent.status = Task.Status.FAILED
        }
    }

}

