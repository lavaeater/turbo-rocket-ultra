package physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Contact
import com.badlogic.gdx.physics.box2d.Fixture
import ecs.components.PlayerComponent
import ktx.ashley.has
import ktx.ashley.mapperFor

fun Contact.isEntityContact(): Boolean {
    return this.fixtureA.body.userData is Entity && this.fixtureB.body.userData is Entity
}

fun Fixture.getEntity() : Entity {
    return this.body.userData as Entity
}

inline fun <reified T: Component>Contact.hasComponent():Boolean {
    if(this.isEntityContact()) {
        val mapper = mapperFor<T>()
        return this.fixtureA.getEntity().has(mapper) ||
                this.fixtureB.getEntity().has(mapper)
    }
    return false
}

inline fun <reified T:Component> Contact.getEntityFor(): Entity {
    val mapper = mapperFor<T>()
    val entityA = this.fixtureA.getEntity()
    val entityB = this.fixtureB.getEntity()
    return if(entityA.has(mapper)) entityA else entityB
}

fun Contact.isPlayerContact(): Boolean {
    if(this.isEntityContact()) {
        return this.hasComponent<PlayerComponent>()
    }
    return false
}