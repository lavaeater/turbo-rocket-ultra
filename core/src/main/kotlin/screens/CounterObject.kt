package screens

object CounterObject {
    var enemyCount = 0
    var bulletCount = 0
    var currentLevel = 1
    val currentLength get() = currentLevel * 8
    var numberOfObjectives = 1
    var maxEnemies = 1
    var maxSpawnedEnemies = 2
}