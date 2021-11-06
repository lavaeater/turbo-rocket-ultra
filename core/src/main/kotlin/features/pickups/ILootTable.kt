package features.pickups

interface ILootTable : ILoot {
    var count: Int
    val contents: MutableList<ILoot>
    val result: List<ILoot>
}