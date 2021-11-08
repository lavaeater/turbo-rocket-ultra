package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.utils.Pool
import ecs.components.graphics.OffsetTextureRegion
import tru.Assets

class ContextActionComponent: Component, Pool.Poolable {
    var texture: OffsetTextureRegion = Assets.dummyRegion
    var contextAction = {}
    override fun reset() {
        contextAction = {}
        texture = Assets.dummyRegion
    }
}