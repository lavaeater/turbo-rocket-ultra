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

import com.badlogic.gdx.InputAdapter
import com.badlogic.gdx.math.Vector2
import eater.ecs.ashley.components.Box2dSteerable

/** An [InputProcessor] that allows you to manually move a [SteeringActor].
 *
 * @autor davebaol
 */
open class Box2dTargetInputProcessor(private val target: Box2dSteerable) : InputAdapter() {
    override fun touchDown(screenX: Int, screenY: Int, pointer: Int, button: Int): Boolean {
        setTargetPosition()
        return true
    }

    override fun touchDragged(screenX: Int, screenY: Int, pointer: Int): Boolean {
        setTargetPosition()
        return true
    }

    private fun setTargetPosition() {
        val pos: Vector2 = target.position
//        y = Gdx.graphics.getHeight() - y
//        pos.x = Box2dSteeringTest.pixelsToMeters(screenX)
//        pos.y = Box2dSteeringTest.pixelsToMeters(screenY)
        target.body.setTransform(pos, target.body.angle)
    }
}
