package ai.arena

import ai.behaviorTree.serialization.BehaviorTreeSerializer
import com.badlogic.ashley.core.Entity
import com.badlogic.gdx.Gdx
import com.badlogic.gdx.ai.btree.BehaviorTree
import com.badlogic.gdx.utils.JsonReader
import com.badlogic.gdx.utils.JsonWriter

data class Candidate(
    val tree: BehaviorTree<Entity>,
    val score: Float = 0f
)

data class Population(
    val generation: Int,
    val candidates: List<Candidate>,
    val bestScore: Float = candidates.maxOfOrNull { it.score } ?: 0f
) {
    val best: Candidate? get() = candidates.maxByOrNull { it.score }

    fun saveToFile(dir: String = "arena") {
        val path = "$dir/generation_${generation.toString().padStart(3, '0')}.json"
        val sb = StringBuilder("[")
        candidates.forEachIndexed { i, c ->
            if (i > 0) sb.append(',')
            sb.append("{\"score\":${c.score},\"tree\":${BehaviorTreeSerializer.serialize(c.tree)}}")
        }
        sb.append(']')
        Gdx.files.local(path).writeString(sb.toString(), false)
        best?.let {
            Gdx.files.local("$dir/best.json").writeString(
                "{\"generation\":$generation,\"score\":${it.score},\"tree\":${BehaviorTreeSerializer.serialize(it.tree)}}",
                false
            )
        }
    }

    companion object {
        fun loadFromFile(path: String): Population {
            val text = Gdx.files.local(path).readString()
            val root = JsonReader().parse(text)
            val genFromPath = path.substringAfterLast("generation_").substringBefore(".json").toIntOrNull() ?: 0
            val candidates = mutableListOf<Candidate>()
            for (i in 0 until root.size) {
                val node = root[i]
                val score = node.getFloat("score", 0f)
                val treeJson = node["tree"]?.toJson(JsonWriter.OutputType.json) ?: continue
                runCatching { Candidate(BehaviorTreeSerializer.deserialize(treeJson), score) }
                    .onSuccess { candidates.add(it) }
            }
            return Population(genFromPath, candidates)
        }
    }
}
