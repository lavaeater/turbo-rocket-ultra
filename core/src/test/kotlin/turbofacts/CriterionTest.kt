package turbofacts

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CriterionTest : FactsTestBase() {

    // SingleBoolean
    @Test fun `IsTrue returns true when fact is true`() {
        facts.setBooleanFact(true, "flag")
        assertTrue(SingleBoolean.IsTrue("flag").checkRule())
    }

    @Test fun `IsTrue returns false when fact is false`() {
        facts.setBooleanFact(false, "flag")
        assertFalse(SingleBoolean.IsTrue("flag").checkRule())
    }

    @Test fun `IsFalse returns true when fact is false`() {
        facts.setBooleanFact(false, "flag")
        assertTrue(SingleBoolean.IsFalse("flag").checkRule())
    }

    @Test fun `IsFalse returns false when fact is true`() {
        facts.setBooleanFact(true, "flag")
        assertFalse(SingleBoolean.IsFalse("flag").checkRule())
    }

    // SingleInt
    @Test fun `SingleInt equals passes when equal`() {
        facts.setIntFact(5, "count")
        assertTrue(SingleInt.equals("count", 5).checkRule())
    }

    @Test fun `SingleInt equals fails when not equal`() {
        facts.setIntFact(3, "count")
        assertFalse(SingleInt.equals("count", 5).checkRule())
    }

    @Test fun `SingleInt moreThan passes when greater`() {
        facts.setIntFact(10, "kills")
        assertTrue(SingleInt.moreThan("kills", 5).checkRule())
    }

    @Test fun `SingleInt moreThan fails when equal`() {
        facts.setIntFact(5, "kills")
        assertFalse(SingleInt.moreThan("kills", 5).checkRule())
    }

    @Test fun `SingleInt lessThan passes when less`() {
        facts.setIntFact(3, "hp")
        assertTrue(SingleInt.lessThan("hp", 10).checkRule())
    }

    // SingleString
    @Test fun `SingleString equals passes when matching`() {
        facts.setStringFact("hero", "name")
        assertTrue(SingleString.equals("name", "hero").checkRule())
    }

    @Test fun `SingleString contains passes when substring present`() {
        facts.setStringFact("heroic deeds", "title")
        assertTrue(SingleString.contains("title", "heroic").checkRule())
    }

    @Test fun `SingleString equals fails when not matching`() {
        facts.setStringFact("villain", "name")
        assertFalse(SingleString.equals("name", "hero").checkRule())
    }

    // toTextToken
    @Test fun `IsTrue toTextToken returns correct string`() {
        assertEquals("boolTrue flag", SingleBoolean.IsTrue("flag").toTextToken())
    }

    @Test fun `SingleInt equals toTextToken returns correct string`() {
        assertEquals("intEquals count 5", SingleInt.equals("count", 5).toTextToken())
    }

    @Test fun `SingleInt moreThan toTextToken returns correct string`() {
        assertEquals("intMoreThan kills 5", SingleInt.moreThan("kills", 5).toTextToken())
    }
}
