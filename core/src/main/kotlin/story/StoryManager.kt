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
				.sortedByDescending { it.matchingRule?.criteriaCount } //Grab the ones with the most criteria first

		/*
		Consequences MUST be self-contained, I realize this now
		they need to lazy-load all dependencies and just do their THANG

		Thing is, the code below will not wait for the consequences of stories before it continues...
		how do we implement this?

		A call back? Some kind of async mechanism?

		I think some kind of simple callback mechanism to do one story at a time,
		somehow. So to begin with we do "more complicated story first" if there are more than one!
		 */
		for(story in matchingStories) {
			for(rule in factsOfTheWorld.rulesThatPass(story.rules.toSet())) {
				if (rule != null) {
					rule.consequence.apply()
					story.finishedRules.add(rule.name)
				}
				if (story.storyFinished) {
					//A story sets its own finished state when it's done
					finishedStories.add(story)
					story.consequence.apply()
					if (!story.neverEnding)
						stories.remove(story)
					else
						story.reset()
				}
			}
		}
	}

	fun activate() {
		for(story in stories)
			story.activate()
	}

	fun addStory(story: Story) {
//		story.activate()
		stories.add(story)
	}
}