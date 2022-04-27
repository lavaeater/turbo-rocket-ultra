package turbofacts

import ecs.systems.enemy.multiKey

class NewFactsOfTheWorld(private val onFactUpdated: (key: String) -> Unit = {}) {
    val facts = mutableMapOf<String, Factoid>()

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

    fun setBooleanFact(value: Boolean, vararg key: String) {
        val mk = multiKey(*key)
        val fact = Factoid.Fact.BooleanFact(mk, value)
        if (!facts.containsKey(mk)) {
            facts[mk] = fact
        } else if (facts[mk]!! is Factoid.Fact.BooleanFact) {
            val fact = facts[mk]!! as Factoid.Fact.BooleanFact
            fact.value = value
        }
        //Check all this out later
        onFactUpdated(mk)
    }

    fun getBooleanFact(vararg key: String): Factoid.Fact.BooleanFact {
        val mk = multiKey(*key)
        if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.BooleanFact)
                return facts[mk]!! as Factoid.Fact.BooleanFact
            else
                throw Exception("Fact $mk is not a Boolean")
        else
            throw Exception("Fact $mk is not set in Facts")
    }

    fun setIntFact(value: Int, vararg key: String) {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val fact = Factoid.Fact.IntFact(mk, value)
            facts[mk] = fact
        } else if (facts[mk]!! is Factoid.Fact.IntFact) {
            (facts[mk]!! as Factoid.Fact.IntFact).value = value
        }
        onFactUpdated(mk)
        throw Exception("Fact with key $mk is not of type Int")
    }

    fun getIntFact(vararg key: String): Factoid.Fact.IntFact {
        val mk = multiKey(*key)
        if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.IntFact)
                return facts[mk]!! as Factoid.Fact.IntFact
            else
                throw Exception("Fact $mk is not a Int")
        else
            throw Exception("Fact $mk is not set in Facts")
    }

    fun setStringFact(value: String, vararg key: String) {
        val mk = multiKey(*key)
        if (!facts.containsKey(mk)) {
            val fact = Factoid.Fact.StringFact(mk, value)
            facts[mk] = fact
        } else if (facts[mk]!! is Factoid.Fact.StringFact) {
            (facts[mk]!! as Factoid.Fact.StringFact).value = value
        }
        onFactUpdated(mk)
        throw Exception("Fact with key $mk is not of type String")
    }

    fun getStringFact(vararg key: String): Factoid.Fact.StringFact {
        val mk = multiKey(*key)
        if (facts.containsKey(mk))
            if (facts[mk] is Factoid.Fact.StringFact)
                return facts[mk]!! as Factoid.Fact.StringFact
            else
                throw Exception("Fact $mk is not a String")
        else
            throw Exception("Fact $mk is not set in Facts")
    }

    fun addToStringList(value: String, vararg key: String) {
        val listFact = ensureStringList(*key)
        listFact.value.add(value)
        onFactUpdated(multiKey(*key))
    }

    fun removeFromStringList(value: String, vararg key: String) {
        val listFact = ensureStringList(*key)
        listFact.value.remove(value)
        onFactUpdated(multiKey(*key))
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

}