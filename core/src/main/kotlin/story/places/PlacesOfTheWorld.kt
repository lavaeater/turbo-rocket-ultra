package story.places

import data.Player
import factories.factsOfTheWorld
import gamestate.GameEvent
import gamestate.GameState
import injection.Context.inject
import statemachine.StateMachine
import story.conversation.ConversationManager
import story.conversation.InternalConversation

class PlacesOfTheWorld {

  val player by lazy { inject<Player>() }
  val gameState by lazy { inject<StateMachine<GameState, GameEvent>>() }
  val conversationManager by lazy { inject<ConversationManager>() }
//  val mapManager by lazy { inject<IMapManager>() }
//  val actorFactory by lazy { inject<ActorFactory>() }
  val factsOfTheWorld by lazy { factsOfTheWorld() }

  init {
//    val someTilesInRange = mapManager.getBandOfTiles(player.currentX, player.currentY,
//        20, 7).filter {
//      it.tile.tileType != "rock" && it.tile.tileType != "water"
//    }.toMutableList()
//    for(city in 0..10) {
//
//      val randomlySelectedTile = someTilesInRange[MathUtils.random(0, someTilesInRange.count() - 1)]
//      someTilesInRange.remove(randomlySelectedTile)
//      val tilesInRangeOfSelected = mapManager.getTilesInRange(randomlySelectedTile.x, randomlySelectedTile.y, 5)
//      //Remove a lot of tiles from the band of possible tiles to have the city at
//      for(tile in tilesInRangeOfSelected)
//        someTilesInRange.remove(tile)
//
//      actorFactory.addFeatureEntity("city_$city", randomlySelectedTile.x, randomlySelectedTile.y)
    }


  fun enterPlace(place:Place) {
  }
}
    /*
    show some shit for a city. For now, how about we show a little conversation?
     */
//    gameState.handleEvent(GameEvents.DialogStarted)
//    conversationManager.startConversation(
//        anotherConvo(),
//        {
//          //set some facts?
//          var bla = "Blo"
//        },
//        true,
//        false)


//  private fun createPlaceConvo() :InlineConvo {
//
////    val antagonistLines = mutableMapOf<Int, List<String>>()
////
////    antagonistLines[0] = listOf(
////            "Välkommen till staden ${factsOfTheWorld.getStringFact(Factoids.CurrentPlace).value}!",
////            "Ödemarkens sanna pärla!",
////            "Vill du hedra oss med ett besök?"
////        )
////    antagonistLines[1] = listOf(
////        "Än så länge kan man inget göra i städer!"
////    )
////    return InlineConvo(player, antagonistLines = antagonistLines)
//   }

//  private fun anotherConvo() : InternalConversation {
//    return convo {
//      startingStepKey = "start"
//      step {
//        key = "start"
//        addLine("Välkommen!")
//        addLine("Du är säkert trött sedan resan")
//        addLine("- kom in och ta ett glas")
//        positive("entered_house", "Ja tack, gärna, jag är otroligt törstig")
//        abort("abort", "Nej tack, så törstig är jag inte.")
//        rude("abort", "Nej tack, så du är jag inte att jag dricker ditt vatten.")
//      }
//      step {
//        key = "entered_house"
//        addLine("Du har säkert rest länge och väl.")
//        addLine("Ödemarken är inte snäll mot en vandrares fötter.")
//        addLine("Här, drick vatten!")
//        positive("is_poisoned", "Ja, gud så törstig jag är!")
//        abort("abort", "Nej, vid närmare eftertanke kom jag nog på att jag måste gå nu!")
//      }
//      step {
//        key = "is_poisoned"
//        addLine("Ha! HA! HAHA!")
//        addLine("Det var inte vatten, din idiot")
//        addLine("Det är gift!")
//        addLine("Men du dör inte. Oroa dig inte.")
//        addLine("Nej, du kommer bli slö. trött och dum.")
//        addLine("Du kommer lyda allt jag säger. Väldigt bra...")
//        addLine("...för en slav i gruvan")
//        rude("abort", "FAN ta dig!")
//      }
//    }
//  }
//}