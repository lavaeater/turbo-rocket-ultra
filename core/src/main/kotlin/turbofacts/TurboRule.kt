package turbofacts

class TurboRule {
    val criteria = mutableListOf<Criterion>()
    fun checkRule(): Boolean {
        return criteria.all { it.checkRule() }
    }

    var consequence: (criteria: List<Criterion>) -> Unit = {}
}