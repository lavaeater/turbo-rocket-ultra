package turbofacts

class TurboStory(
    val name: String,
    val description: String,
    val rules: List<TurboRule>,
    val consequence: (List<Criterion>) -> Unit = {},
    private val initializer: () -> Unit
) {
    private var needsInit = true
    fun initialize() {
        if(needsInit) {
            needsInit = false
            initializer()
        }
    }

    fun checkAndApplyStory() : Boolean {
        val checksOut = rules.all { it.checkRule() }
        if(checksOut) {
            consequence(rules.map { it.criteria }.flatten())
        }
        return checksOut
    }
}

fun story(block: TurboStoryBuilder.() -> Unit) = TurboStoryBuilder().apply(block).build()
