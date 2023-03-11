package common.ai.ashley

class InvertedConsideration(
    name: String,
    consideration: Consideration
) : Consideration(
    name,
    { entity ->
            1f / consideration.scoreFunction(entity)
    })