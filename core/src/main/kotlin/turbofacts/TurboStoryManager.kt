package turbofacts

class TurboStoryManager {
    private var isActive = false

    val stories = mutableListOf<TurboStory>()

    fun addStories(vararg stories: TurboStory) = stories.forEach { addStory(it) }

    fun addStory(story: TurboStory) {
        stories.add(story)
        stories.sortByDescending { it.specificityScore }
    }

    fun activate() {
        isActive = true
        stories.forEach { it.initialize() }
    }

    var needsChecking = true

    fun checkIfNeeded() {
        if (!isActive || !needsChecking) return
        needsChecking = false
        for (story in stories) {
            val fired = story.checkAndApplyStory()
            if (fired && story.exclusive) break
        }
    }
}

interface Builder<out T> {
    fun build(): T
}
