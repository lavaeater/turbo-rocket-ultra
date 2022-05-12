package ecs.systems.graphics

object GameConstants {

    const val ENEMY_INVESTIGATE_SPEED = 12.5f
    const val ENEMY_VIEW_DISTANCE = 30f
    const val ENEMY_FOV = 90f
    const val PLAYER_BASE_SPEED = 20f
    const val ENEMY_BASE_SPEED = 10f
    const val ENEMY_RUN_SPEED = 15f
    const val pixelsPerMeter = 16f
    const val scale = 1 / pixelsPerMeter

    const val ENEMY_DENSITY = .1f
    const val SHOT_DENSITY = .01f
    const val SHIP_DENSITY = .1f
    const val PLAYER_DENSITY = 1f
    const val CAR_DENSITY = .3f
    const val SHIP_LINEAR_DAMPING = 20f
    const val SHIP_ANGULAR_DAMPING = 20f
    const val MAX_ENEMIES = 2000

    const val GAMEWIDTH = 48f
    const val GAMEHEIGHT = 32f
    const val AIMING_SPEED_FACTOR = 0.2f
    const val NORMAL_SPEED_FACTOR = 1f
}