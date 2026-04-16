package ecs.components.gameplay

import box2dLight.p3d.P3dLightManager
import box2dLight.p3d.P3dPointLight
import com.badlogic.ashley.core.Component
import com.badlogic.gdx.graphics.Color
import com.badlogic.gdx.utils.Pool
import eater.injection.InjectionContext.Companion.inject

class LightComponent : Component, Pool.Poolable {
    val light = P3dPointLight(inject<P3dLightManager>(), 32, Color.GREEN, 15f, 0f, 0f).apply{
        isActive = false
        setHeight(2.0f)
    }

    override fun reset() {
        light.isActive = false
        light.setHeight(2.0f)
    }
}

