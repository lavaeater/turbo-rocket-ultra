package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.TextureComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf
import physics.AshleyMappers
import physics.getComponent
import tru.AnimState
import tru.Assets


class WeaponUpdateSystem: IteratingSystem(
    allOf(
        WeaponComponent::class,
        AnimatedCharacterComponent::class,
        TextureComponent::class).get()) {

    override fun processEntity(entity: Entity, deltaTime: Float) {
        val weaponComponent = AshleyMappers.weapon.get(entity)
        val weapon = weaponComponent.currentWeapon
        val animatedCharacterComponent = AshleyMappers.animatedCharacter.get(entity)
        val textureComponent = AshleyMappers.texture.get(entity)
        if(animatedCharacterComponent.currentAnimState == AnimState.Aiming)  {
            textureComponent.extraTextures["gun"] = Pair(Assets.weapons[weapon.textureName]!![animatedCharacterComponent.currentDirection]!!, 1.0f)
        } else {
            textureComponent.extraTextures.remove("gun")
        }
    }
}