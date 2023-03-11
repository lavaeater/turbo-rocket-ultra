package common.turbofacts

class TurboStoryBuilder: Builder<TurboStory> {
    var name = "Story"
    var description = "Describe your story"
    val rules = mutableListOf<TurboRule>()
    var consequence: (TurboStory) -> Unit = {}
    var initializer: ()->Unit = {}
    var repeat = true

    fun rule(block: TurboRuleBuilder.() -> Unit) = rules.add(TurboRuleBuilder().apply(block).build())

    override fun build(): TurboStory = TurboStory(name, description, repeat, rules, consequence, initializer)
}