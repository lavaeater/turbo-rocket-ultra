package story.consequence

interface Consequence {
    val consequenceType: ConsequenceType
    fun apply()
}
