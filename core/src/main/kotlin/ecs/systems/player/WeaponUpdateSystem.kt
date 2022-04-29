package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.graphics.AnimatedCharacterComponent
import ecs.components.graphics.TextureComponent
import ecs.components.player.WeaponComponent
import ktx.ashley.allOf
import physics.getComponent
import tru.AnimState
import tru.Assets


class WeaponUpdateSystem: IteratingSystem(
    allOf(
        WeaponComponent::class,
        AnimatedCharacterComponent::class,
        TextureComponent::class).get()) {

    @OptIn(ExperimentalStdlibApi::class)
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val weaponComponent = entity.getComponent<WeaponComponent>()
        val animatedCharacterComponent = entity.getComponent<AnimatedCharacterComponent>()
        val textureComponent = entity.getComponent<TextureComponent>()
        if(animatedCharacterComponent.currentAnimState == AnimState.Aiming)  {
            textureComponent.extraTextures["gun"] = Assets.weapons[weaponComponent.currentGun.textureName]!![animatedCharacterComponent.currentDirection]!!
        } else {
            textureComponent.extraTextures.remove("gun")
        }
    }
}