package gamestate

class Player(var health: Int = 100) {
    val dead : Boolean
        get() = health < 1
}