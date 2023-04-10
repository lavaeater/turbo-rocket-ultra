package screens.concept

import eater.turbofacts.Factoids
import eater.turbofacts.factsOfTheWorld

object CounterObject {
    val factsOfTheWorld by lazy { factsOfTheWorld() }
    fun setValue(key: String, value: Int) { factsOfTheWorld.setIntFact(value, key) }

    fun getValue(key:String) : Int = factsOfTheWorld.getInt(key)
    var enemyCount: Int
        get() = getValue(Factoids.EnemyCount)
        set(value) = setValue(Factoids.EnemyCount, value)

    var bulletCount: Int
        get() = getValue(Factoids.BulletCount)
        set(value) = setValue(Factoids.BulletCount, value)
    var currentLevel: Int
        get() = getValue(Factoids.CurrentLevel)
        set(value) = setValue(Factoids.CurrentLevel, value)
    val currentLength get() = currentLevel * 8
    var numberOfObjectives: Int
        get() = getValue(Factoids.NumberOfObjectives)
        set(value) = setValue(Factoids.NumberOfObjectives, value)
    var maxEnemies: Int
        get() = getValue(Factoids.MaxEnemies)
        set(value) = setValue(Factoids.MaxEnemies, value)
    var maxSpawnedEnemies: Int
        get() = getValue(Factoids.MaxSpawnedEnemies)
        set(value) = setValue(Factoids.MaxSpawnedEnemies, value)

    var startingEnemyCount: Int
    get() = getValue(Factoids.StartingEnemyCount)
    set(value) = setValue(Factoids.StartingEnemyCount, value)
}