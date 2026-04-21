package turbofacts

import org.junit.jupiter.api.BeforeEach
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TurboFactsOfTheWorldTest {

    private lateinit var facts: TurboFactsOfTheWorld

    @BeforeEach
    fun setup() {
        facts = TurboFactsOfTheWorld()
    }

    @Test
    fun `boolean fact defaults to false when not set`() {
        assertFalse(facts.getBoolean("missing"))
    }

    @Test
    fun `set and get boolean fact`() {
        facts.setBooleanFact(true, "alive")
        assertTrue(facts.getBoolean("alive"))
    }

    @Test
    fun `set and get int fact`() {
        facts.setIntFact(42, "kills")
        assertEquals(42, facts.getInt("kills"))
    }

    @Test
    fun `int fact defaults to zero when not set`() {
        assertEquals(0, facts.getInt("missing"))
    }

    @Test
    fun `addToInt accumulates correctly`() {
        facts.addToInt(5, "score")
        facts.addToInt(3, "score")
        assertEquals(8, facts.getInt("score"))
    }

    @Test
    fun `set and get float fact`() {
        facts.setFloatFact(1.5f, "speed")
        assertEquals(1.5f, facts.getFloat("speed"))
    }

    @Test
    fun `float fact defaults to zero when not set`() {
        assertEquals(0f, facts.getFloat("missing"))
    }

    @Test
    fun `set and get string fact`() {
        facts.setStringFact("hero", "name")
        assertEquals("hero", facts.getString("name"))
    }

    @Test
    fun `string fact defaults to empty when not set`() {
        assertEquals("", facts.getString("missing"))
    }

    @Test
    fun `multikey joins with dot`() {
        facts.setIntFact(7, "player", "level")
        assertEquals(7, facts.getInt("player", "level"))
    }

    @Test
    fun `intOrDefault returns existing value`() {
        facts.setIntFact(10, "hp")
        assertEquals(10, facts.intOrDefault(99, "hp"))
    }

    @Test
    fun `intOrDefault sets and returns default when missing`() {
        assertEquals(5, facts.intOrDefault(5, "new"))
        assertEquals(5, facts.getInt("new"))
    }

    @Test
    fun `boolOrDefault returns default when missing`() {
        assertTrue(facts.boolOrDefault(true, "flag"))
    }

    @Test
    fun `stringOrDefault returns default when missing`() {
        assertEquals("anon", facts.stringOrDefault("anon", "player", "name"))
    }

    @Test
    fun `string list add and retrieve`() {
        facts.addToStringList("sword", "inventory")
        facts.addToStringList("shield", "inventory")
        val list = facts.getStringList("inventory").value
        assertTrue(list.contains("sword"))
        assertTrue(list.contains("shield"))
    }

    @Test
    fun `string list remove`() {
        facts.addToStringList("sword", "inventory")
        facts.removeFromStringList("sword", "inventory")
        assertFalse(facts.getStringList("inventory").value.contains("sword"))
    }

    @Test
    fun `setFactsFromMap sets int bool and string`() {
        facts.setFactsFromMap(mapOf("kills" to 3, "alive" to true, "name" to "rex"))
        assertEquals(3, facts.getInt("kills"))
        assertTrue(facts.getBoolean("alive"))
        assertEquals("rex", facts.getString("name"))
    }

    @Test
    fun `onFactUpdated callback fires on set`() {
        val updated = mutableListOf<String>()
        val f = TurboFactsOfTheWorld { updated.add(it) }
        f.setIntFact(1, "score")
        assertTrue(updated.contains("score"))
    }

    @Test
    fun `silent block suppresses callbacks`() {
        val updated = mutableListOf<String>()
        val f = TurboFactsOfTheWorld { updated.add(it) }
        f.silent { setIntFact(99, "score") }
        assertTrue(updated.isEmpty())
    }

    @Test
    fun `factsFor with wildcard matches multiple keys`() {
        facts.setIntFact(1, "enemy", "a", "hp")
        facts.setIntFact(2, "enemy", "b", "hp")
        val results = facts.factsFor("enemy", "*", "hp")
        assertEquals(2, results.size)
    }
}
