package ecs.systems.graphics

object GameConstants {

    const val ENEMY_ROTATION_SPEED = 45f
    const val ENEMY_BASE_HEALTH = 100f
    const val GENERAL_SPEED_FACTOR = 1f
    const val ENEMY_INVESTIGATE_SPEED = 7.5f * GENERAL_SPEED_FACTOR
    const val ENEMY_VIEW_DISTANCE = 30f
    const val ENEMY_FOV = 90f
    const val ENEMY_BASE_SPEED = 5f * GENERAL_SPEED_FACTOR
    const val ENEMY_RUSH_SPEED = 10f * GENERAL_SPEED_FACTOR
    const val PIXELS_PER_METER = 16f
    const val SCALE = 1 / PIXELS_PER_METER

    const val PLAYER_DENSITY = 1f
    const val SHIP_LINEAR_DAMPING = 20f
    const val SHIP_ANGULAR_DAMPING = 20f
    const val MAX_ENEMIES = 2000

    const val GAME_WIDTH = 48f
    const val GAME_HEIGHT = 32f

    const val PLAYER_BASE_SPEED = 15f * GENERAL_SPEED_FACTOR
    const val AIMING_SPEED_FACTOR = 0.2f
    const val NORMAL_SPEED_FACTOR = 1f

    const val ENEMY_DENSITY = .1f
    const val SHOT_DENSITY = .01f
    const val SHIP_DENSITY = .1f
    const val CAR_DENSITY = .3f
}