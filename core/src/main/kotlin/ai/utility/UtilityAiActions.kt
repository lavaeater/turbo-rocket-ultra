package ai.utility

import com.badlogic.ashley.core.Entity
import eater.ai.CanISeeThisConsideration
import ecs.components.player.PlayerComponent

object UtilityAiActions {
    private val followPlayerAction = /**
     * Remove the components we add below
     */
        /**
         * Remove the components we add below
         */
        /**
         * Remove the components we add below
         */
        ConsideredAction(
            "MoveTowards",
            { entity: Entity ->
                /**
                 * Remove the components we add below
                 */
            },
            { entity: Entity, deltaTime: Float ->
                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */
                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */
                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */

                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */
                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */
                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */
                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */
                /**
                 * To make this "super-efficient" there could be
                 * a system that does nothing but checks if entities of some
                 * type can see other entities of some other type and puts that info in a
                 * list in a component,
                 * so that we won't have to do it here. On the other hand
                 *
                 * - this function will simply try to find an entity with the
                 * specified component and then after that is done will simply follow that
                 * particular entity, ignoring other entities that show up - unless they take
                 * damage or something else interrupts them.
                 *
                 * So the "unique" code here is the code looking for a particular entity
                 * and setting that in a component on the entity, but the moving towards
                 * a particular position (vector) is the same as for the action that ambles
                 * and we should probably make that into a function on the companion
                 * object or something.
                 */


                //1. Check if we have a player to follow
                //2. If not, find one
                //3. Follow the player
                //4. If player is dead, stop all of this nonsense!

            }, CanISeeThisConsideration(PlayerComponent::class),
            Consideration.MyHealthConsideration
        )

    val defaultActions = setOf(
        AmbleAiAction(),
        followPlayerAction
    )
}