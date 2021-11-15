package story.conversation

import data.IAgent

class EmptyAgent : IAgent {
    override val id = ""
    override var name = ""
    override var strength = 0
    override var health = 0
    override var intelligence = 0
    override var sightRange = 0
    override val inventory: MutableList<String> = mutableListOf()
    override val skills: MutableMap<String, Int> = mutableMapOf()
    override var currentX = 0
    override var currentY = 0
}
