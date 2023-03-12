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
package eater.ecs.ashley.components

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.steer.Steerable
import com.badlogic.gdx.ai.steer.SteerableAdapter
import com.badlogic.gdx.ai.steer.SteeringAcceleration
import com.badlogic.gdx.ai.steer.SteeringBehavior
import com.badlogic.gdx.ai.utils.Location
import com.badlogic.gdx.math.Vector2
import com.badlogic.gdx.physics.box2d.Body
import com.badlogic.gdx.utils.Pool
import eater.ai.steering.box2d.Box2dLocation
import eater.ai.steering.box2d.Box2dSteeringUtils
import ktx.ashley.mapperFor
import ktx.math.vec2

open class StupidSteerable : SteerableAdapter<Vector2>(), Component, Pool.Poolable {
    var actualPosition = vec2()
    override fun getPosition(): Vector2 {
        return actualPosition
    }

    override fun reset() {
        actualPosition = vec2()
    }

    companion object {
        val mapper = mapperFor<StupidSteerable>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): StupidSteerable {
            return mapper.get(entity)
        }
    }
}

/** A steering entity for box2d physics engine.
 *
 * @author davebaol
 */
open class Box2dSteerable(
) : Steerable<Vector2>, Component, Pool.Poolable {
    var isIndependentFacing = false
    private var _boundingRadius = 5f
    private var _tagged = false
    private var _maxLinearSpeed = 0f
    private var _maxLinearAcceleration = 0f
    private var _maxAngularSpeed = 0f
    private var _maxAngularAcceleration = 0f
    var steeringBehavior: SteeringBehavior<Vector2>? = null

    val steeringOutput = SteeringAcceleration(Vector2())
    private var _body: Body? = null

    var body: Body
        get() = _body!!
        set(value) {
            _body = value
        }

    override fun getPosition(): Vector2 {
        return body.position
    }

    override fun getOrientation(): Float {
        return body.angle
    }

    override fun setOrientation(orientation: Float) {
        body.setTransform(position, orientation)
    }

    override fun getLinearVelocity(): Vector2 {
        return body.linearVelocity
    }

    override fun getAngularVelocity(): Float {
        return body.angularVelocity
    }

    fun setBoundingRadius(radius: Float) {
        _boundingRadius = radius
    }

    override fun getBoundingRadius(): Float {
        return _boundingRadius
    }

    override fun isTagged(): Boolean {
        return _tagged
    }

    override fun setTagged(tagged: Boolean) {
        this._tagged = tagged
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

    fun update(deltaTime: Float) {
        if (steeringBehavior != null) {
            // Calculate steering acceleration
            steeringBehavior!!.calculateSteering(steeringOutput)

            /*
			 * Here you might want to add a motor control layer filtering steering accelerations.
			 *
			 * For instance, a car in a driving game has physical constraints on its movement: it cannot turn while stationary; the
			 * faster it moves, the slower it can turn (without going into a skid); it can brake much more quickly than it can
			 * accelerate; and it only moves in the direction it is facing (ignoring power slides).
			 */

            // Apply steering acceleration
            applySteering(deltaTime)
        }
    }

    private fun applySteering(deltaTime: Float) {
        var anyAccelerations = false

        // Update position and linear velocity.
        if (!steeringOutput.linear.isZero) {
            // this method internally scales the force by deltaTime
            body.applyForceToCenter(steeringOutput.linear, true)
            anyAccelerations = true
        }

        // Update orientation and angular velocity
        if (isIndependentFacing) {
            if (steeringOutput.angular != 0f) {
                // this method internally scales the torque by deltaTime
                body.applyTorque(steeringOutput.angular, true)
                anyAccelerations = true
            }
        } else {
            // If we haven't got any velocity, then we can do nothing.
            val linVel = linearVelocity
            if (!linVel.isZero(zeroLinearSpeedThreshold)) {
                val newOrientation = vectorToAngle(linVel)
                body.angularVelocity =
                    (newOrientation - angularVelocity) * deltaTime // this is superfluous if independentFacing is always true
                body.setTransform(body.position, newOrientation)
            }
        }
        if (anyAccelerations) {
            // body.activate();

            // TODO:
            // Looks like truncating speeds here after applying forces doesn't work as expected.
            // We should likely cap speeds form inside an InternalTickCallback, see
            // http://www.bulletphysics.org/mediawiki-1.5.8/index.php/Simulation_Tick_Callbacks

            // Cap the linear speed
            val velocity = body.linearVelocity
            val currentSpeedSquare = velocity.len2()
            val maxLinearSpeed = getMaxLinearSpeed()
            if (currentSpeedSquare > maxLinearSpeed * maxLinearSpeed) {
                body.linearVelocity = velocity.scl(maxLinearSpeed / Math.sqrt(currentSpeedSquare.toDouble()).toFloat())
            }

            // Cap the angular speed
            val maxAngVelocity = getMaxAngularSpeed()
            if (body.angularVelocity > maxAngVelocity) {
                body.angularVelocity = maxAngVelocity
            }
        }
    }


    //
    // Limiter implementation
    //
    override fun getMaxLinearSpeed(): Float {
        return _maxLinearSpeed
    }

    override fun setMaxLinearSpeed(maxLinearSpeed: Float) {
        this._maxLinearSpeed = maxLinearSpeed
    }

    override fun getMaxLinearAcceleration(): Float {
        return _maxLinearAcceleration
    }

    override fun setMaxLinearAcceleration(maxLinearAcceleration: Float) {
        this._maxLinearAcceleration = maxLinearAcceleration
    }

    override fun getMaxAngularSpeed(): Float {
        return _maxAngularSpeed
    }

    override fun setMaxAngularSpeed(maxAngularSpeed: Float) {
        this._maxAngularSpeed = maxAngularSpeed
    }

    override fun getMaxAngularAcceleration(): Float {
        return _maxAngularAcceleration
    }

    override fun setMaxAngularAcceleration(maxAngularAcceleration: Float) {
        this._maxAngularAcceleration = maxAngularAcceleration
    }

    override fun getZeroLinearSpeedThreshold(): Float {
        return 0.001f
    }

    override fun setZeroLinearSpeedThreshold(value: Float) {
        throw UnsupportedOperationException()
    }

    companion object {

        val mapper = mapperFor<Box2dSteerable>()
        fun has(entity: Entity): Boolean {
            return mapper.has(entity)
        }

        fun get(entity: Entity): Box2dSteerable {
            return mapper.get(entity)
        }
    }

    override fun reset() {
        steeringBehavior = null
        _body = null
    }
}
