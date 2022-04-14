package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import ecs.systems.player.selectedItemListOf
import factories.blockade
import factories.tower
import ktx.math.vec2
import ktx.scene2d.scene2d
import ktx.scene2d.table
import tru.Assets

sealed class Buildable(val name: String, val sprite: Sprite) {
    open fun buildIt(at: Vector2) {}
    object Blockade : Buildable("Blockade", Assets.buildables.first()) {
        override fun buildIt(at: Vector2) {
            blockade(at.x, at.y)
        }
    }
    object MachineGunTower : Buildable("MachineGun", Assets.newTower) {
        override fun buildIt(at: Vector2) {
            tower(at.x, at.y, towerType = "machinegun")
        }
    }
}

class BuildModeComponent: Component, Pool.Poolable {
    val buildables = selectedItemListOf(Buildable.Blockade, Buildable.MachineGunTower)
    var buildCursorEntity: Entity? = null
    override fun reset() {
        buildables.clear()
        buildCursorEntity = null
    }
}

sealed class ComplexActionResult {
    object Failure: ComplexActionResult()
    object Success: ComplexActionResult()
    object Running: ComplexActionResult()
}

class ComplexActionComponent: Component, Pool.Poolable {
    val worldPosition = vec2()
    var busy = false
    var scene2dTable = scene2d.table {  }
    var doneFunction: () -> ComplexActionResult = { ComplexActionResult.Failure }
    val doneCallBacks = mutableListOf<(ComplexActionResult)->Unit>()
    override fun reset() {
        busy = false
        scene2dTable = scene2d.table {  }
        worldPosition.setZero()
        doneFunction = { ComplexActionResult.Failure }
        doneCallBacks.clear()
    }

}

class ContextActionComponent: Component, Pool.Poolable {
    var sprite: Sprite = Sprite()
    var contextAction = {}
    override fun reset() {
        contextAction = {}
        sprite = Sprite()
    }
}