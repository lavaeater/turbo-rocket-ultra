package control

class ShipControl {

    fun fire(yes: Boolean) {
        firing = yes
    }

    fun throttle(amount: Float) {
        thrust = amount
    }

    fun turn(amount: Float) {
        rotation = amount
    }

    var firing: Boolean = false
        private set
    var rotation: Float = 0f
        private set
    var thrust: Float = 0f
        private set
}

