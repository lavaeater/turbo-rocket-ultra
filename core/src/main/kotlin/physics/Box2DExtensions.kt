package physics

import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.physics.box2d.Contact


fun Contact.bodyForTag(tag: String) : Body {
    return if(this.fixtureA.body.userData == tag) this.fixtureA.body else this.fixtureB.body //Always returns body B if tag is not on A, this is buggy
}

fun Contact.hasTags(tagA: String, tagB: String) : Boolean {
    val tags = this.tags()
    return (tags.first == tagA && tags.second == tagB) || (tags.first == tagB && tags.second == tagA)
}

fun Contact.tags() : Pair<String, String> {
    return Pair(this.fixtureA.body.userData as String, this.fixtureB.body.userData as String)
}

fun Contact.hasTag(tag: String) : Boolean {
    return bodies().any { it.userData == tag }
}

fun Contact.bodies(): List<Body> {
    return listOf(this.fixtureA.body, this.fixtureB.body)
}