package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.SpriteComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf
import physics.*
import tru.AnimState
import tru.Assets


class WeaponUpdateSystem: IteratingSystem(
    allOf(
        WeaponComponent::class,
        AnimatedCharacterComponent::class,
        SpriteComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val weaponComponent =entity.weapon()
        val weapon = weaponComponent.currentWeapon
        val animatedCharacterComponent = entity.animation()
        val spriteComponent = entity.sprite()
        if(animatedCharacterComponent.currentAnimState == AnimState.Aiming)  {
            spriteComponent.extraSprites["gun"] = Assets.weapons[weapon.textureName]!![animatedCharacterComponent.currentDirection]!!
        } else {
            spriteComponent.extraSprites.remove("gun")
        }
    }
}