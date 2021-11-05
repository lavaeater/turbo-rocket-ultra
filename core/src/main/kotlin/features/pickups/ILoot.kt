package features.pickups

interface ILoot {
    var probability: Float
    val unique: Boolean
    val always: Boolean
    val enabled: Boolean
    val preResultEvaluation: (ILoot) -> Unit
    val hit: (ILoot) -> Unit
    val postResultEvaluation: (ILoot) -> Unit

}