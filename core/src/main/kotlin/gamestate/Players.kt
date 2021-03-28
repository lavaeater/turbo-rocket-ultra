package gamestate

import gamestate.Player
import input.ControlMapper

object Players {
    val players = mutableMapOf<ControlMapper, Player>()
}