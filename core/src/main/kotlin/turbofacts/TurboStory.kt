package turbofacts

import story.consequence.Consequence
import story.consequence.EmptyConsequence

data class TurboStory(
    val name: String,
    val description: String,
    var repeat: Boolean = true,
    val rules: List<TurboRule>,
    val consequence: Consequence = EmptyConsequence(),
    val exclusive: Boolean = false,
    private val initializer: () -> Unit
) {
    private var needsInit = true
    private var storyIsFinished = false

    /** Number of criteria across all rules — used as specificity score for firing priority. */
    val specificityScore: Int get() = rules.sumOf { it.criteria.size }

    val allCriteria get() = rules.flatMap { it.criteria }

    fun initialize() {
        if (needsInit) {
            needsInit = false
            if (repeat && storyIsFinished) storyIsFinished = false
            initializer()
        }
    }

    /** Returns true if this story fired (rules passed and consequence applied). */
    fun checkAndApplyStory(): Boolean {
        if (storyIsFinished) return false
        if (!rules.all { it.checkRule() }) return false
        storyIsFinished = true
        needsInit = true
        consequence.apply()
        return true
    }
}

fun story(block: TurboStoryBuilder.() -> Unit) = TurboStoryBuilder().apply(block).build()
