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

import com.badlogic.gdx.ai.utils.Collision
import com.badlogic.gdx.ai.utils.Ray
import com.badlogic.gdx.ai.utils.RaycastCollisionDetector
import com.badlogic.gdx.math.MathUtils
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.RayCastCallback
import com.badlogic.gdx.physics.box2d.World

/** A raycast collision detector for box2d.
 *
 * @author davebaol
 */
class Box2dRaycastCollisionDetector @JvmOverloads constructor(
    val world: World,
    var callback: Box2dRaycastCallback = Box2dRaycastCallback()
) : RaycastCollisionDetector<Vector2> {
    override fun collides(ray: Ray<Vector2>): Boolean {
        return findCollision(null, ray)
    }

    override fun findCollision(outputCollision: Collision<Vector2>?, inputRay: Ray<Vector2>): Boolean {
        callback.collided = false
        if (!inputRay.start.epsilonEquals(inputRay.end, MathUtils.FLOAT_ROUNDING_ERROR)) {
            callback.outputCollision = outputCollision
            world.rayCast(callback, inputRay.start, inputRay.end)
        }
        return callback.collided
    }

    class Box2dRaycastCallback : RayCastCallback {
        var outputCollision: Collision<Vector2>? = null
        var collided = false
        override fun reportRayFixture(fixture: Fixture, point: Vector2, normal: Vector2, fraction: Float): Float {
            if (outputCollision != null) outputCollision!![point] = normal
            collided = true
            return fraction
        }
    }
}