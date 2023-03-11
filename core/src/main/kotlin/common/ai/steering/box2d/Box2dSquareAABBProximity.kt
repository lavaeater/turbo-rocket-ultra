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
import com.badlogic.gdx.ai.steer.Proximity
import com.badlogic.gdx.ai.steer.Proximity.ProximityCallback
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Fixture
import com.badlogic.gdx.physics.box2d.QueryCallback
import com.badlogic.gdx.physics.box2d.World
import eater.ecs.ashley.components.Box2dSteerable
import eater.physics.addComponent

/** A `Box2dSquareAABBProximity` is a [Proximity] that queries the world for all fixtures that potentially overlap the
 * square AABB built around the circle having the specified detection radius and whose center is the owner position.
 *
 * @author davebaol
 */
open class Box2dSquareAABBProximity(
    protected var _owner: Steerable<Vector2>,
    /** Sets the box2d world.  */
    val world: World,
    /** Sets the detection radius that is half the side of the square AABB.  */
    var detectionRadius: Float,
    val acceptFunction: (Steerable<Vector2>)-> Boolean = {_ -> true}
) : Proximity<Vector2>, QueryCallback {
    /** Returns the box2d world.  */
    protected var behaviorCallback: ProximityCallback<Vector2>? = null

    /** Returns the detection radius that is half the side of the square AABB.  */
    private var neighborCount = 0
    override fun getOwner(): Steerable<Vector2> {
        return _owner
    }

    override fun setOwner(owner: Steerable<Vector2>) {
        this._owner = owner
    }

    override fun findNeighbors(behaviorCallback: ProximityCallback<Vector2>): Int {
        this.behaviorCallback = behaviorCallback
        neighborCount = 0
        prepareAABB(aabb)
        world.QueryAABB(this, aabb.lowerX, aabb.lowerY, aabb.upperX, aabb.upperY)
        this.behaviorCallback = null
        return neighborCount
    }

    protected open fun prepareAABB(aabb: AABB) {
        val position = _owner.position
        aabb.lowerX = position.x - detectionRadius
        aabb.lowerY = position.y - detectionRadius
        aabb.upperX = position.x + detectionRadius
        aabb.upperY = position.y + detectionRadius
    }

    protected open fun getSteerable(fixture: Fixture): Steerable<Vector2>? {
        return if (fixture.body.userData == null)
            null
        else {
            val entity = fixture.body.userData as Entity
            if (Box2dSteerable.has(entity)) Box2dSteerable.get(entity) else
                entity.addComponent<Box2dSteerable> {
                    isIndependentFacing = false
                    body = fixture.body
                    maxLinearSpeed = 0f
                    maxLinearAcceleration = 0f
                    maxAngularAcceleration = 0f
                    maxAngularSpeed = 0f
                    boundingRadius = 0f
                }
        }
    }

    protected open fun accept(steerable: Steerable<Vector2>?): Boolean {
        return (steerable != null) && acceptFunction(steerable)
    }

    override fun reportFixture(fixture: Fixture): Boolean {
        val steerable = getSteerable(fixture)
        if (steerable !== _owner && accept(steerable)) {
            if (behaviorCallback!!.reportNeighbor(steerable)) neighborCount++
        }
        return true
    }

    class AABB {
        var lowerX = 0f
        var lowerY = 0f
        var upperX = 0f
        var upperY = 0f
    }

    companion object {
        private val aabb = AABB()
    }
}
