package features.pickups

class NullValue(
    probability: Float,
    unique: Boolean = false,
    always: Boolean = false,
    enabled: Boolean = true,
    preResultEvaluation: (List<ILoot>) -> Unit = {},
    hit: (List<ILoot>) -> Unit = {},
    postResultEvaluation: (List<ILoot>) -> Unit = {}
) : LootValue<Object?>(
    null,
    probability,
    unique,
    always,
    enabled,
    preResultEvaluation,
    hit,
    postResultEvaluation
)