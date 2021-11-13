package story

import com.badlogic.gdx.math.MathUtils
import data.Player
import factory.ActorFactory
import injection.Ctx
import map.IMapManager
import story.conversation.InkConversation
import story.fact.Contexts
import story.fact.Facts
import ui.IUserInterface

class StoryHelper {

	/*
	Make it possible to bundle the assets
	to some random folder, that would be pretty sweet.

	So, we use external files.

	In a particular folder, we could just add
	a story code file, then some kind of hierarchical file structure
	to add new content as needed. So the code file is parsed and if
	new characters are added, they need to have their content there.

	In this particular case, I have hard-coded the file name etc, which
	works for now, but the end goal must be code-independet content for
	reusability.
	 */


	companion object {
		val factsOfTheWorld by lazy { Ctx.context.inject<FactsOfTheWorld>() }
		val actorFactory by lazy { Ctx.context.inject<ActorFactory>() }
		val mapManager by lazy { Ctx.context.inject<IMapManager>() }
		val player by lazy { Ctx.context.inject<Player>() }

		val mainStory by lazy {
			val storyName = "MainStory"
			var npcId = "StaticTestId"
			story {
				name = storyName
				initializer = {
					/*
				Inject a factory to create a specific npc at some location in the world.

				 */

					val someTilesInRange = mapManager.getBandOfTiles(0, 0, 5, 3).filter {
						it.tile.tileType != "rock" && it.tile.tileType != "water"
					}

					val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]

					/*
				Create some world facts or something for this story.

				Do they have to be global? No, they do not!
				 */
					factsOfTheWorld.stateIntFact(Facts.subFact(Facts.StoryStep, storyName), 0)


					//Type set to townsfolk to make the behavior tree random, basically
					val npcToFind = actorFactory.addNpcAtTileWithAnimation("Flexbert", npcId, "orc", "stephenhawking", randomlySelectedTile.x, randomlySelectedTile.y)
				}
				rule {
					name = "Meeting Flexbert"
					/*
				Thing is, we can use all sorts of properties etc for
				a meeting with flexbert, that do not necessarily require
				different rules -> setting of met / not met can be done for a
				all the conversations with this guy.

				This actually persists the story, by itself, during the current game session
				at least. To persist it into preferences, it will need some work...
				*/
					context(Contexts.MetNpc)
					equalsCriterion(Facts.CurrentNpc, npcId)
					conversation {
						inkStory("conversations/flexbert.sv.ink.json") {
							/*
						Thougts: in this case we can
						certainly imagine keeping this particular story around. Maybe we will set some
						flag for the story using a different rule, opening up more options, but
						the story will remember "itself" so we can use ONE story...
						 */
						}
						beforeConversation = {
							/*
						If they have met before, set that variable in the convo. The convo itself might set it, of course
						but I want the state in the world - since that state can be persisted. On the OTHER
						hand, we might be able to serialize a list of convos to preferences...

						 */

							it.variablesState[InkConversation.MET_BEFORE] = factsOfTheWorld.getFactList(Facts.NpcsPlayerHasMet).contains(npcId)
							it.variablesState[InkConversation.PLAYER_NAME] = Ctx.context.inject<Player>().name
							it.variablesState[InkConversation.STEP_OF_STORY] = factsOfTheWorld.getIntValue(Facts.subFact(Facts.StoryStep, storyName))
							it.variablesState[InkConversation.REACTION_SCORE] = factsOfTheWorld.getIntValue(Facts.subFact(Facts.NpcReactionScore, npcId))
						}
						afterConversation = {
							/*
						save story state in prefs?
						we need a general "update basic facts about the world-method for all things
						that will be getting

						Cool thing: we can update the "step" of a story, a simple
						mechanism to keep track of "where" we are in a story.
						 */
							factsOfTheWorld.stateIntFact(Facts.subFact(Facts.StoryStep, storyName), it.variablesState[InkConversation.STEP_OF_STORY] as Int)
							factsOfTheWorld.stateIntFact(Facts.subFact(Facts.NpcReactionScore, npcId), it.variablesState[InkConversation.REACTION_SCORE] as Int)
							factsOfTheWorld.addToList(Facts.NpcsPlayerHasMet, npcId)
							factsOfTheWorld.addToList(Facts.KnownNames, "Flexbert")
						}
					}
				}
				/*
				Not everything needs to be setup in a story's initializer!

				And also, rules should probably be able to have more than one consequence!
				 */
			}
		}

		val simpleEncounters by lazy {
			story {
				name = "MeetAllTheEmployees"
				consequence {
					apply = {
						if (factsOfTheWorld.getBooleanFact(Facts.GameWon).value)
							Ctx.context.inject<IUserInterface>().showSplashScreen()
					}
				}
				rule {
					name = "WhenMeetingNpcStartConversation"
					context("MetNpc")
					conversation {
						inkStory("conversations/basic_dialog.ink.json") {} //This block can be used to set vars at time of creation, but we need something more powerful
						beforeConversation = {
							val antagonist = factsOfTheWorld.getCurrentNpc()
							if (antagonist != null) {
								it.variablesState["c_name"] = antagonist.name

								val potentialNames = mutableListOf(
										"Carl Sagan",
										"Stephen Hawking",
										"Erwin Hubble",
										"Nikolas Kopernikus",
										"Julius Caeasar",
										"Marcus Antonious",
										"Sun Tzu",
										"Mark Wahlberg",
										"Galileo Galilei",
										"Carolyn Shoemaker",
										"Sandra Faber"
								).filter { it != antagonist.name }.toMutableList()

								val correctIndex = MathUtils.random(0, 2)

								it.variablesState["name_guess_0"] = if (correctIndex == 0) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
								it.variablesState["name_guess_1"] = if (correctIndex == 1) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
								it.variablesState["name_guess_2"] = if (correctIndex == 2) antagonist.name else potentialNames.removeAt(MathUtils.random(0, potentialNames.size - 1))
								//Query the global facts to see if we have met before:
								it.variablesState[InkConversation.MET_BEFORE] = factsOfTheWorld.getFactList(Facts.NpcsPlayerHasMet).contains(antagonist.id)
								it.variablesState["first_encounter"] = factsOfTheWorld.getIntValue(Facts.MetNumberOfNpcs) == 0
							}
						}
						afterConversation = {
							val npc = factsOfTheWorld.getCurrentNpc()
							if (npc != null) {
								factsOfTheWorld.addToList(Facts.NpcsPlayerHasMet, npc.id)
								//Add to counter of this particular type
								factsOfTheWorld.addToIntFact(Facts.MetNumberOfNpcs, 1)

								if (!factsOfTheWorld.getFactList(Facts.KnownNames).contains(npc.name)
										&& it.variablesState["guessed_right"] as Int == 1) {
									factsOfTheWorld.addToIntFact(Facts.Score, 1)
									factsOfTheWorld.addToList(Facts.KnownNames, npc.name)
								}
							}
						}
					}
				}
				rule {
					name = "CheckIfScoreIsFour"
					equalsCriterion(Facts.Score, 4)
					consequence {
						apply = {
							factsOfTheWorld.stateBoolFact(Facts.GameWon, true)
							val prop by lazy { Ctx.context.inject<IUserInterface>() }
							prop.showSplashScreen()
						}
					}
				}
			}
		}
	}
}

infix fun Story.add(story: Story) {

}