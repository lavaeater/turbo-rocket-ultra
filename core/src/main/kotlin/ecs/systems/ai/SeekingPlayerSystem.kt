package ecs.systems.ai

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.ai.btree.Task
import com.badlogic.gdx.graphics.Color
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
import gamestate.Players
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

class SeekPlayerSystem : IteratingSystem(allOf(SeekPlayer::class).get(), 100) {
    val players by lazy { Players.players.values.map { it.entity } }
    val debug = true
    val shapeDrawer by lazy { Assets.shapeDrawer }

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

            seekComponent.scanPolygon = createScanPolygon(
                enemyPosition,
                seekComponent.scanVector,
                seekComponent.viewDistance,
                seekComponent.fieldOfView,
                seekComponent.scanResolution
            )
            seekComponent.needsScanVector = false

            // We shall now build a SCAN POLYGON
            //And to be honest, the easiest way is to make like five smaller triangles, because

        }

        if (!seekComponent.foundAPlayer && seekComponent.coolDown > 0f) {

            var lowestFraction = 1f
            lateinit var closestFixture: Fixture
            val pointOfHit = vec2()
            val hitNormal = vec2()

            seekComponent.scanCount++
            for (player in playersInRange) {
                if (!seekComponent.foundAPlayer) {
                    val playerPosition = player.getComponent<TransformComponent>().position

//                    seekComponent.scanVectorStart.set(enemyPosition)
//
//                    val scanVectorRotated = seekComponent.scanVector.cpy()
//                        .setAngleDeg(seekComponent.scanVector.angleDeg() - seekComponent.fieldOfView / 2)
//
//                    seekComponent.scanVectorEnd.set(seekComponent.scanVectorStart)
//                        .add(scanVectorRotated)
//                        .sub(seekComponent.scanVectorStart)
//                        .scl(seekComponent.viewDistance)
//                        .add(seekComponent.scanVectorStart)
//                        .add(scanVectorRotated)
//
//                    scanVectorRotated.setAngleDeg(scanVectorRotated.angleDeg() + seekComponent.fieldOfView)
//
//                    val end2 = vec2().set(seekComponent.scanVectorStart)
//                        .add(scanVectorRotated)
//                        .sub(seekComponent.scanVectorStart)
//                        .scl(seekComponent.viewDistance)
//                        .add(seekComponent.scanVectorStart)
//                        .add(scanVectorRotated)


//                    val triangle = Polygon(
//                        arrayOf(
//                            seekComponent.scanVectorStart.x,
//                            seekComponent.scanVectorStart.y,
//                            seekComponent.scanVectorEnd.x,
//                            seekComponent.scanVectorEnd.y,
//                            end2.x,
//                            end2.y
//                        ).toFloatArray()
//                    )

//                    shapeDrawer.batch.use {
//                        shapeDrawer.filledTriangle(
//                            seekComponent.scanVectorStart,
//                            seekComponent.scanVectorEnd,
//                            end2,
//                            Color(0f, 1f, 0f, .5f)
//                        )
//                    }

                    if (seekComponent.scanPolygon.contains(playerPosition)) {
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

                        shapeDrawer.batch.use {
                            shapeDrawer.setColor(Color(0f, 1f, 0f, 0.5f))
                            shapeDrawer.filledPolygon(seekComponent.scanPolygon.vertices)
                            shapeDrawer.line(enemyPosition, pointOfHit, Color.RED, .5f)
                        }
                        if (closestFixture.isPlayer()) {
                            seekComponent.foundAPlayer = true
//                            seekComponent.foundPlayer = player
                            entity.add(
                                engine.createComponent(TrackingPlayer::class.java)
                                    .apply { this.player = player.getComponent<PlayerComponent>().player })
                            seekComponent.status = Task.Status.SUCCEEDED
                            return
                        }
                    }
//
//                    val relativePosNormalized = playerPosition.cpy().sub(enemyPosition).nor()
//                    val dotProduct = seekComponent.scanVector.dot(relativePosNormalized)
//                    val acos = acos(dotProduct)
//                    if (acos < seekComponent.fieldOfView / 2
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

    fun createScanPolygon(
        start: Vector2,
        viewDirection: Vector2,
        viewDistance: Float,
        fov: Float,
        step: Float
    ): Polygon {
        val numberOfSteps = (fov / step).toInt()

        val direction = viewDirection.cpy().setAngleDeg(viewDirection.angleDeg() - (fov / 2) - step)
        val points = mutableListOf<Vector2>()
        points.add(start)
        for (i in 0..numberOfSteps) {
            direction.setAngleDeg(direction.angleDeg() + step)
            val pointToAdd = vec2(start.x, start.y)
                .add(direction)
                .sub(start)
                .scl(viewDistance)
                .add(start)
                .add(direction)
            points.add(pointToAdd)
        }
        val floatArray = points.map { listOf(it.x, it.y) }.flatten().toFloatArray()
        val returnPolygon = Polygon(floatArray)
        return returnPolygon
    }

}