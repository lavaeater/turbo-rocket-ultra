package features.pickups

open class LootValue<T>(
    override val lootValue: T,
    override var probability: Float,
    override val isUnique: Boolean = false,
    override val always: Boolean = false,
    override val enabled: Boolean = true,
    override val preResultEvaluation: (List<ILoot>) -> Unit = {},
    override val hit: (List<ILoot>) -> Unit = {},
    override val postResultEvaluation: (List<ILoot>) -> Unit = {}
) : ILootValue<T>