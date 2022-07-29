package data

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.physics.box2d.Body
import ecs.components.gameplay.ObjectiveComponent
import ecs.systems.graphics.GameConstants
import tru.AnimState
import tru.Assets
import tru.CardinalDirection

class Player(val name: String, val isAiPlayer: Boolean = false) {
    init {
        playerIndex++
    }

    var isReady = false
    var speed: Float = GameConstants.PLAYER_BASE_SPEED
    val playerId = "P$playerIndex"
    var currentWeapon = ""
    var kills = 0
    set(value) {
        field = value
        score += value * 10
    }
    val index = 0
    var score = 0
    lateinit var body: Body
    lateinit var entity: Entity
    var selectedCharacterSpriteName = Assets.playerCharacters.keys.first()
    val selectedSprite get() = Assets.playerCharacters[selectedCharacterSpriteName]!!
    var currentAnimState: AnimState = AnimState.Idle
    var currentCardinalDirection : CardinalDirection = CardinalDirection.South
    private val startingLives = 3

    var lives = startingLives

    val touchedObjectives = mutableSetOf<ObjectiveComponent>()
    fun touchObjective(objective: ObjectiveComponent) {
        if(touchedObjectives.add(objective)) {
            score += 100
        }
    }

    var ammoLeft = 0
    var totalAmmo = 0

    fun reset() {
        lives = startingLives
        kills = 0
        touchedObjectives.clear()
    }
    companion object {
        var playerIndex = 0
    }
}