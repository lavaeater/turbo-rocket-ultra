package physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Batch
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import data.Player
import eater.ecs.components.AgentProperties
import eater.physics.*
import ecs.components.enemy.EnemySensorComponent
import ecs.components.enemy.TackleComponent
import ecs.components.gameplay.*
import ecs.components.pickups.LootComponent
import ecs.components.player.ComplexActionComponent
import ecs.components.player.PlayerComponent
import ecs.components.player.PlayerControlComponent
import ecs.components.player.PlayerWaitsForRespawn
import ktx.ashley.has

fun Entity.playerControlComponent(): PlayerControlComponent {
    return this.getComponent()
}

fun Body.isEnemy(): Boolean {
    return (userData as Entity).has<AgentProperties>()
}

fun Body.isPlayer(): Boolean {
    return (userData as Entity).has<PlayerComponent>()
}

fun Body.playerComponent(): PlayerComponent {
    return (userData as Entity).getComponent()
}

fun Body.player(): Player {
    return (userData as Entity).getComponent<PlayerComponent>().player
}

fun Fixture.isPlayer(): Boolean {
    return this.body.userData is Entity && (this.body.isPlayer())
}

inline fun <reified T : Component> Contact.atLeastOneHas(): Boolean {
    if (this.eitherIsEntity()) {
        return if (this.fixtureA.isEntity() && this.fixtureA.getEntity()
                .has<T>()
        ) true else this.fixtureB.isEntity() && this.fixtureB.getEntity().has<T>()
    }
    return false
}

inline fun <reified T : Component> Contact.justOneHas(): Boolean {
        val fOne = this.fixtureA.isEntity() && this.fixtureA.getEntity()
            .has<T>()
    val fTwo = this.fixtureB.isEntity() && this.fixtureB.getEntity().has<T>()
    return fOne xor fTwo
}

inline fun<reified T: Component> getEntityThatHas(entityOne: Entity, entityTwo: Entity): Entity {
    return if(entityOne.has<T>()) entityOne else entityTwo
}
inline fun <reified T : Component> Contact.bothHaveComponent(): Boolean {
    if (this.bothAreEntities()) {
        val mapper = AshleyMapperStore.getMapper<T>()
        return this.fixtureA.getEntity().has(mapper) &&
                this.fixtureB.getEntity().has(mapper)
    }
    return false
}

fun Contact.getPlayerFor(): Player {
    return this.getEntityFor<PlayerComponent>().getComponent<PlayerComponent>().player
}

fun Contact.getOtherEntity(entity: Entity): Entity {
    val entityA = this.fixtureA.getEntity()
    return if (entityA == entity) this.fixtureB.getEntity() else entityA
}

inline fun <reified T : Component> Contact.getEntityFor(): Entity {
    return if(this.fixtureA.isEntity() && this.fixtureA.getEntity().has<T>()) this.fixtureA.getEntity() else this.fixtureB.getEntity()
}

fun Contact.isPlayerByPlayerContact(): Boolean {
    return this.bothAreEntities() && this.bothHaveComponent<PlayerComponent>()
}

fun Contact.eitherIsSensor() : Boolean {
    return this.fixtureA.isSensor || this.fixtureB.isSensor
}

fun Contact.isPlayerContact(): Boolean {
    if (this.bothAreEntities()) {
        return this.atLeastOneHas<PlayerComponent>()
    }
    return false
}

fun Float.toDegrees(): Float {
    return this * MathUtils.radiansToDegrees
}

fun Float.to360Degrees(): Float {
    var rotation = this.toDegrees() % 360
    if (rotation < 0f)
        rotation += 360f
    if (rotation > 360f)
        rotation -= 360f
    return rotation
}

fun Batch.drawScaled(
    textureRegion: TextureRegion,
    x: Float,
    y: Float,
    scaleX: Float = 1f,
    scaleY: Float = 1f,
    rotation: Float = 180f
) {
    draw(
        textureRegion,
        x,
        y,
        0f,
        0f,
        textureRegion.regionWidth.toFloat(),
        textureRegion.regionHeight.toFloat(),
        scaleX,
        scaleY,
        rotation
    )
}

fun Batch.drawScaled(
    textureRegion: TextureRegion,
    x: Float,
    y: Float,
    scale: Float = 1f,
    rotation: Float = 180f
) {

    drawScaled(textureRegion, x, y, scale, scale, rotation)

}

