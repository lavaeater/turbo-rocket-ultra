package turbofacts

import story.consequence.SetFactConsequence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class StoryTextSerializerTest {

    private fun makeStory(
        name: String = "TestStory",
        repeat: Boolean = true,
        exclusive: Boolean = false,
        criteria: List<Criterion> = emptyList(),
        consequence: story.consequence.Consequence = story.consequence.EmptyConsequence()
    ) = TurboStory(
        name = name,
        description = "",
        repeat = repeat,
        exclusive = exclusive,
        rules = if (criteria.isEmpty()) emptyList()
                else listOf(TurboRule("Rule1", criteria)),
        consequence = consequence
    ) {}

    @Test fun `serialized output contains story name`() {
        val out = StoryTextSerializer.serialize(makeStory("MyStory"))
        assertTrue(out.contains("story MyStory"))
    }

    @Test fun `serialized output contains repeat flag`() {
        val out = StoryTextSerializer.serialize(makeStory(repeat = false))
        assertTrue(out.contains("repeat false"))
    }

    @Test fun `serialized output contains exclusive flag`() {
        val out = StoryTextSerializer.serialize(makeStory(exclusive = true))
        assertTrue(out.contains("exclusive true"))
    }

    @Test fun `serialized output contains criterion token`() {
        val out = StoryTextSerializer.serialize(
            makeStory(criteria = listOf(SingleBoolean.IsTrue("alive")))
        )
        assertTrue(out.contains("boolTrue alive"))
    }

    @Test fun `serialized output contains then block for SetFactConsequence`() {
        val out = StoryTextSerializer.serialize(
            makeStory(consequence = SetFactConsequence("LevelComplete", "true", "b"))
        )
        assertTrue(out.contains("then"))
        assertTrue(out.contains("setFact LevelComplete true b"))
    }

    @Test fun `round-trip parse then serialize preserves name and rule count`() {
        val original = """
            story RoundTrip
            repeat false
            exclusive false
            rule Check
            intMoreThan score 5
            boolFalse done
            then
            setFact done true b
        """.trimIndent().lines()

        val parsed = StoryTextParser.parse(original)
        val reserialized = StoryTextSerializer.serialize(parsed)
        val reparsed = StoryTextParser.parse(reserialized.lines())

        assertEquals(1, reparsed.size)
        assertEquals("RoundTrip", reparsed[0].name)
        assertEquals(false, reparsed[0].repeat)
        assertEquals(1, reparsed[0].rules.size)
        assertEquals(2, reparsed[0].rules[0].criteria.size)
    }

    @Test fun `serialize multiple stories joins with blank line`() {
        val stories = listOf(makeStory("A"), makeStory("B"))
        val out = StoryTextSerializer.serialize(stories)
        assertTrue(out.contains("story A"))
        assertTrue(out.contains("story B"))
    }
}
