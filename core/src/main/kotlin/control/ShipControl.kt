package control

class ShipControl {
    var turn = 0f
    var throttle = 0f
    val rotation: Float get() = turn
    val thrust: Float get()  = throttle
}

