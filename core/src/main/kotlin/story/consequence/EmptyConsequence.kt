package story.consequence

class EmptyConsequence : Consequence {
    override val consequenceType = ConsequenceType.Empty
    override fun apply() {}
}
