package eater.turbofacts

import eater.injection.InjectionContext.Companion.inject


fun multiKey(vararg key: String): String {
    return key.joinToString(".")
}

fun stateBooleanFact(toSet: Boolean, vararg key: String): Boolean {
    return factsOfTheWorld().setBooleanFact(toSet, *key).value
}

fun addToIntStat(toAdd: Int, vararg key: String): Int {
    return factsOfTheWorld().addToInt(toAdd, *key)
}

fun factsOfTheWorld(): TurboFactsOfTheWorld {
    return inject()
}

class TurboFactsOfTheWorld(private val onFactUpdated: (key: String) -> Unit = {}) {
    val facts = mutableMapOf<String, Factoid>()

    fun updated(key: String) {
        if (!silent) {
            onFactUpdated(key)
        }
    }

    fun intOrDefault(default: Int, vararg key: String): Int {
        val k = multiKey(*key)
        if (!facts.containsKey(k)) {
            setIntFact(default, *key)
        }
        return getInt(*key)
    }

    fun boolOrDefault(default: Boolean, vararg key: String): Boolean {
        val k = multiKey(*key)
        if (!facts.containsKey(k)) {
            setBooleanFact(default, *key)
        }
        return getBoolean(*key)
    }

    fun floatOrDefault(default: Float, vararg key: String): Float {
        val k =  multiKey(*key)
        if(!facts.containsKey(k)) {
            setFloatFact(default, *key)
        }
        return getFloat(*key)
    }

    fun stringOrDefault(default: String, vararg key: String): String {
        val k =  multiKey(*key)
        if(!facts.containsKey(k)) {
            setStringFact(default, *key)
        }
        return getString(*key)
    }

    fun factsFor(vararg key: String): List<Factoid> {
        /*
        advanced queries, my main man

        To begin with, first scenario, we want all keys that contain
        this particular key, start to finish or something. Keys
        are a hierarchy
         */

        val fs = if (key.any { it.contains("*") } && key.count { it.contains("*") } == 1) {
            val mk = multiKey(*key)
            val start = mk.substringBefore("*")
            val end = mk.substringAfter("*")
            facts.filterKeys { it.startsWith(start) && it.endsWith(end) }.values.toList()
        } else {
            val mk = multiKey(*key)
            facts.filterKeys { it.contains(mk) }.values.toList()
        }
        return fs
    }

    fun setBooleanFact(value: Boolean, vararg key: String): Factoid.Fact.BooleanFact {
        val mk = multiKey(*key)
        var fact = Factoid.Fact.BooleanFact(mk, value)
        if (!facts.containsKey(mk)) {
            facts[mk] = fact
            updated(mk)
        } else if (facts[mk]!! is Factoid.Fact.BooleanFact) {
            fact = facts[mk]!! as Factoid.Fact.BooleanFact
            fact.value = value
            updated(mk)
        } else {
            throw Exception("Not a boolean fact")
        }
        return fact
        //Check all this out later
    }

    fun setTrue(vararg key: String) {
        setBooleanFact(true, *key)
    }

    fun setFalse(vararg key: String) {
        setBooleanFact(false, *key)
    }

    fun getBooleanFact(vararg key: String): Factoid.Fact.BooleanFact {
        val mk = multiKey(*key)
        return facts[mk] as Factoid.Fact.BooleanFact? ?: setBooleanFact(false, *key)
    }

    fun getBoolean(vararg key: String): Boolean {
        return getBooleanFact(*key).value
    }

