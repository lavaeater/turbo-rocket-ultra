package story.rule

import injection.Ctx
import story.FactsOfTheWorld
import story.fact.Facts
import story.fact.IFact

class Criterion(val key: String, private val matcher: (IFact<*>) -> Boolean) {
  fun isMatch(fact: IFact<*>):Boolean {
    return matcher(fact)
  }

  companion object {
    private val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }

    fun booleanCriterion(key: String, checkFor: Boolean) : Criterion {
      return Criterion(key, { it.value == checkFor })
    }

    fun <T> equalsCriterion(key: String, value: T): Criterion {
      return Criterion(key, {
        it.value == value
      })
    }

    fun <T> notEqualsCriterion(key: String, value: T): Criterion {
      return Criterion(key, {
        it.value != value
      })
    }

    fun lessThanCriterion(key: String, value: Int) : Criterion {
      return Criterion(key, {
        (it.value as Int) < value
      })
    }

    fun moreThanCriterion(key: String, value: Int) : Criterion {
      return Criterion(key, {
        (it.value as Int) > value
      })
    }

    fun rangeCriterion(key: String, range: IntRange): Criterion {
      return Criterion(key, {
        it.value in range
      })
    }

    fun containsCriterion(key: String, value: String) : Criterion {
      return Criterion(key, {
        val factList = factsOfTheWorld.getFactList(key)
        factList.contains(value)
      })
    }

    fun listContainsFact(key:String, contextKey:String): Criterion {
      return Criterion(key, {
        val contextValue = factsOfTheWorld.stringForKey(contextKey)
        factsOfTheWorld.getFactList(key).contains(contextValue)
      })
    }

    fun listDoesNotContainFact(key:String, contextKey:String): Criterion {
      return Criterion(key, {
        val contextValue = factsOfTheWorld.stringForKey(contextKey)
        !factsOfTheWorld.getFactList(key).contains(contextValue)
      })
    }

    fun context(context: String) : Criterion {
      return Criterion(Facts.Context, { fact ->
        fact.value == context
      })
    }

    fun notContainsCriterion(key: String, value: String): Criterion {
      return Criterion(key, {
        !factsOfTheWorld.getFactList(key).contains(value)
      })
    }
  }
}