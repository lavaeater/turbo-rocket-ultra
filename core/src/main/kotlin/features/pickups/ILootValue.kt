package features.pickups

interface ILootValue<T> : ILoot {
    val lootValue: T
}