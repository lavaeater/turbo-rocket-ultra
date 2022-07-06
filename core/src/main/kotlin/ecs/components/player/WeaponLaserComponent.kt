package ecs.components.player

import box2dLight.ConeLight
import box2dLight.RayHandler
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import eater.injection.InjectionContext.Companion.inject
import factories.Box2dCategories
import injection.Context

class WeaponLaserComponent: Component, Pool.Poolable {
    val rayHandler by lazy { inject<RayHandler>() }
    // To make them not collide with lights, simply make sure they are not lights.
    val weaponlaser = ConeLight(rayHandler, 12, Color(1f, 0f, 0f, .2f), 60f, 0f, 0f, 30f, 15f).apply {
        setContactFilter(
            Box2dCategories.indicators,
            0x00,
            Box2dCategories.none
        )
        isStaticLight = true
    }
    override fun reset() {
        //TODO: DO SOMETHING!
    }
}