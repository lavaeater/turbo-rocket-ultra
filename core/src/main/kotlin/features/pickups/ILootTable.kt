package features.pickups

interface ILootTable : ILoot {
    val count: Int
    val contents: List<ILoot>
    val result: List<ILoot>
}