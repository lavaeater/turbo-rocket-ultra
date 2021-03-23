package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.BodyComponent
import ecs.components.EnemyComponent
import ecs.components.TransformComponent
import factories.world
import ktx.ashley.allOf
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.math.random
import ktx.math.vec2
import physics.*

class EnemyControlSystem : IteratingSystem(
    allOf(
        EnemyComponent::class,
        BodyComponent::class,
        TransformComponent::class
    ).get()
) {

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = entity.getComponent<EnemyComponent>()
        val bodyComponent = entity.getComponent<BodyComponent>()
        val transformComponent = entity.getComponent<TransformComponent>()
        enemyComponent.coolDown(deltaTime)

        when (enemyComponent.state) {
            EnemyState.ChasePlayer -> chasePlayer(enemyComponent, transformComponent)
            EnemyState.Ambling -> amble(enemyComponent)
            EnemyState.Seeking -> seek(entity, enemyComponent, bodyComponent)
            EnemyState.FollowAFriend -> followAFriend(enemyComponent, transformComponent)
        }

        when (enemyComponent.state) {
            EnemyState.ChasePlayer -> moveEnemy(enemyComponent, bodyComponent, 3.5f)
            EnemyState.FollowAFriend -> moveEnemy(enemyComponent, bodyComponent)
            EnemyState.Ambling -> moveEnemy(enemyComponent, bodyComponent)
            EnemyState.Seeking -> {
            }
        }
    }

    private fun moveEnemy(enemyComponent: EnemyComponent, bodyComponent: BodyComponent, speed: Float = 2.5f) {
        bodyComponent.body.setLinearVelocity(
            enemyComponent.directionVector.x * speed,
            enemyComponent.directionVector.y * speed
        )
    }

    private fun amble(enemyComponent: EnemyComponent) {
        if (enemyComponent.timeRemaining <= 0f)
            enemyComponent.newState(EnemyState.Seeking)
    }

    val fieldOfView = 180
    val scanResolution = 1

    @ExperimentalStdlibApi
    private fun seek(entity: Entity, enemyComponent: EnemyComponent, bodyComponent: BodyComponent) {
        //Pick a random direction
        if (enemyComponent.needsScanVector) {
            val unitVectorRange = -1f..1f
            enemyComponent.directionVector.set(Vector2.Zero)
            enemyComponent.scanVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
            enemyComponent.scanVector.setAngleDeg(enemyComponent.scanVector.angleDeg() - 45f)
            enemyComponent.maxNumberOfScans = fieldOfView / scanResolution //one scan per degree of angle, easy peasy

            enemyComponent.needsScanVector = false
            enemyComponent.keepScanning = true
        }
        /*
        Do some intricate raycasting in that direction to see if we find the player there...
        like, simply create a second vector starting with the unit vector, then rotate it first slightly left,
        then iterate over increments of angles until we have surpassed 90 degrees, then we're done
        Hey, and also, we do this every update, not all in one go, so that we can actually see it happening
         */

        enemyComponent.scanVectorStart.set(bodyComponent.body.position)
        var lowestFraction = 1f
        var foundPlayer = false
        lateinit var closestFixture: Fixture
        val pointOfHit = vec2()
        val hitNormal = vec2()

        if (enemyComponent.keepScanning) {
            enemyComponent.scanCount++
            if (enemyComponent.scanCount > enemyComponent.maxNumberOfScans) {
                enemyComponent.keepScanning = false
                enemyComponent.scanCount = 0
            }

            enemyComponent.scanVectorEnd.set(enemyComponent.scanVectorStart)
                .add(enemyComponent.scanVector)
                .sub(enemyComponent.scanVectorStart)
                .scl(30f)
                .add(enemyComponent.scanVectorStart)
                .add(enemyComponent.scanVector)

            world().rayCast(
                enemyComponent.scanVectorStart,
                enemyComponent.scanVectorEnd
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
                    enemyComponent.keepScanning = false
                    enemyComponent.needsScanVector = true
                    foundPlayer = true
                    enemyComponent.newState(EnemyState.ChasePlayer)
                    enemyComponent.chaseTransform = closestFixture.getEntity().getComponent()
                } else if(closestFixture.isEntity() && closestFixture.body.isEnemy()) {
                    val friend =closestFixture.getEntity().getComponent<EnemyComponent>()
                    if(friend.state == EnemyState.ChasePlayer) {
                        enemyComponent.keepScanning = false
                        enemyComponent.needsScanVector = true
                        foundPlayer = true
                        enemyComponent.newState(EnemyState.ChasePlayer)
                        enemyComponent.chaseTransform = friend.chaseTransform
                    }
                }
            }
            enemyComponent.scanVector.setAngleDeg(enemyComponent.scanVector.angleDeg() + scanResolution)
        }
        if (!foundPlayer && !enemyComponent.keepScanning) {
            enemyComponent.needsScanVector = true
            enemyComponent.chaseTransform = null
            enemyComponent.directionVector.set(enemyComponent.scanVector)
            enemyComponent.newState(EnemyState.Ambling, (5f..30f).random())
        }
    }

    private fun followAFriend(
        enemyComponent: EnemyComponent,
        transformComponent: TransformComponent) {
        val friendPosition =
            if (enemyComponent.chaseTransform != null) enemyComponent.chaseTransform!!.position else transformComponent.position

        enemyComponent.directionVector.set(friendPosition).sub(transformComponent.position)
            .nor()
    }


    private fun chasePlayer(
        enemyComponent: EnemyComponent,
        transformComponent: TransformComponent) {
        val playerPosition =
            if (enemyComponent.chaseTransform != null) enemyComponent.chaseTransform!!.position else transformComponent.position
        val distance = vec2().set(transformComponent.position).sub(playerPosition).len2()
        if (distance < 5f)
            enemyComponent.directionVector.set(Vector2.Zero)
        else {
            enemyComponent.directionVector.set(playerPosition).sub(transformComponent.position)
                .nor()
        }
    }
}

sealed class EnemyState {
    object ChasePlayer : EnemyState()
    object Ambling : EnemyState()
    object Seeking : EnemyState()
    object FollowAFriend : EnemyState() {

    }
}
