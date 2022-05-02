package turbofacts

import kotlinx.serialization.SerialName

@kotlinx.serialization.Serializable
@SerialName("story")
class TurboStory(
    val name: String,
    val description: String,
    var repeat: Boolean = true,
    val rules: List<TurboRule>,
    val consequence: (List<Criterion>) -> Unit = {},
    private val initializer: () -> Unit
) {
    private var needsInit = true
    private var storyIsFinished = false
    fun initialize() {
        if(needsInit) {
            needsInit = false
            if(repeat && storyIsFinished) {
                storyIsFinished = false
            }
            initializer()
        }
    }

    fun checkAndApplyStory() {
        if(storyIsFinished) {
            return
        }
        val checksOut = rules.all { it.checkRule() }
        if(checksOut) {
            storyIsFinished = true
            needsInit = true
            consequence(rules.map { it.criteria }.flatten())
        }
    }
}

fun story(block: TurboStoryBuilder.() -> Unit) = TurboStoryBuilder().apply(block).build()
