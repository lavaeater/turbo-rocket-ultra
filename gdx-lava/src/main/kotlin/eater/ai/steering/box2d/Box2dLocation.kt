/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package eater.ai.steering.box2d

import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2
import ktx.math.vec2

class Box2dLocation(
    private var _position: Vector2 = vec2(), private var _orientation: Float = 0f
) : Location<Vector2> {


    override fun getPosition(): Vector2 {
        return _position
    }

    override fun getOrientation(): Float {
        return _orientation
    }

    override fun setOrientation(orientation: Float) {
        _orientation = orientation
    }

    override fun newLocation(): Location<Vector2> {
        return Box2dLocation()
    }

    override fun vectorToAngle(vector: Vector2): Float {
        return Box2dSteeringUtils.vectorToAngle(vector)
    }

    override fun angleToVector(outVector: Vector2, angle: Float): Vector2 {
        return Box2dSteeringUtils.angleToVector(outVector, angle)
    }
}