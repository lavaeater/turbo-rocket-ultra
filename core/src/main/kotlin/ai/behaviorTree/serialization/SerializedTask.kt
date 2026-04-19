package ai.behaviorTree.serialization

/**
 * JSON-friendly representation of a behavior tree node.
 * "t" distinguishes the three structural cases.
 */
sealed class SerializedTask {
    abstract val guard: SerializedTask?

    data class Branch(
        val branchType: String,
        val children: List<SerializedTask>,
        override val guard: SerializedTask? = null
    ) : SerializedTask()

    data class Decor(
        val decorType: String,
        val child: SerializedTask,
        val params: Map<String, String> = emptyMap(),
        override val guard: SerializedTask? = null
    ) : SerializedTask()

    data class Leaf(
        val leafType: String,
        val params: Map<String, String> = emptyMap(),
        override val guard: SerializedTask? = null
    ) : SerializedTask()
}
