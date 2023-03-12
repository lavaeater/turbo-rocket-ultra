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

import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.World
import eater.physics.addComponent

/** A `Box2dRadiusProximity` is a [Proximity] that queries the world for all fixtures that potentially overlap the
 * circle having the specified detection radius and whose center is the owner position.
 *
 * @author davebaol
 */
class Box2dRadiusProximity(
    owner: Steerable<Vector2>,
    world: World,
    detectionRadius: Float,
    acceptFunction: (Steerable<Vector2>) -> Boolean = { _ -> true }
) :
    Box2dSquareAABBProximity(owner, world, detectionRadius, acceptFunction) {
    override fun accept(steerable: Steerable<Vector2>?): Boolean {
        // The bounding radius of the current body is taken into account
        // by adding it to the radius proximity
        return if (steerable == null)
            false
        else {
            return if (acceptFunction(steerable)) {
                val range = detectionRadius + steerable.boundingRadius

                // Make sure the current body is within the range.
                // Notice we're working in distance-squared space to avoid square root.
                val distanceSquare = steerable.position.dst2(_owner.position)
                distanceSquare <= range * range
            } else
                false
        }
    }
}