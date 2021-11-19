package ecs.systems.graphics

object GameConstants {

    const val pixelsPerMeter = 16f
    const val scale = 1 / pixelsPerMeter

    const val ENEMY_DENSITY = .1f
    const val SHOT_DENSITY = .01f
    const val SHIP_DENSITY = .1f
    const val PLAYER_DENSITY = 1f
    const val CAR_DENSITY = .3f
    const val SHIP_LINEAR_DAMPING = 20f
    const val SHIP_ANGULAR_DAMPING = 20f
    const val MAX_ENEMIES = 512

    const val GAMEWIDTH = 64f
    const val GAMEHEIGHT = 48f
}