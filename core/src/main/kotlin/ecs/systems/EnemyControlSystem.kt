package ecs.systems

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.BodyComponent
import ecs.components.EnemyComponent
import ecs.components.SeesPlayerComponent
import factories.world
import ktx.ashley.allOf
import ktx.ashley.get
import ktx.ashley.has
import ktx.ashley.mapperFor
import ktx.box2d.RayCast
import ktx.box2d.rayCast
import ktx.math.random
import ktx.math.vec2
import physics.getComponent
import physics.getEntity
import physics.isEntity
import physics.isPlayer

class EnemyControlSystem : IteratingSystem(allOf(EnemyComponent::class, BodyComponent::class).get()) {

    val ecMapper = mapperFor<EnemyComponent>()
    val bcMapper = mapperFor<BodyComponent>()
    val spMapper = mapperFor<SeesPlayerComponent>()

    @ExperimentalStdlibApi
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val enemyComponent = entity[ecMapper]!!
        val bodyComponent = entity[bcMapper]!!
        enemyComponent.coolDown(deltaTime)
        if (entity.has(spMapper)) {
            val sp = entity[spMapper]!!
            if (sp.shouldUpdateState) {
                enemyComponent.newState(EnemyState.ChasePlayer)
                sp.shouldUpdateState = false
            }
        } else {
            enemyComponent.newState(EnemyState.Ambling, (5f..15f).random())
        }

        when (enemyComponent.state) {
            EnemyState.ChasePlayer -> chasePlayer(enemyComponent, bodyComponent, entity[spMapper]!!)
            EnemyState.Ambling -> amble(enemyComponent)
            EnemyState.Seeking -> seek(entity, enemyComponent, bodyComponent)
        }

        when (enemyComponent.state) {
            EnemyState.ChasePlayer, EnemyState.Ambling -> moveEnemy(enemyComponent, bodyComponent)
            EnemyState.Seeking -> {}
        }
    }

    private fun moveEnemy(enemyComponent: EnemyComponent, bodyComponent: BodyComponent, speed: Float = 10f) {
        bodyComponent.body.setLinearVelocity(
            enemyComponent.directionVector.x * speed,
            enemyComponent.directionVector.y * speed
        )
    }

    private fun amble(enemyComponent: EnemyComponent) {
        if (enemyComponent.timeRemaining == 0f)
            enemyComponent.newState(EnemyState.Seeking)
    }

    @ExperimentalStdlibApi
    private fun seek(entity: Entity, enemyComponent: EnemyComponent, bodyComponent: BodyComponent) {
        //Pick a random direction
        val unitVectorRange = -1f..1f
        enemyComponent.directionVector.set(unitVectorRange.random(), unitVectorRange.random()).nor()
        val scanVector = vec2().set(enemyComponent.directionVector)
        /*
        Do some intricate raycasting in that direction to see if we find the player there...
        like, simply create a second vector starting with the unit vector, then rotate it first slightly left,
        then iterate over increments of angles until we have surpassed 90 degrees, then we're done
         */
        scanVector.setAngleDeg(scanVector.angleDeg() - 45f)

        val endAngle = scanVector.angleDeg() + 90f
        val start = vec2().set(bodyComponent.body.position)
        val end = vec2()
        var lowestFraction = 1f
        var keepScanning = true
        var foundPlayer = false
        lateinit var closestFixture: Fixture
        val pointOfHit = vec2()
        val hitNormal = vec2()

        while (keepScanning) {
            if (scanVector.angleDeg() < endAngle)
                keepScanning = false

            end.set(start)
                .add(scanVector)
                .sub(start)
                .scl(15f)
                .add(start)
                .add(scanVector)

            world().rayCast(start, end) { fixture, point, normal, fraction ->

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
                    keepScanning = false
                    foundPlayer = true
                    entity.add(SeesPlayerComponent(closestFixture.getEntity().getComponent()))
                    enemyComponent.newState(EnemyState.ChasePlayer)
                }
            }
            scanVector.setAngleDeg(scanVector.angleDeg() + 5f)
        }
        if (!foundPlayer) {
            enemyComponent.newState(EnemyState.Ambling, (5f..30f).random())
        }
    }

    private fun chasePlayer(
        enemyComponent: EnemyComponent,
        bodyComponent: BodyComponent,
        seesPlayerComponent: SeesPlayerComponent
    ) {
        val distance = vec2().set(bodyComponent.body.position).sub(seesPlayerComponent.playerPosition).len2()
        if (distance < 5f)
            enemyComponent.directionVector.set(Vector2.Zero)
        else {
            enemyComponent.directionVector.set(seesPlayerComponent.playerPosition).sub(bodyComponent.body.position)
                .nor()
        }
    }
}

sealed class EnemyState {
    object ChasePlayer : EnemyState()
    object Ambling : EnemyState()
    object Seeking : EnemyState()
}
