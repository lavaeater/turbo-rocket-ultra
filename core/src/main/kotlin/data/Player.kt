package data

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import ecs.components.gameplay.ObjectiveComponent
import tru.*

class Player() {
    var currentWeapon = ""
    var kills = 0
    set(value) {
        field = value
        score += value * 10
    }
    var score = 0
    lateinit var body: Body
    lateinit var entity: Entity
    var selectedCharacterSpriteName = Assets.playerCharacters.keys.first()
    val selectedSprite get() = Assets.playerCharacters[selectedCharacterSpriteName]!!
    var currentAnimState: AnimState = AnimState.Idle
    var currentSpriteDirection : SpriteDirection = SpriteDirection.South
    val startingHealth = 100
    private val startingLives = 3

    var lives = startingLives
    var health: Int = startingHealth
    set(value) {
        field = value.coerceAtLeast(0)
    }

    val touchedObjectives = mutableSetOf<ObjectiveComponent>()
    fun touchObjective(objective: ObjectiveComponent) {
        if(touchedObjectives.add(objective)) {
            score += 100
        }
    }

    var ammoLeft = 0
    var totalAmmo = 0

    val isDead : Boolean
        get() = health < 1

    fun reset() {
        lives = startingLives
        health = startingHealth
        kills = 0
        touchedObjectives.clear()
    }
}