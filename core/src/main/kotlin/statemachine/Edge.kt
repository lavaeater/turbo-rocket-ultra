package statemachine

/**
 * A transition between states when an [BaseEvent] occurs that goes
 * to a next [State]
 */
class Edge<S,E>(private val event: E, private val targetState: S) {
    private val actionList = mutableListOf<(Edge<S, E>) -> Unit>()

    /**
     * Add an action to be performed upon transition
     */
    fun action(action: (Edge<S, E>) -> Unit) {
        actionList.add(action)
    }

    /**
     * Apply the transition actions
     */
    fun applyTransition(getNextState: (S) -> State<S, E>): State<S, E> {
        actionList.forEach { it(this) }

        return getNextState(targetState)
    }

    override fun toString(): String {
        return "Edge to ${targetState} on ${event}"
    }
}
