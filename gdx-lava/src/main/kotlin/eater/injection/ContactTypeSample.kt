package eater.injection

import com.badlogic.ashley.core.Entity

sealed class ContactTypeSample {
    class FishAndCity(val fish: Entity, val city: Entity): ContactTypeSample()
}