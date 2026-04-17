package ai.utility

class InvertedConsideration(
    name: String,
    consideration: Consideration
) : Consideration(
    name,
    { entity ->
            1f / consideration.scoreFunction(entity)
    })