    fun setIntFact(value: Int, vararg key: String): Factoid.Fact.IntFact {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val fact = Factoid.Fact.IntFact(mk, value)
            facts[mk] = fact
            updated(mk)
            return fact
        } else if (facts[mk]!! is Factoid.Fact.IntFact) {
            val fact = facts[mk]!! as Factoid.Fact.IntFact
            fact.value = value
            updated(mk)
            return fact
        }
        throw Exception("Fact with key $mk is not of type Int")
    }

    fun getIntFact(vararg key: String): Factoid.Fact.IntFact {
        val mk = multiKey(*key)
        return if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.IntFact)
                facts[mk]!! as Factoid.Fact.IntFact
            else
                throw Exception("Fact $mk is not a Int")
        else {
            val fact = Factoid.Fact.IntFact(mk, 0)
            facts[mk] = fact
            fact
        }
    }

    fun getInt(vararg key: String): Int {
        return getIntFact(*key).value
    }

    fun addToInt(value: Int, vararg key: String): Int {
        return setIntFact(getInt(*key) + value, *key).value
    }

    fun setFloatFact(value: Float, vararg key: String): Factoid.Fact.FloatFact {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val fact = Factoid.Fact.FloatFact(mk, value)
            facts[mk] = fact
            updated(mk)
            return fact
        } else if (facts[mk]!! is Factoid.Fact.FloatFact) {
            val fact = facts[mk]!! as Factoid.Fact.FloatFact
            fact.value = value
            updated(mk)
            return fact
        }
        throw Exception("Fact with key $mk is not of type Float")
    }

    fun getFloatFact(vararg key: String): Factoid.Fact.FloatFact {
        val mk = multiKey(*key)
        return if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.FloatFact)
                facts[mk]!! as Factoid.Fact.FloatFact
            else
                throw Exception("Fact $mk is not a Float")
        else {
            val fact = Factoid.Fact.FloatFact(mk, 0f)
            facts[mk] = fact
            fact
        }
    }

    fun getFloat(vararg key: String): Float {
        return getFloatFact(*key).value
    }

    fun addToFloat(value: Float, vararg key: String): Float {
        return setFloatFact(getFloat(*key) + value, *key).value
    }

    fun setStringFact(value: String, vararg key: String): Factoid.Fact.StringFact {
        val mk = multiKey(*key)
        var fact = facts[mk]
        when (fact) {
            null -> {
                updated(mk)
                fact = Factoid.Fact.StringFact(mk, value)
                facts[mk] = fact
            }
            is Factoid.Fact.StringFact -> {
                updated(mk)
                fact.value = value
            }
            else -> {
                throw Exception("Fact with key $mk is not of type String")
            }
        }
        return fact
    }

    fun getStringFact(vararg key: String): Factoid.Fact.StringFact {
        val mk = multiKey(*key)
        return if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.StringFact)
                facts[mk]!! as Factoid.Fact.StringFact
            else
                throw Exception("Fact $mk is not a String")
        else {
            val fact = Factoid.Fact.StringFact(mk, "")
            facts[mk] = fact
            fact
        }
    }

    fun getString(vararg key: String): String {
        return getStringFact(*key).value
    }

    fun addToStringList(value: String, vararg key: String) {
        val listFact = ensureStringList(*key)
        listFact.value.add(value)
        updated(multiKey(*key))
    }

    fun removeFromStringList(value: String, vararg key: String) {
        val listFact = ensureStringList(*key)
        listFact.value.remove(value)
        updated(multiKey(*key))
    }

    fun getStringList(vararg key: String): Factoid.Fact.StringListFact {
        return ensureStringList(*key)
    }

    private fun ensureStringList(vararg key: String): Factoid.Fact.StringListFact {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val newFact = Factoid.Fact.StringListFact(mk, mutableListOf())
            facts[mk] = newFact
            return newFact
        } else if (facts[mk]!! is Factoid.Fact.StringListFact) {
            return facts[mk]!! as Factoid.Fact.StringListFact
        }
        throw Exception("Fact $mk exists and is not a stringlist")
    }

    var silent = false

    fun silent(block: TurboFactsOfTheWorld.() -> Unit) {
        silent = true
        block()
        silent = false

    }

    fun setFactsFromMap(facts: Map<String, Any>) {
        for ((key, value) in facts) {
            when (value) {
                is Int -> setIntFact(value, key)
                is Boolean -> setBooleanFact(value, key)
                is Float -> setFloatFact(value, key)
                else -> setStringFact(value.toString(), key)
            }
        }
    }

}