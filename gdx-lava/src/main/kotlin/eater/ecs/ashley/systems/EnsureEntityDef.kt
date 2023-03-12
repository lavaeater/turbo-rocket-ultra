package eater.ecs.ashley.systems

import com.badlogic.ashley.core.Family

class EnsureEntityDef(val entityFamily: Family, val numberOfEntities: Int, val interval:Float = 1f, val random: Boolean, val creator: ()->Unit) {
    var coolDown = interval
}