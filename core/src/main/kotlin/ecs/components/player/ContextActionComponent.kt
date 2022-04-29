package ecs.components.player

import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.g2d.Sprite
import com.badlogic.gdx.graphics.g2d.TextureRegion
import com.badlogic.gdx.utils.Pool
import tru.Assets

class ContextActionComponent: Component, Pool.Poolable {
    var sprite: Sprite = Sprite()
    var contextAction = {}
    override fun reset() {
        contextAction = {}
        sprite = Sprite()
    }
}