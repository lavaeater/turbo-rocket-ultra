package turbofacts

import story.consequence.SimpleConsequence
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TurboStoryTest : FactsTestBase() {

    private fun passingRule(): TurboRule {
        facts.setBooleanFact(true, "ready")
        return TurboRule("pass", listOf(SingleBoolean.IsTrue("ready")))
    }

    private fun failingRule(): TurboRule {
        facts.setBooleanFact(false, "ready")
        return TurboRule("fail", listOf(SingleBoolean.IsTrue("ready")))
    }

    @Test fun `story fires and returns true when rules pass`() {
        val story = TurboStory("S", "", rules = listOf(passingRule())) {}
        assertTrue(story.checkAndApplyStory())
    }

    @Test fun `story does not fire when rules fail`() {
        val story = TurboStory("S", "", rules = listOf(failingRule())) {}
        assertFalse(story.checkAndApplyStory())
    }

    @Test fun `consequence is applied when story fires`() {
        var applied = false
        val story = TurboStory(
            "S", "",
            rules = listOf(passingRule()),
            consequence = SimpleConsequence { applied = true }
        ) {}
        story.checkAndApplyStory()
        assertTrue(applied)
    }

    @Test fun `story with repeat=false does not fire twice`() {
        var count = 0
        val story = TurboStory(
            "S", "",
            repeat = false,
            rules = listOf(passingRule()),
            consequence = SimpleConsequence { count++ }
        ) {}
        story.checkAndApplyStory()
        story.checkAndApplyStory()
        assertEquals(1, count)
    }

    @Test fun `story with repeat=true fires again after initialize`() {
        var count = 0
        val story = TurboStory(
            "S", "",
            repeat = true,
            rules = listOf(passingRule()),
            consequence = SimpleConsequence { count++ }
        ) {}
        story.checkAndApplyStory()
        story.initialize()
        story.checkAndApplyStory()
        assertEquals(2, count)
    }

    @Test fun `specificityScore equals total criterion count across rules`() {
        val story = TurboStory(
            "S", "",
            rules = listOf(
                TurboRule("r1", listOf(SingleBoolean.IsTrue("a"), SingleBoolean.IsTrue("b"))),
                TurboRule("r2", listOf(SingleBoolean.IsTrue("c")))
            )
        ) {}
        assertEquals(3, story.specificityScore)
    }
}
