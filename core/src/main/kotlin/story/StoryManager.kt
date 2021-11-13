package story

import injection.Context.inject

class StoryManager {
	private val stories  = mutableListOf<Story>()
	private val finishedStories = mutableListOf<Story>()
	private val factsOfTheWorld by lazy { inject<FactsOfTheWorld>() }
	/*
	or do we create a global, main story... yes we do, yes we do...
	 */

	fun checkStories() {
		val matchingStories = stories.filter {
			it.active &&
					factsOfTheWorld.storyMatches(it) }
				.sortedByDescending { it.matchingRule?.criteriaCount } //just grab the first active story - null  check later

		/*
		Consequences MUST be self-contained, I realize this now
		they need to lazy-load all dependencies and just do their THANG

		Thing is, the code below will not wait for the consequences of stories before it continues...
		how do we implement this?

		A call back? Some kind of async mechanism?

		I think some kind of simple callback mechanism to do one story at a time,
		somehow. So to begin with we do "more complicated story first" if there are more than one!
		 */
		var story = matchingStories.firstOrNull()
		if(story != null) {

			val rule = factsOfTheWorld.rulesThatPass(story.rules.toSet()).firstOrNull()
			if (rule != null) {
				rule.consequence.apply()
				story.finishedRules.add(rule.name)
			}
			if (story.storyFinished) {
				//A story sets its own finished state when it's done
				//A STORY NEEDS A CONSEQUENCE! <- Mind blown!
				stories.remove(story)
				finishedStories.add(story)
				story.consequence.apply()
			}
		}
	}

	fun addStory(story: Story) {
		story.activate()
		stories.add(story)
	}

	init {
		addStory(StoryHelper.simpleEncounters)
		addStory(StoryHelper.mainStory)
	}
}