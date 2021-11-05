package features.pickups

class Loot(
    override var probability: Float,
    override val unique: Boolean,
    override val always: Boolean,
    override val enabled: Boolean,
    override val preResultEvaluation: (ILoot) -> Unit,
    override val hit: (ILoot) -> Unit,
    override val postResultEvaluation: (ILoot) -> Unit
) : ILoot