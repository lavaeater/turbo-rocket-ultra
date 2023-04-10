package screens

fun boundMoveTo(xFunc: () -> Float, yFunc: () -> Float): BoundMoveToAction {
    return BoundMoveToAction(xFunc, yFunc)
}