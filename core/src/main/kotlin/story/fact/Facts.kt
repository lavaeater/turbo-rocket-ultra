package story.fact

class Facts {
    companion object {
        fun subFact(factKey: String, subKey: String): String {
            return "$factKey.$subKey"
        }

        const val Context = "Context"
        const val MetNumberOfNpcs = "MetNumberOfNpcs"
        const val Score = "Score"
        const val PlayerTileX = "PlayerTileX"
        const val PlayerTileY = "PlayerTileY"


        // New keys for my new, shiny game
        const val BossIsDead = "BossIsDead"
        const val AllObjectivesAreTouched = "AllObjectivesAreTouched"
        const val LevelComplete = "LevelComplete"
        const val EnemyKillCount = "EnemyKillCount"
        const val TargetEnemyKillCount = "TargetEnemyKillCount"
        const val ShowEnemyKillCount = "ShowEnemyKillCount"
        const val AcceleratingSpawns = "AcceleratingSpawns"
        const val AcceleratingSpawnsFactor = "AcceleratingSpawnsFactor"

        //Map Facts
        const val CurrentMapName = "CurrentMapName"
        const val MapStartMessage = "MapStartMessage"
        const val MapSuccessMessage = "MapSuccessMessage"
        const val MapFailMessage = "MapFailMessage"
    }
}