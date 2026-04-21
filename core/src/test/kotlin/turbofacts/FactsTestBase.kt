package turbofacts

import dependencies.InjectionContext
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach

abstract class FactsTestBase {
    protected lateinit var facts: TurboFactsOfTheWorld

    @BeforeEach
    fun bindFacts() {
        facts = TurboFactsOfTheWorld()
        try { InjectionContext.context.remove<TurboFactsOfTheWorld>() } catch (_: Exception) {}
        InjectionContext.context.bind { facts }
    }

    @AfterEach
    fun unbindFacts() {
        try { InjectionContext.context.remove<TurboFactsOfTheWorld>() } catch (_: Exception) {}
    }
}
