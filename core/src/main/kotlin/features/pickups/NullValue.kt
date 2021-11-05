package features.pickups

class NullValue(
    probability: Float,
    unique: Boolean = false,
    always: Boolean = false,
    enabled: Boolean = true,
    preResultEvaluation: (ILoot) -> Unit = {},
    hit: (ILoot) -> Unit = {},
    postResultEvaluation: (ILoot) -> Unit = {}
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