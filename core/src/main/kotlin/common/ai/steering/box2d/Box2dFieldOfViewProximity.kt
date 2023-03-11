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
package common.ai.steering.box2d

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World

/** A `Box2dFieldOfViewProximity` is a [Proximity] that queries the world for all fixtures that potentially overlap the
 * arc area of the circle having the specified detection radius and whose center is the owner position.
 *
 * @author davebaol
 */
class Box2dFieldOfViewProximity(owner: Steerable<Vector2>, world: World, detectionRadius: Float, angle: Float) :
    Box2dSquareAABBProximity(owner, world, detectionRadius) {
    var angle = angle
        get() = field
        set(value) {
            field = value
            coneThreshold = Math.cos((angle * 0.5f).toDouble()).toFloat()
        }
    var coneThreshold = 0f


    override fun prepareAABB(aabb: AABB) {
        super.prepareAABB(aabb)

        // Transform owner orientation to a Vector2
        _owner.angleToVector(ownerOrientation, _owner.orientation)
    }

    override fun accept(steerable: Steerable<Vector2>?): Boolean {
        return if(steerable == null)
            false
        else if(acceptFunction(steerable)) {
            toSteerable.set(steerable.position).sub(_owner.position)

            // The bounding radius of the current body is taken into account
            // by adding it to the radius proximity
            val range = detectionRadius + steerable.boundingRadius
            val toSteerableLen2 = toSteerable.len2()

            // Make sure the steerable is within the range.
            // Notice we're working in distance-squared space to avoid square root.
            return if (toSteerableLen2 < range * range) {
                // Accept the steerable if it is within the field of view of the owner.
                ownerOrientation.dot(toSteerable) > coneThreshold
            } else false
        } else {
            false
        }
    }

    companion object {
        private val toSteerable = Vector2()
        private val ownerOrientation = Vector2()
    }
}