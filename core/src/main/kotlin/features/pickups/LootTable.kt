package features.pickups

class LootTable(
    override val contents: List<ILoot>,
    override var probability: Float,
    override val count: Int,
    override val unique: Boolean = false,
    override val always: Boolean = false,
    override val enabled: Boolean = true,
    override val preResultEvaluation: (ILoot) -> Unit = {},
    override val hit: (ILoot) -> Unit = {},
    override val postResultEvaluation: (ILoot) -> Unit = {}
) : ILootTable {
    override val result: List<ILoot> get() {
        return emptyList()
    }

}