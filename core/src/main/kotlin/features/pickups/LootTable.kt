package features.pickups

class LootTable(
    override val contents: MutableList<ILoot>,
    override var count: Int,
    override var probability: Float = 100f,
    override val isUnique: Boolean = false,
    override val always: Boolean = false,
    override val enabled: Boolean = true,
    override val preResultEvaluation: (List<ILoot>) -> Unit = {},
    override val hit: (List<ILoot>) -> Unit = {},
    override val postResultEvaluation: (List<ILoot>) -> Unit = {}
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
                    if(loot is IObjectCreator) {
                        val lootToAdd = (loot as IObjectCreator).createLoot()
                        resultList.add(lootToAdd)
                        hit(emptyList())
                    } else {
                        resultList.add(loot)
                        hit(listOf(loot))
                    }
                }
            } else {
                hit(emptyList())
            }
        }
    }
    override val result: List<ILoot> get() {
        val resultList = mutableListOf<ILoot>()
        val uniqueDrops = mutableListOf<ILoot>()
        for(loot in contents) {
            loot.preResultEvaluation(emptyList())
        }
        for(loot in contents.filter { it.always && it.enabled })
            addToResult(resultList, loot)

        val alwaysCount = contents.filter { it.always && it.enabled }.count()
        val actualDropCount = count - alwaysCount
        if(actualDropCount > 0) {
            for(dropCount in 0 until actualDropCount) {
                val dropables = contents.filter { it.enabled && !it.always }
                val hitValue = Randomizer.getRandomFloat(dropables.map { it.probability }.sum())
                var runUp = 0f
                for(loot in dropables) {
                    runUp += loot.probability
                    if(hitValue < runUp) {
                        addToResult(resultList, loot)
                        break
                    }
                }

            }
        }
        for(loot in resultList) {
            loot.postResultEvaluation(resultList)
        }
        return resultList
    }
}