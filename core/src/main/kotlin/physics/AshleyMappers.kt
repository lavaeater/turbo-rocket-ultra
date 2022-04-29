package physics

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.ComponentMapper
import ecs.components.BodyComponent
import ecs.components.PlayerControlComponent
import ecs.components.VehicleComponent
import ecs.components.VehicleControlComponent
import ecs.components.gameplay.TransformComponent
import ecs.components.graphics.CharacterSpriteComponent
import ktx.ashley.mapperFor
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object AshleyMappers {
    @kotlin.ExperimentalStdlibApi
    inline fun <reified T: Component>getMapper(): ComponentMapper<T> {
        val type = typeOf<T>()
        if(!mappers.containsKey(type))
            mappers[type] = mapperFor<T>()
        return mappers[type] as ComponentMapper<T>
    }
    val mappers = mutableMapOf<KType, ComponentMapper<*>>()

    val transformMapper = mapperFor<TransformComponent>()
    val characterSpriteComponentMapper = mapperFor<CharacterSpriteComponent>()
    val playerControlMapper = mapperFor<PlayerControlComponent>()
    val bodyMapper = mapperFor<BodyComponent>()
    val vehicleMapper = mapperFor<VehicleComponent>()
    val vehicleControlMapper = mapperFor<VehicleControlComponent>()
}