package ecs.components.player

import box2dLight.ConeLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import common.InjectionContext.Companion.inject

class FlashlightComponent: Component, Pool.Poolable {
    val rayHandler by lazy { inject<RayHandler>() }
    val flashLight = ConeLight(rayHandler, 64, Color(.2f, .2f, .2f, 1f), 30f, 0f, 0f, 30f, 15f).apply {
        height = 2.0f
    }

    override fun reset() {
    }
}

