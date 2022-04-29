package ai.builders

import com.badlogic.gdx.ai.btree.Task

abstract class DecoratorBuilder<T>(val child: Task<T>) : TaskBuilder<T>() {
}