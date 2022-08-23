package screens.stuff

import ktx.math.ImmutableVector2
import ktx.math.vec3
import screens.DirtyClass
import java.awt.Color
import kotlin.properties.Delegates.observable

open class Node3d : DirtyClass() {
    var color by observable(Color.WHITE, ::setDirty)
    var parent: Node3d? by observable(null, ::setDirty)
    var position by observable(vec3(), ::setDirty)
    var offset by observable(vec3(), ::setDirty)

}