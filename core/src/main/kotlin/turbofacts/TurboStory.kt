package turbofacts

fun story(block: TurboStoryBuilder.() -> Unit) = TurboStoryBuilder().apply(block).build()

class TurboStoryBuilder: Builder<TurboStory> {
    var name = "Story"
    var description = "Describe your story"
    val rules = mutableListOf<TurboRule>()
    var consequence: (List<Criterion>) -> Unit = {}
    var initializer: ()->Unit = {}

    fun rule(block: TurboRuleBuilder.() -> Unit) = rules.add(TurboRuleBuilder().apply(block).build())

    override fun build(): TurboStory = TurboStory(name, description, rules, consequence, initializer)
}
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