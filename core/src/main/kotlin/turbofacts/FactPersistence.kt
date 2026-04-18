package turbofacts

import com.badlogic.gdx.Gdx

private const val SAVE_PATH = "localfiles/facts_save.json"

object FactPersistence {
    fun saveExists(): Boolean = Gdx.files.local(SAVE_PATH).exists()

    fun save(facts: TurboFactsOfTheWorld) {
        val sb = StringBuilder("{")
        var first = true
        for ((key, factoid) in facts.facts) {
            if (!first) sb.append(',')
            first = false
            when (factoid) {
                is Factoid.Fact.BooleanFact -> sb.append("\"$key\":{\"t\":\"b\",\"v\":${factoid.value}}")
                is Factoid.Fact.IntFact     -> sb.append("\"$key\":{\"t\":\"i\",\"v\":${factoid.value}}")
                is Factoid.Fact.FloatFact   -> sb.append("\"$key\":{\"t\":\"f\",\"v\":${factoid.value}}")
                is Factoid.Fact.StringFact  -> sb.append("\"$key\":{\"t\":\"s\",\"v\":\"${factoid.value.replace("\"", "\\\"")}\"}")
                is Factoid.Fact.StringListFact -> { /* skip list facts — runtime-only */ }
            }
        }
        sb.append('}')
        Gdx.files.local(SAVE_PATH).writeString(sb.toString(), false)
    }

    fun load(facts: TurboFactsOfTheWorld) {
        val file = Gdx.files.local(SAVE_PATH)
        if (!file.exists()) return
        val json = file.readString()
        // Minimal hand-rolled parser for the format we write above
        val entryRe = Regex(""""([^"]+)":\{"t":"([bisf])","v":("[^"]*"|[^}]+)\}""")
        facts.silent {
            for (match in entryRe.findAll(json)) {
                val key = match.groupValues[1]
                val type = match.groupValues[2]
                val raw = match.groupValues[3]
                try {
                    when (type) {
                        "b" -> setBooleanFact(raw == "true", key)
                        "i" -> setIntFact(raw.toInt(), key)
                        "f" -> setFloatFact(raw.toFloat(), key)
                        "s" -> setStringFact(raw.removeSurrounding("\"").replace("\\\"", "\""), key)
                    }
                } catch (_: Exception) {}
            }
        }
    }
}
