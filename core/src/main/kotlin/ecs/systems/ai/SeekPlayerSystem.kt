package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.math.MathUtils.degreesToRadians
import com.badlogic.gdx.math.Polygon
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.ai.NoticedSomething
import ecs.components.ai.SeekPlayer
import ecs.components.ai.TrackingPlayer
import ecs.components.enemy.EnemyComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.player.PlayerComponent
import factories.world
import data.Players
import input.canISeeYouFromHere
import ktx.ashley.allOf
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.graphics.use
import ktx.math.random
import ktx.math.vec2
import physics.getComponent
import physics.has
import physics.isPlayer
import tru.Assets

class SeekPlayerSystem(val debug: Boolean) : IteratingSystem(allOf(SeekPlayer::class).get()) {
    val players by lazy { Players.players.values.map { it.entity } }
    val shapeDrawer by lazy { Assets.shapeDrawer }
    lateinit var closestFixture: Fixture

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val seekComponent = entity.getComponent<SeekPlayer>()
        val debugColor = Color(0f, 1f, 0f, 0.1f)

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

            seekComponent.needsScanVector = false
        }

        val scanDirection = vec2()
        if (!seekComponent.foundAPlayer && seekComponent.coolDown > 0f) {

            var lowestFraction = 1f
            val pointOfHit = vec2()
            val hitNormal = vec2()

            seekComponent.scanCount++
            for (player in playersInRange) {
                if (!seekComponent.foundAPlayer) {
                    val playerPosition = player.getComponent<TransformComponent>().position

                    if (canISeeYouFromHere(
                            enemyPosition,
                            seekComponent.scanVector,
                            playerPosition,
                            seekComponent.fieldOfView
                        )
                    ) {

                         scanDirection.set(enemyPosition.cpy().add(playerPosition.cpy().sub(enemyPosition).nor().scl(enemyComponent.viewDistance)))

                        world().rayCast(
                            enemyPosition,
                            scanDirection) { fixture, point, normal, fraction ->
                            if (fraction < lowestFraction && fixture.isPlayer()) {
                                lowestFraction = fraction
                                closestFixture = fixture
                                pointOfHit.set(point)
                                hitNormal.set(normal)
                            }
                            RayCast.CONTINUE
                        }


                        if (::closestFixture.isInitialized && closestFixture.isPlayer()) {
                            seekComponent.foundAPlayer = true
                            entity.add(
                                engine.createComponent(TrackingPlayer::class.java)
                                    .apply { this.player = player.getComponent<PlayerComponent>().player })
                            seekComponent.status = Task.Status.SUCCEEDED
                            return
                        }
                    }
                    if (debug) {
                        shapeDrawer.batch.use {
                            shapeDrawer.sector(
                                enemyPosition.x,
                                enemyPosition.y,
                                seekComponent.viewDistance,
                                seekComponent.scanVector.angleRad() - seekComponent.fieldOfView / 2 * degreesToRadians, seekComponent.fieldOfView * degreesToRadians, debugColor, debugColor)
                            shapeDrawer.line(enemyPosition, scanDirection, Color(0f, 0f, 1f, 0.3f), .2f)
                            shapeDrawer.line(enemyPosition, pointOfHit, Color(1f, 0f, 0f, 0.3f), .2f)
                        }
                    }
                }
            }
        }

        seekComponent.coolDown -= deltaTime

        seekComponent.status = if (seekComponent.coolDown > 0f)
            Task.Status.RUNNING
        else if (seekComponent.foundAPlayer)
            Task.Status.SUCCEEDED
        else
            Task.Status.FAILED
    }

    /**
     * Creates a polygon starting at the entity and creating a semi-circle, or sector
     * that corresponds to field of view. More efficient than raycasting every degree
     * but probably less efficient than doing the cross or dot product thing that I just
     * dont understand currently - I will need to set up a small test for that one.
     */


}