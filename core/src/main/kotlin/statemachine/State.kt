package statemachine

/**
 *
 */
class State<S,E>(val state: S) {
    private val edges = hashMapOf<E, Edge<S, E>>()   // Convert to HashMap with event as key
    private val stateActions = mutableListOf<(State<S, E>) -> Unit>()

    /**
     * Creates an edge from a [State] to another when a [BaseEvent] occurs
     * @param event: Transition event
     * @param targetState: Next state
     * @param init: I find it as weird as you do, here you go https://kotlinlang.org/docs/reference/lambdas.html
     */
    fun edge(event: E, targetState: S, init: Edge<S, E>.() -> Unit) {
        val edge = Edge(event, targetState)
        edge.init()

        if (edges.containsKey(event)) {
            throw Error("Adding multiple edges for the same event is invalid")
        }

        edges.put(event, edge)
    }

    /**
     * Action performed by state
     */
    fun action(action: (State<S, E>) -> Unit) {
        stateActions.add(action)
    }

    /**
     * Enter the state and run all actions
     */
    fun enter() {
        // Every action takes the current state
        stateActions.forEach { it(this) }
    }

    /**
     * Get the appropriate statemachine.Edge for the Event
     */
    fun getEdgeForEvent(event: E): Edge<S, E> {
        try {
            return edges[event]!!
        } catch (e: KotlinNullPointerException) {
            throw IllegalStateException("Event $event isn't registered with state ${this.state}")
        }
    }

    override fun toString(): String {
        return state.toString()
    }

}
