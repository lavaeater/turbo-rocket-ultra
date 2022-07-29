package features.pickups

interface ILoot {
    var probability: Float
    val isUnique: Boolean
    val always: Boolean
    val enabled: Boolean
    val preResultEvaluation: (List<ILoot>) -> Unit
    val hit: (List<ILoot>) -> Unit
    val postResultEvaluation: (List<ILoot>) -> Unit
}