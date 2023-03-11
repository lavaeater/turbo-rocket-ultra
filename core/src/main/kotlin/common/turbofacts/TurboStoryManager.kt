package common.turbofacts

class TurboStoryManager {
    private var isActive = false

    val stories = mutableListOf<TurboStory>()
    fun addStories(vararg stories: TurboStory) {
        for (story in stories) {
            addStory(story)
        }
    }

    fun activate() {
        isActive = true
        for (story in stories)
            story.initialize()
    }

    fun addStory(story: TurboStory) {
        stories.add(story)
    }

    var needsChecking = true
    fun checkIfNeeded() {
        if (isActive && needsChecking) {
            needsChecking = false
            for (story in stories) {
                story.checkAndApplyStory()
            }
        }
    }
}

interface Builder<out T> {
    fun build(): T
}