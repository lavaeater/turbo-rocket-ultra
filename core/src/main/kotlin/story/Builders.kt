package story

import story.consequence.Consequence
import story.consequence.ConversationConsequence
import story.consequence.EmptyConsequence
import story.consequence.SimpleConsequence
import story.conversation.*
import story.fact.IFact
import story.rule.Criterion
import story.rule.Rule

class ConsequenceBuilder: Builder<Consequence> {
	var apply: () -> Unit = {}

  override fun build(): SimpleConsequence {
    return SimpleConsequence(apply)
  }
}

class ConversationConsequenceBuilder : Builder<ConversationConsequence> {

	var afterConversation: (story: com.bladecoder.ink.runtime.Story) -> Unit = {}
	var beforeConversation: (story: com.bladecoder.ink.runtime.Story)-> Unit = {}

	private lateinit var story: com.bladecoder.ink.runtime.Story

	fun inkStory(storyPath: String, block: com.bladecoder.ink.runtime.Story.() -> Unit) {
		story = com.bladecoder.ink.runtime.Story(InkLoader().readStoryJson(storyPath)).apply(block)
	}

	override fun build(): ConversationConsequence {
		return ConversationConsequence(story,afterConversation, beforeConversation)
	}
}

interface Builder<out T> {
	fun build(): T
}

class StoryBuilder: Builder<Story> {
	var name = ""
	var initializer : () -> Unit = {}
	private val rules = mutableListOf<Rule>()
	private var consequence: Consequence = EmptyConsequence()

	fun rule(block: RuleBuilder.() -> Unit) {
		rules.add(RuleBuilder().apply(block).build())
	}

	fun consequence(block: ConsequenceBuilder.() -> Unit) {
		consequence = ConsequenceBuilder().apply(block).build()
	}

	override fun build() : Story = Story(name, rules, consequence, initializer)
}

class CriteriaBuilder:Builder<Criterion> {
	var key = ""
	private var matcher: (IFact<*>) -> Boolean = { false }
	/*
	val key: String, private val matcher: (IFact<*>) -> Boolean
	 */

	override fun build(): Criterion {
		return Criterion(key, matcher)
	}

}

class RuleBuilder:Builder<Rule> {
	var name = ""
	private val criteria = mutableSetOf<Criterion>()
	var consequence: Consequence? = null

	fun criterion(block: CriteriaBuilder.() -> Unit) {
		criteria.add(CriteriaBuilder().apply(block).build())
	}

	fun booleanCriteria(key: String, checkFor:Boolean) {
		criteria.add(Criterion.booleanCriterion(key, checkFor))
	}

	fun <T> equalsCriterion(key: String, value: T) {
		criteria.add(Criterion.equalsCriterion(key, value))
	}
	fun rangeCriterion(key: String, range: IntRange) {
		criteria.add(Criterion.rangeCriterion(key, range))
	}

	fun containsCriterion(key: String, value: String) {
		criteria.add(Criterion.containsCriterion(key, value))
	}
	fun listContainsFact(key:String, contextKey:String) {
		criteria.add(Criterion.listContainsFact(key, contextKey))
	}

	fun listDoesNotContainFact(key:String, contextKey:String) {
		criteria.add(Criterion.listDoesNotContainFact(key, contextKey))
	}

	fun context(context: String) {
		criteria.add(Criterion.context(context))
	}

	fun notContainsCriterion(key: String, value: String) {
		criteria.add(Criterion.notContainsCriterion(key, value))
	}

	fun consequence(block: ConsequenceBuilder.() -> Unit) {
		consequence = ConsequenceBuilder().apply(block).build()
	}

	fun conversation(block: ConversationConsequenceBuilder.() -> Unit) {
		consequence = ConversationConsequenceBuilder().apply(block).build()
	}

	override fun build(): Rule {
		if(consequence == null)
			throw IllegalStateException("You must define a consequence for a rule")
		return Rule(name, criteria, consequence!!)
	}

}

fun story(block: StoryBuilder.() -> Unit) = StoryBuilder().apply(block).build()

fun convo(block: InternalConversationBuilder.() -> Unit) = InternalConversationBuilder().apply(block).build()

class InternalConversationBuilder : Builder<InternalConversation> {
	var startingStepKey = ""
	val steps = mutableMapOf<String, ConversationStep>()
	fun step(block: ConversationStepBuilder.() -> Unit) {
		val c = ConversationStepBuilder().apply(block).build()
		steps[c.key] = c
	}

	override fun build(): InternalConversation {
		if(!steps.containsKey("abort"))
			steps["abort"] = ConversationStep("abort", listOf("Tack för pratet"), emptyList())

		return InternalConversation(startingStepKey, steps)
	}
}

class ConversationStepBuilder() : Builder<ConversationStep> {
	var key = ""
	private val antagonistLines = mutableListOf<String>()
	private val conversationRoutes = mutableListOf<ConversationRoute>()

	fun addLine(line:String) {
		antagonistLines.add(line)
	}

	fun positive(key: String, text: String ="Ja") {
		if(!conversationRoutes.any { it.routeType == RouteType.positive })
			conversationRoutes.add(ConversationRoute(key, text, RouteType.positive))
	}

	fun negative(key:String, text: String = "Nej") {
		if(!conversationRoutes.any {it.routeType == RouteType.negative})
			conversationRoutes.add(ConversationRoute(key, text, RouteType.negative))
	}
	fun rude(key:String, text: String = "Far åt helvete!") {
		if(!conversationRoutes.any {it.routeType == RouteType.rude})
			conversationRoutes.add(ConversationRoute(key, text, RouteType.rude))
	}
	fun abort(key:String, text: String = "Avsluta") {
		if(!conversationRoutes.any {it.routeType == RouteType.abort})
			conversationRoutes.add(ConversationRoute(key, text, RouteType.abort))
	}

	override fun build(): ConversationStep = ConversationStep(key, antagonistLines, conversationRoutes)
}

