package factories

import kotlin.experimental.or

object Box2dCategories {
    const val none: Short = 0
    const val players: Short = 1
    const val enemies: Short = 2
    const val objectives: Short = 4
    const val obstacles: Short = 8
    const val enemySensors: Short = 16
    const val lights: Short = 32
    const val loot: Short = 64
    const val indicators: Short = 128
    const val bullets: Short = 256
    const val walls: Short = 512
    const val gibs: Short = 1024
    const val sensors: Short = 2048
    const val molotov: Short = 4096
    val all =
        players or enemies or objectives or obstacles or enemySensors or lights or loot or bullets or walls or gibs or molotov
    val allButSensors = players or enemies or objectives or obstacles or lights or loot or bullets or walls or gibs
    val allButLights =
        players or enemies or objectives or obstacles or enemySensors or loot or bullets or walls or gibs or molotov
    val whatGibsHit = players or enemies or walls or obstacles or loot or objectives
    val whatEnemiesHit = players or objectives or obstacles or walls or lights or bullets or gibs or sensors
    val whatPlayersHit =
        players or enemies or objectives or obstacles or walls or lights or gibs or enemySensors or indicators or loot
    val whatMolotovsHit = walls or obstacles or objectives or molotov
    val whatSensorsSense = players or enemies

    /**
     * Will this show up when hovering?
     */
    val thingsBulletsHit = objectives or obstacles or walls or enemies
}