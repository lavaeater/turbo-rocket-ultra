package story

import ecs.components.gameplay.TransformComponent
import data.Player
import injection.Context.inject
import map.grid.GridMapSection
import physics.getComponent
import story.fact.*
import story.rule.Rule

class FactsOfTheWorld(private val preferences: com.badlogic.gdx.Preferences, clearFacts: Boolean = false) {
    init {
        if (clearFacts)
            clearAllFacts()
    }

    val npcNames = mapOf(
        1 to "Carl Sagan",
        2 to "Stephen Hawking",
        3 to "Carolyn Shoemaker",
        4 to "Sandra Faber"
    )

    fun factForKey(key: String): IFact<*>? {
        if (preferences.contains(key))
            return factForKey(key, preferences.get()[key]!! as String)

        return null
    }

    fun factForKey(key: String, value: String): IFact<*> {
        if (value.isBoolean())
            return BooleanFact(key, value.parseToBoolean())

        if (value.isInt())
            return IntFact(key, value.parseToInt())

        if (value.isFloat())
            return FloatFact(key, value.parseToFloat())

        if (value.isString())
            return StringFact(key, value.parseToString())

        if (value.isList())
            return ListFact(key, value.toMutableSet())

        throw IllegalArgumentException("BLAGH")
    }

    fun factsForKeys(keys: Set<String>): Sequence<IFact<*>> {
        //A key can be "VisitedCities" or "VisitedCities.Europe" or something...

        return preferences.get().filterKeys { keys.contains(it) }.map { factForKey(it.key, it.value!! as String) }
            .asSequence()
    }

    /**
     *     Checks the rule, with supplied Context.
     *     Any keys in Context that exists in world facts are filtered out
     *     so a fact only exists once. Yay
     */

    private fun checkRule(rule: Rule): Boolean {
        val factsToCheck = factsForKeys(rule.keys)
            .toSet()
        return rule.pass(factsToCheck)
    }

    fun rulesThatPass(rules: Set<Rule>): List<Rule> {
        return rules.filter { checkRule(it) }
            .sortedByDescending { it.criteriaCount }
    }

    fun stateBoolFact(key: String, value: Boolean) {
        val fact = BooleanFact(key, value)
        storeBooleanFact(fact)
    }

    fun stateStringFact(key: String, value: String) {
        val fact = StringFact(key, value)
        storeStringFact(fact)
    }

    fun storeStringFact(fact: StringFact) {
        preferences.putString(fact.key, fact.value.serializeToString())
    }

    fun clearStringFact(key: String) {
        preferences.putString(key, "".serializeToString())
    }

    fun stateIntFact(key: String, value: Int) {
        val fact = IntFact(key, value)
        storeIntFact(fact)
    }

    fun stateFloatFact(key: String, value: Float) {
        val fact = FloatFact(key, value)
        storeFloatFact(fact)
    }

    fun addToIntFact(key: String, value: Int) {
        val factValue = getIntValue(key)
        storeIntFact(IntFact(key, factValue + value))
    }

    fun subtractFromIntFact(key: String, value: Int) {
        val factValue = getIntValue(key)
        storeIntFact(IntFact(key, factValue - value))
    }

    fun addToList(key: String, value: String) {
        val fact = ensureListFact(key)
        fact.value.add(value)
        saveListFact(fact)
    }

    private fun saveListFact(fact: ListFact) {
        preferences.putString(fact.key, fact.value.serializeToString())
    }

    fun removeFromList(key: String, value: String) {
        val fact = ensureListFact(key)
        fact.value.remove(value)
        saveListFact(fact)
    }

    fun storeBooleanFact(fact: BooleanFact) {
        preferences.putString(fact.key, fact.value.serializeToString())
    }

    fun getIntValue(key: String): Int {
        return preferences.getString(key).parseToInt()
    }

    fun getFloatValue(key: String): Float {
        return preferences.getString(key).parseToFloat()
    }

