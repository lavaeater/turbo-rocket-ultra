package physics

import com.badlogic.ashley.core.Entity

sealed class ContactType {
    object Unknown : ContactType()
    class EnemyAndDamage(val damageEntity: Entity, val enemy: Entity) : ContactType()
    class PlayerAndDamage(val damageEntity: Entity, val player: Entity) : ContactType()
    class SomeEntityAndDamage(val damageEntity: Entity, val otherThing: Entity) : ContactType()
    class DamageAndWall(val damageEntity: Entity) : ContactType()
    class PlayerCloseToPlayer(val playerOne: Entity, val playerTwo: Entity) : ContactType()
    class PlayerAndLoot(val player: Entity, val lootEntity: Entity) : ContactType()
    class PlayerAndProjectile(val player: Entity, val shotEntity: Entity) : ContactType()
    class EnemySensesPlayer(val enemy: Entity, val player: Entity) : ContactType()
    class PlayerAndObjective(val player: Entity, val objective: Entity) : ContactType()
    class PlayerAndDeadPlayer(val livingPlayer: Entity, val deadPlayer: Entity) : ContactType()
    class PlayerAndComplexAction(val player: Entity, val other: Entity) : ContactType()
    class PlayerAndSomeoneWhoTackles(val player: Entity, val tackler: Entity) : ContactType()
    class TwoEnemySensors(val enemyOne: Entity, val enemyTwo: Entity) : ContactType()
    class EnemyAndBullet(val enemy: Entity, val bullet: Entity) : ContactType()
    class MolotovHittingAnything(val molotov: Entity) : ContactType()
    class GrenadeHittingAnything(val grenade: Entity) : ContactType()

}