package turbofacts

import story.consequence.Consequence
import story.consequence.EmptyConsequence
import story.consequence.SimpleConsequence

class TurboStoryBuilder : Builder<TurboStory> {
    var name = "Story"
    var description = "Describe your story"
    val rules = mutableListOf<TurboRule>()
    var repeat = true
    var exclusive = false
    var initializer: () -> Unit = {}

    private var _consequence: Consequence = EmptyConsequence()

    /** Lambda syntax — existing stories keep working unchanged. */
    var consequence: (TurboStory) -> Unit = {}
        set(value) {
            field = value
            _consequence = SimpleConsequence { value(DUMMY_STORY) }
        }

    /** Typed consequence — use for ConversationConsequence etc. */
    fun consequence(c: Consequence) { _consequence = c }

    fun rule(block: TurboRuleBuilder.() -> Unit) = rules.add(TurboRuleBuilder().apply(block).build())

    override fun build(): TurboStory =
        TurboStory(name, description, repeat, rules, _consequence, exclusive, initializer)

    companion object {
        // Placeholder passed to legacy consequence lambdas that ignore their argument.
        private val DUMMY_STORY = TurboStory("", "", false, emptyList(), EmptyConsequence(), false) {}
    }
}
