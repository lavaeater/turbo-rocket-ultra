package turbofacts

import story.consequence.SimpleConsequence

/**
 * Parses inline story definitions from map file text lines.
 *
 * Format (each block starts with "story <name>"):
 * ```
 * story KillCountWin
 * repeat false
 * exclusive true
 * rule TheRule
 * intMoreThan EnemyKillCount 10
 * boolFalse LevelComplete
 * then
 * setFact LevelComplete true b
 * ```
 *
 * Criterion tokens:  boolTrue|boolFalse <key>
 *                    intMoreThan|intLessThan|intEquals <key> <value>
 *                    stringEquals|stringContains <key> <value>
 *                    listContains <key> <value>
 *                    listSizeMoreThan|listSizeLessThan|listSizeEquals <key> <value>
 *
 * Consequence tokens: setFact <key> <value> <type>   (type: b/i/f/s)
 */
object StoryTextParser {

    private val storySubKeywords = setOf("repeat", "exclusive", "rule", "then")

    fun parse(lines: List<String>): List<TurboStory> {
        val stories = mutableListOf<TurboStory>()
        var builder: TurboStoryBuilder? = null
        var currentRuleBuilder: TurboRuleBuilder? = null
        var inThen = false

        fun flushRule() {
            currentRuleBuilder?.let { rb ->
                builder?.rules?.add(rb.build())
            }
            currentRuleBuilder = null
        }

        fun flushStory() {
            flushRule()
            builder?.let { stories.add(it.build()) }
            builder = null
            inThen = false
        }

        for (rawLine in lines) {
            val line = rawLine.trim()
            if (line.isEmpty()) continue

            val parts = line.split("\\s+".toRegex())
            val token = parts[0].lowercase()

            when {
                token == "story" -> {
                    flushStory()
                    builder = TurboStoryBuilder().apply {
                        name = parts.drop(1).joinToString(" ").ifBlank { "Story" }
                    }
                }
                token == "repeat" && builder != null -> {
                    builder!!.repeat = parts.getOrNull(1)?.toBoolean() ?: true
                }
                token == "exclusive" && builder != null -> {
                    builder!!.exclusive = parts.getOrNull(1)?.toBoolean() ?: false
                }
                token == "rule" && builder != null -> {
                    flushRule()
                    inThen = false
                    currentRuleBuilder = TurboRuleBuilder().apply {
                        name = parts.drop(1).joinToString(" ").ifBlank { "Rule" }
                    }
                }
                token == "then" && builder != null -> {
                    flushRule()
                    inThen = true
                }
                inThen && builder != null -> {
                    parseConsequence(parts)?.let { fn -> builder!!.consequence = fn }
                }
                currentRuleBuilder != null -> {
                    parseCriterion(parts)?.let { currentRuleBuilder!!.criteria.add(it) }
                }
            }
        }

        flushStory()
        return stories
    }

    private fun parseCriterion(parts: List<String>): Criterion? {
        val type = parts[0].lowercase()
        val key = parts.getOrNull(1) ?: return null
        val strVal = parts.getOrNull(2) ?: ""
        val intVal = strVal.toIntOrNull() ?: 0
        return when (type) {
            "booltrue"          -> SingleBoolean.IsTrue(key)
            "boolfalse"         -> SingleBoolean.IsFalse(key)
            "intmorethan"       -> SingleInt.moreThan(key, intVal)
            "intlessthan"       -> SingleInt.lessThan(key, intVal)
            "intequals"         -> SingleInt.equals(key, intVal)
            "stringequals"      -> SingleString.equals(key, strVal)
            "stringcontains"    -> SingleString.contains(key, strVal)
            "listcontains"      -> StringListContains(key, strVal)
            "listsizemorethan"  -> StringListSize.moreThan(key, intVal)
            "listsizelessthan"  -> StringListSize.lessThan(key, intVal)
            "listsizeequals"    -> StringListSize.equals(key, intVal)
            else -> null
        }
    }

    private fun parseConsequence(parts: List<String>): ((TurboStory) -> Unit)? {
        if (parts[0].lowercase() != "setfact") return null
        val key = parts.getOrNull(1) ?: return null
        val value = parts.getOrNull(2) ?: return null
        val typeCode = parts.getOrNull(3)?.lowercase() ?: "s"
        return when (typeCode) {
            "b" -> { _ -> factsOfTheWorld().setBooleanFact(value.toBoolean(), key) }
            "i" -> { _ -> factsOfTheWorld().setIntFact(value.toInt(), key) }
            "f" -> { _ -> factsOfTheWorld().setFloatFact(value.toFloat(), key) }
            else -> { _ -> factsOfTheWorld().setStringFact(value, key) }
        }
    }
}
