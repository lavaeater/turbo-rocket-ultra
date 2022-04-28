package ecs.components.ai

import com.badlogic.ashley.core.Component
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.ai.btree.BehaviorTree

class BehaviorComponent(val tree: BehaviorTree<Entity>) : Component