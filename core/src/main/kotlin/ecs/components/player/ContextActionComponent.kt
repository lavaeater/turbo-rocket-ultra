package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import tru.Assets

class ContextActionComponent: Component, Pool.Poolable {
    var texture: TextureRegion = Assets.dummyRegion
    var contextAction = {}
    override fun reset() {
        contextAction = {}
        texture = Assets.dummyRegion
    }
}