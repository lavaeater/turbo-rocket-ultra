package screens.concepts

import com.badlogic.gdx.graphics.Color

sealed class SectionDefinition(val sectionColor: Color, val key: String) {
    object Boss : SectionDefinition(Color.RED, "b")
    object Loot : SectionDefinition(Color.WHITE, "l")
    object Start : SectionDefinition(Color.BLUE, "s")
    object Goal : SectionDefinition(Color.GREEN, "g")
    object Spawner : SectionDefinition(Color.CYAN, "w")
    object PerimeterGoal : SectionDefinition(Color.ORANGE, "p")
    object HackingStation : SectionDefinition(Color.PURPLE, "h")
    object Corridor : SectionDefinition(Color.GRAY, "c")
}