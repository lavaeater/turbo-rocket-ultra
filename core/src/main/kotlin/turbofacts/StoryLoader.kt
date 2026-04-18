package turbofacts

import com.badlogic.gdx.Gdx
import com.badlogic.gdx.utils.JsonReader

/**
 * Loads TurboStory definitions from a JSON file in assets.
 *
 * Format (assets/stories/example.json):
 * ```json
 * [
 *   {
 *     "name": "My Story",
 *     "repeat": false,
 *     "exclusive": false,
 *     "rules": [
 *       {
 *         "name": "Rule 1",
 *         "criteria": [
 *           { "type": "boolTrue",  "key": "BossIsDead" },
 *           { "type": "boolFalse", "key": "LevelComplete" },
 *           { "type": "intMoreThan", "key": "EnemyKillCount", "value": 5 },
 *           { "type": "intLessThan", "key": "EnemyKillCount", "value": 20 },
 *           { "type": "intEquals",   "key": "EnemyKillCount", "value": 10 },
 *           { "type": "stringEquals",   "key": "Context", "value": "town" },
 *           { "type": "stringContains", "key": "Context", "value": "own" },
 *           { "type": "listContains", "key": "VisitedZones", "value": "market" },
 *           { "type": "listSizeMoreThan", "key": "VisitedZones", "value": 2 },
 *           { "type": "listSizeLessThan", "key": "VisitedZones", "value": 10 },
 *           { "type": "listSizeEquals",   "key": "VisitedZones", "value": 3 }
 *         ]
 *       }
 *     ],
 *     "consequence": {
 *       "type": "setFact",
 *       "key": "LevelComplete",
 *       "value": true
 *     }
 *   }
 * ]
 * ```
 *
 * Supported consequence types: setFact (bool/int/string by value type).
 * For complex consequences (conversations, state machine events) wire them in Kotlin
 * by name via [StoryHelper.allStories].
 */
object StoryLoader {
    fun load(assetPath: String): List<TurboStory> {
        val file = Gdx.files.internal(assetPath)
        if (!file.exists()) return emptyList()

        val root = JsonReader().parse(file)
        val stories = mutableListOf<TurboStory>()

        for (i in 0 until root.size) {
            val storyJson = root[i]
            val builder = TurboStoryBuilder().apply {
                name = storyJson.getString("name", "Story")
                repeat = storyJson.getBoolean("repeat", true)
                exclusive = storyJson.getBoolean("exclusive", false)
            }

            val rulesJson = storyJson["rules"] ?: continue
            for (j in 0 until rulesJson.size) {
                val ruleJson = rulesJson[j]
                builder.rule {
                    name = ruleJson.getString("name", "Rule")
                    val criteriaJson = ruleJson["criteria"] ?: return@rule
                    for (k in 0 until criteriaJson.size) {
                        val c = criteriaJson[k]
                        val type = c.getString("type", "")
                        val key = c.getString("key", "")
                        val strVal = c.getString("value", "")
                        val intVal = c.getInt("value", 0)
                        when (type) {
                            "boolTrue"         -> criteria.add(SingleBoolean.IsTrue(key))
                            "boolFalse"        -> criteria.add(SingleBoolean.IsFalse(key))
                            "intMoreThan"      -> criteria.add(SingleInt.moreThan(key, intVal))
                            "intLessThan"      -> criteria.add(SingleInt.lessThan(key, intVal))
                            "intEquals"        -> criteria.add(SingleInt.equals(key, intVal))
                            "stringEquals"     -> criteria.add(SingleString.equals(key, strVal))
                            "stringContains"   -> criteria.add(SingleString.contains(key, strVal))
                            "listContains"     -> criteria.add(StringListContains(key, strVal))
                            "listSizeMoreThan" -> criteria.add(StringListSize.moreThan(key, intVal))
                            "listSizeLessThan" -> criteria.add(StringListSize.lessThan(key, intVal))
                            "listSizeEquals"   -> criteria.add(StringListSize.equals(key, intVal))
                        }
                    }
                }
            }

            // Consequence is story-level: fires when all rules pass
            val conseqJson = storyJson["consequence"]
            if (conseqJson != null) {
                val cType = conseqJson.getString("type", "")
                val cKey = conseqJson.getString("key", "")
                if (cType == "setFact") {
                    val boolNode = conseqJson["value"]
                    when {
                        boolNode != null && (boolNode.asString() == "true" || boolNode.asString() == "false") -> {
                            val boolVal = boolNode.asBoolean()
                            builder.consequence = { factsOfTheWorld().setBooleanFact(boolVal, cKey) }
                        }
                        else -> {
                            val intv = conseqJson.getInt("value", Int.MIN_VALUE)
                            if (intv != Int.MIN_VALUE)
                                builder.consequence = { factsOfTheWorld().setIntFact(intv, cKey) }
                            else {
                                val strVal = conseqJson.getString("value", "")
                                builder.consequence = { factsOfTheWorld().setStringFact(strVal, cKey) }
                            }
                        }
                    }
                }
            }

            stories.add(builder.build())
        }
        return stories
    }
}
