package twodee.ecs.ashley.components

sealed class BodyPart {
    object Legs: BodyPart()
    object Head: BodyPart()
    object Body: BodyPart()
}
