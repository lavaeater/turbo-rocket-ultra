package ecs.components.player

sealed class PlayerMode {
    object Control: PlayerMode()
    object Building: PlayerMode()
    object Hacking: PlayerMode()
}