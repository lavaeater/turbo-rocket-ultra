package control

class ShipControl {
    fun startFiring() {
        _firing = true
    }

    fun stopFiring() {
        _firing = false
    }

    private var _firing = false
    var firing: Boolean
        get() { return _firing}
        private set(value) {_firing = value}

    var turn = 0f
    var throttle = 0f
    val rotation: Float get() = turn
    val thrust: Float get()  = throttle
}

