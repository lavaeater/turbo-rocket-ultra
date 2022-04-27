package turbofacts

class TurboStoryManager {
    val stories = mutableListOf<TurboStory>()
    var needsChecking = true
    fun checkIfNeeded() {
        if (needsChecking) {
            needsChecking = false
            for(story in stories) {
                story.checkAndApplyStory()
            }
        }
    }
}

interface Builder<out T> {
    fun build(): T
}