package turbofacts

import story.consequence.SetFactConsequence

object StoryTextSerializer {

    fun serialize(stories: List<TurboStory>): String =
        stories.joinToString("\n\n") { serialize(it) }

    fun serialize(story: TurboStory): String = buildString {
        appendLine("story ${story.name}")
        appendLine("repeat ${story.repeat}")
        appendLine("exclusive ${story.exclusive}")
        for (rule in story.rules) {
            appendLine("rule ${rule.name}")
            for (criterion in rule.criteria) {
                val token = criterion.toTextToken()
                if (token != null) appendLine(token)
            }
        }
        val consequence = story.consequence
        if (consequence is SetFactConsequence) {
            appendLine("then")
            append(consequence.toTextToken())
        }
    }
}
