package screens.ui

object ApplicationFlags {
    val map = mutableMapOf("showEnemyPaths" to false, "showEnemyActionInfo" to false, "showCanSee" to false, "showMemory" to false)
    var showEnemyPaths by map
    var showCanSee by map
    var showEnemyActionInfo by map
    var showMemory by map
}