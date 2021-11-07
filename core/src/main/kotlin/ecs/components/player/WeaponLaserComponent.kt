package ecs.components.player

import box2dLight.ConeLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import injection.Context

class WeaponLaserComponent: Component, Pool.Poolable {
    val rayHandler by lazy { Context.inject<RayHandler>() }
    val weaponlaser = ConeLight(rayHandler, 12, Color(1f, 0f, 0f, .8f), 60f, 0f, 0f, 30f, 15f)
    override fun reset() {
        //TODO: DO SOMETHING!
    }
}