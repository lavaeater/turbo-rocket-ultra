package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.utils.Pool
import factories.blockade
import factories.tower
import sun.jvm.hotspot.opto.Block
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

class BuildComponent: Component, Pool.Poolable {
    val buildables = selectedItemListOf(Buildable.Blockade, Buildable.MachineGunTower)
    override fun reset() {
        buildables.clear()
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