fun Contact.thisIsAContactBetween(): ContactType {
    if(this.justOneHas<AgentProperties>()) {
        val entity = this.getEntityFor<AgentProperties>()
        val otherFixture = if(this.fixtureA.isEntity() && entity == this.fixtureA.getEntity()) this.fixtureB else this.fixtureA

        if(!otherFixture.isEntity()) {
            //This is a wall, for sure
            return ContactType.EnemyAndObstacle(entity)
        } else if(otherFixture.getEntity().hasObstacle()) {
            return ContactType.EnemyAndObstacle(entity)
        }
    }
    if (this.justOneHas<DamageEffectComponent>()) {
        val damageEffectEntity = this.getEntityFor<DamageEffectComponent>()
        if (this.bothAreEntities()) {
            val otherEntity = this.getOtherEntity(damageEffectEntity)
            return if (otherEntity.has<PlayerComponent>()) {
                ContactType.PlayerAndDamage(damageEffectEntity, otherEntity)
            } else if (otherEntity.has<AgentProperties>()) {
                ContactType.EnemyAndDamage(damageEffectEntity, otherEntity)
            } else {
                ContactType.SomeEntityAndDamage(damageEffectEntity, otherEntity)
            }
        } else {
            ContactType.DamageAndWall(damageEffectEntity)
        }
    }

    if (this.isPlayerByPlayerContact()) {
        return if (this.justOneHas<PlayerWaitsForRespawn>()) {
            val deadPlayer = this.getEntityFor<PlayerWaitsForRespawn>()
            val otherPlayer = this.getOtherEntity(deadPlayer)
            ContactType.PlayerAndDeadPlayer(otherPlayer, deadPlayer)
        } else {
            ContactType.PlayerCloseToPlayer(this.fixtureA.getEntity(), this.fixtureB.getEntity())
        }
    }

    if (this.isPlayerContact()) {
        if (this.atLeastOneHas<LootComponent>()) {
            val playerEntity = this.getPlayerFor().entity
            val lootEntity = this.getEntityFor<LootComponent>()

            return ContactType.PlayerAndLoot(playerEntity, lootEntity)
        }
        if(this.atLeastOneHas<ComplexActionComponent>()) {
            val playerEntity = this.getPlayerFor().entity
            val other = this.getEntityFor<ComplexActionComponent>()
            return ContactType.PlayerAndComplexAction(playerEntity, other)
        }
        if (this.atLeastOneHas<ShotComponent>()) {
            val playerEntity = this.getPlayerFor().entity
            return ContactType.PlayerAndProjectile(this.getPlayerFor().entity, this.getOtherEntity(playerEntity))
        }
        if (this.atLeastOneHas<EnemySensorComponent>()) {
            val enemy = this.getEntityFor<EnemySensorComponent>()
            val player = this.getOtherEntity(enemy)
            return ContactType.EnemySensesPlayer(enemy, player)
        }
        if (this.atLeastOneHas<ObjectiveComponent>()) {
            val cEntity = this.getEntityFor<ObjectiveComponent>()
            return ContactType.PlayerAndObjective(this.getPlayerFor().entity, cEntity)
        }
        if (this.noSensors() && this.atLeastOneHas<TackleComponent>()) {
            val enemy = this.getEntityFor<TackleComponent>()
            val player = this.getOtherEntity(enemy)
            return ContactType.PlayerAndSomeoneWhoTackles(player, enemy)
        }
    }

    if (this.bothHaveComponent<EnemySensorComponent>()) {
        /*
        This is an enemy noticing an enemy - if that enemy is chasing the player, then both should do that!
         */
        val enemyAEntity = this.fixtureA.getEntity()
        val enemyBEntity = this.fixtureB.getEntity()
        return ContactType.EnemyAndEnemy(enemyAEntity, enemyBEntity)
    }

    if (this.atLeastOneHas<AgentProperties>() && this.atLeastOneHas<BulletComponent>()) {

        val enemy = this.getEntityFor<AgentProperties>()
        val bulletEntity = this.getEntityFor<BulletComponent>()
        return ContactType.EnemyAndBullet(enemy, bulletEntity)
    }

    if (this.atLeastOneHas<MolotovComponent>()) {
        /*
        Lets not add a new entity, let's modify the one we have
         */
        val molotov = this.getEntityFor<MolotovComponent>()
        return ContactType.MolotovHittingAnything(molotov)
    }
    if (this.atLeastOneHas<GrenadeComponent>()) {
        /*
        Lets not add a new entity, let's modify the one we have
         */
        val grenade = this.getEntityFor<GrenadeComponent>()
        return ContactType.GrenadeHittingAnything(grenade)
    }
    return ContactType.Unknown
}