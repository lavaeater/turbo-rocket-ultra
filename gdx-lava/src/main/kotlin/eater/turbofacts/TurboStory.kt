package eater.turbofacts

data class TurboStory(
    val name: String,
    val description: String,
    var repeat: Boolean = true,
    val rules: List<TurboRule>,
    var consequence: (TurboStory) -> Unit = {},
    private val initializer: () -> Unit
) {

    private var needsInit = true
    private var storyIsFinished = false
    fun initialize() {
        if (needsInit) {
            needsInit = false
            if (repeat && storyIsFinished) {
                storyIsFinished = false
            }
            initializer()
        }
    }

    val allCriteria get() = rules.map { it.criteria }.flatten()

    fun checkAndApplyStory() {
        if (storyIsFinished) {
            return
        }
        val checksOut = rules.all { it.checkRule() }
        if (checksOut) {
            storyIsFinished = true
            needsInit = true
            consequence(this)//rules.map { it.criteria.map { c -> c.factKey } }.flatten())
        }
    }
}

fun story(block: TurboStoryBuilder.() -> Unit) = TurboStoryBuilder().apply(block).build()