    private fun storeIntFact(fact: IntFact) {
        preferences.putString(fact.key, fact.value.serializeToString())
    }

    private fun storeFloatFact(fact: FloatFact) {
        preferences.putString(fact.key, fact.value.serializeToString())
    }

    private fun ensureListFact(key: String): ListFact {
        return if (preferences.contains(key)) ListFact(
            key,
            preferences.getString(key).toMutableSet()
        ) else ListFact(key, mutableSetOf())
    }

    fun contains(key: String): Boolean {
        return preferences.contains(key)
    }

    fun clearAllFacts() {
        preferences.clear()
    }

    fun setupInitialFacts() {
        //Actually we might need to arduosly create all the goddamned facts in the game.
        /*
        This is due to shitty type management in java... will make better, me promise.

        We need to create basic values for all the int facts
        since our shitty code returns just random types...
         */

        if (!preferences.contains(Facts.Score))
            stateIntFact(Facts.Score, 0)

        if (!preferences.contains(Facts.MetNumberOfNpcs))
            stateIntFact(Facts.MetNumberOfNpcs, 0)
    }

    private fun storeFacts(facts: Set<IFact<*>>) {
        for (fact in facts) {
            if (fact is StringFact)
                storeStringFact(fact)

            if (fact is BooleanFact)
                storeBooleanFact(fact)

            if (fact is IntFact)
                storeIntFact(fact)

            if (fact is ListFact)
                saveListFact(fact)
            if(fact is FloatFact)
                storeFloatFact(fact)
        }
    }

    fun getFactList(key: String): ListFact {
        return ensureListFact(key)
    }

    fun getStringFact(key: String): StringFact {
        return if (preferences.contains(key)) StringFact(
            key,
            preferences.getString(key).parseToString()
        ) else StringFact(key, "")
    }

    fun getIntFact(key: String): IntFact {
        return if (preferences.contains(key)) IntFact(key, preferences.getString(key).parseToInt()) else IntFact(key, 0)
    }

    fun getBooleanFact(key: String): BooleanFact {
        return if (preferences.contains(key)) BooleanFact(
            key,
            preferences.getString(key).parseToBoolean()
        ) else BooleanFact(key, false)
    }

    fun getBoolean(key: String): Boolean {
        return if (preferences.contains(key)) preferences.getString(key).parseToBoolean() else false
    }

    fun stringForKey(key: String): String {
        return getStringFact(key).value
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun save() {

        /*
        Should we stealthily save position etc?

        I think we should, actually.

        Just get the player and set the values.

        This code can and will be used to save the current position
        of every actor in the world.

        But how, oh, how, do we keep track of their state? Either by serializing
        stories, trees etc, or by simple, for simple actors, variables in the prefs
        using the key / subkey syntax.
         */

        /*
        We need to add some simple mechanism to keep track of the players "tile coordinate"
        in the game world. We only use tiles for rendering map stuff, but it might come in handy for
        making the map more "gridlike" in the future.
         */
        val player = inject<Player>()

        stateIntFact(
            Facts.PlayerTileX,
            (player.entity.getComponent<TransformComponent>().position.x / GridMapSection.tileWidth).toInt()
        )
        stateIntFact(
            Facts.PlayerTileY,
            (player.entity.getComponent<TransformComponent>().position.y / GridMapSection.tileHeight).toInt()
        )

        preferences.flush()
    }

    /**
     * Do not clear list keys, the entire list will disappear...
     */
    fun clearFacts(facts: Set<String>) {
        for (key in facts) {
            preferences.remove(key)
        }
    }

//	fun getCurrentNpc(): Npc? {
//		val npcId = getStringFact(Facts.CurrentNpc).value
//		return ActorFactory.npcByKeys[npcId]
//	}

    fun storyMatches(story: Story): Boolean {
        val rule = rulesThatPass(story.rules.toSet()).firstOrNull()
        story.matchingRule = rule
        return rule != null
    }
}

enum class FactTypes {
    String,
    Int,
    Boolean,
    List
}
