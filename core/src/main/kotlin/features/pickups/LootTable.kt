package features.pickups

class LootTable(
    override val contents: List<ILoot>,
    override var probability: Float,
    override val count: Int,
    override val isUnique: Boolean = false,
    override val always: Boolean = false,
    override val enabled: Boolean = true,
    override val preResultEvaluation: (ILoot) -> Unit = {},
    override val hit: (ILoot) -> Unit = {},
    override val postResultEvaluation: (ILoot) -> Unit = {}
) : ILootTable {
    private val uniqueDrops = mutableListOf<ILoot>()

    fun addToResult(resultList: MutableList<ILoot>, loot: ILoot) {
        if(!loot.isUnique || !uniqueDrops.contains(loot)) {
            if(loot.isUnique)
                uniqueDrops.add(loot)

            if(loot !is NullValue) {
                if(loot is LootTable) {
                    resultList.addAll(loot.result)
                } else {

                }
            }
        }
    }
    override val result: List<ILoot> get() {
        return emptyList()
    }

}