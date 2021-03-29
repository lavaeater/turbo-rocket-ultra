package ecs.systems.player

import com.badlogic.ashley.core.Entity
import com.badlogic.ashley.systems.IteratingSystem
import ecs.components.player.PlayerComponent
import ktx.ashley.allOf
import ktx.ashley.mapperFor

class PlayerDeathSystem: IteratingSystem(allOf(PlayerComponent::class).get()) {
    val mapper = mapperFor<PlayerComponent>()
    override fun processEntity(entity: Entity, deltaTime: Float) {
        val pc = mapper[entity]
        if(pc.player.isDead && pc.player.lives > 0) {
            //Some kind of respawn-component perhaps? With cooldown for respawn so you don't die immediately
            //And remove the body from the game? Hmm
            //So first the player cannot move at all, perhaps? While being dead - for a cooldown period
            //Then we remove the dead-component
            //And add the respawn-component
            //And after that we remove the respawn-component as well.
            //Respawn-blink can be handled by rendering for now.
        }
    }
}