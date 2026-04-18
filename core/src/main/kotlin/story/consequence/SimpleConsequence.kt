package story.consequence

class SimpleConsequence(private val applier: () -> Unit) : Consequence {
    override val consequenceType = ConsequenceType.ApplyLambdaConsequence
    override fun apply() = applier()
}
