package ecs.components.intent

sealed class IntendsTo {
    object DoNothing: IntendsTo()
    object ToggleBuildMode : IntendsTo()
    object Build : IntendsTo()
}