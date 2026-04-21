package turbofacts

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TurboRuleTest : FactsTestBase() {

    @Test fun `rule with no criteria passes`() {
        val rule = TurboRule("empty", emptyList())
        assertTrue(rule.checkRule())
    }

    @Test fun `rule passes when all criteria pass`() {
        facts.setBooleanFact(true, "alive")
        facts.setIntFact(10, "kills")
        val rule = TurboRule("win", listOf(
            SingleBoolean.IsTrue("alive"),
            SingleInt.moreThan("kills", 5)
        ))
        assertTrue(rule.checkRule())
    }

    @Test fun `rule fails when any criterion fails`() {
        facts.setBooleanFact(true, "alive")
        facts.setIntFact(3, "kills")
        val rule = TurboRule("win", listOf(
            SingleBoolean.IsTrue("alive"),
            SingleInt.moreThan("kills", 5)
        ))
        assertFalse(rule.checkRule())
    }

    @Test fun `builder produces rule with correct name and criteria`() {
        val rule = TurboRuleBuilder().apply {
            name = "MyRule"
            criteria.add(SingleBoolean.IsTrue("alive"))
            criteria.add(SingleInt.moreThan("kills", 5))
        }.build()

        kotlin.test.assertEquals("MyRule", rule.name)
        kotlin.test.assertEquals(2, rule.criteria.size)
    }
}
