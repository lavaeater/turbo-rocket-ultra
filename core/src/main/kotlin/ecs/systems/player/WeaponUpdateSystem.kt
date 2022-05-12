package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.SpriteComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf
import physics.animation
import physics.sprite
import physics.weapon
import tru.AnimState
import tru.Assets
import tru.SpriteDirection


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
            when(animatedCharacterComponent.currentDirection) {
                SpriteDirection.East -> spriteComponent.extraSprites["gun"]?.setFlip(false, false)
                SpriteDirection.North -> spriteComponent.extraSprites["gun"]?.setFlip(false, true)
                SpriteDirection.South -> spriteComponent.extraSprites["gun"]?.setFlip(false, true)
                SpriteDirection.West -> spriteComponent.extraSprites["gun"]?.setFlip(false, true)
            }
            if(weapon.handleKey != "") {
                spriteComponent.extraSpriteAnchors["gun"] = weapon.handleKey
            }
        } else {
            spriteComponent.extraSprites.remove("gun")
            spriteComponent.extraSpriteAnchors.remove("gun")
        }
    }
}