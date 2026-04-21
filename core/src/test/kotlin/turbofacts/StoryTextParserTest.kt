package turbofacts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class StoryTextParserTest {

    private val storyText = """
        story KillCountWin
        repeat false
        exclusive true
        rule TheRule
        intMoreThan EnemyKillCount 10
        boolFalse LevelComplete
        then
        setFact LevelComplete true b
    """.trimIndent().lines()

    @Test fun `parses story name`() {
        val stories = StoryTextParser.parse(storyText)
        assertEquals(1, stories.size)
        assertEquals("KillCountWin", stories[0].name)
    }

    @Test fun `parses repeat flag`() {
        val story = StoryTextParser.parse(storyText)[0]
        assertEquals(false, story.repeat)
    }

    @Test fun `parses exclusive flag`() {
        val story = StoryTextParser.parse(storyText)[0]
        assertTrue(story.exclusive)
    }

    @Test fun `parses one rule with correct name`() {
        val story = StoryTextParser.parse(storyText)[0]
        assertEquals(1, story.rules.size)
        assertEquals("TheRule", story.rules[0].name)
    }

    @Test fun `parses two criteria in rule`() {
        val rule = StoryTextParser.parse(storyText)[0].rules[0]
        assertEquals(2, rule.criteria.size)
    }

    @Test fun `first criterion is SingleInt moreThan`() {
        val criterion = StoryTextParser.parse(storyText)[0].rules[0].criteria[0]
        assertTrue(criterion is SingleInt.SingleIntOperatorCriteria)
        assertEquals("intMoreThan EnemyKillCount 10", criterion.toTextToken())
    }

    @Test fun `second criterion is SingleBoolean IsFalse`() {
        val criterion = StoryTextParser.parse(storyText)[0].rules[0].criteria[1]
        assertTrue(criterion is SingleBoolean.IsFalse)
        assertEquals("boolFalse LevelComplete", criterion.toTextToken())
    }

    @Test fun `parses multiple stories`() {
        val text = """
            story First
            rule R1
            boolTrue flag

            story Second
            rule R2
            intEquals counter 0
        """.trimIndent().lines()
        val stories = StoryTextParser.parse(text)
        assertEquals(2, stories.size)
        assertEquals("First", stories[0].name)
        assertEquals("Second", stories[1].name)
    }

    @Test fun `empty input produces no stories`() {
        assertTrue(StoryTextParser.parse(emptyList()).isEmpty())
    }
}
