package ecs.components.intent

sealed class IntendsTo {
    object SelectNextWeapon : IntendsTo()
    object SelectPreviousWeapon : IntendsTo()
    object Reload: IntendsTo()
    object DoNothing : IntendsTo()
    object ToggleBuildMode : IntendsTo()
    object Build : IntendsTo()
}