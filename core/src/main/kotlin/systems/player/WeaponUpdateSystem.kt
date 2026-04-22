package systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import components.graphics.AnimatedCharacterComponent
import components.graphics.TextureRegionComponent
import components.player.WeaponComponent
import ktx.ashley.allOf
import physics.animation
import physics.textureRegionComponent
import physics.weapon
import animation.AnimState
import animation.Assets
import lava.input.CardinalDirection

class WeaponUpdateSystem: IteratingSystem(
    allOf(
        WeaponComponent::class,
        AnimatedCharacterComponent::class,
        TextureRegionComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val weaponComponent =entity.weapon()
        val weapon = weaponComponent.currentWeapon
        val animatedCharacterComponent = entity.animation()
        val spriteComponent = entity.textureRegionComponent()
        if(animatedCharacterComponent.currentAnimState == AnimState.Aiming)  {
            spriteComponent.extraTextureRegions["gun"] = Assets.weapons[weapon.textureName]!![animatedCharacterComponent.currentDirection]!!
            when(animatedCharacterComponent.currentDirection) {
                CardinalDirection.East -> spriteComponent.extraTextureRegions["gun"]?.flip(false, false)
                CardinalDirection.North -> spriteComponent.extraTextureRegions["gun"]?.flip(false, true)
                CardinalDirection.South -> spriteComponent.extraTextureRegions["gun"]?.flip(false, true)
                CardinalDirection.West -> spriteComponent.extraTextureRegions["gun"]?.flip(false, true)
            }
            if(weapon.handleKey != "") {
                spriteComponent.extraSpriteAnchors["gun"] = weapon.handleKey
            }
        } else {
            spriteComponent.extraTextureRegions.remove("gun")
            spriteComponent.extraSpriteAnchors.remove("gun")
        }
    }
}