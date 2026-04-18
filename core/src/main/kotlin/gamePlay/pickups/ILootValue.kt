package gamePlay.pickups

interface ILootValue<T> : ILoot {
    val lootValue: T
}