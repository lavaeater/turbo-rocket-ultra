package story.fact

class Facts {
  companion object {
    fun subFact(factKey:String, subKey: String) : String {
      return "$factKey.$subKey"
    }
    const val Context ="Context"
    const val NpcsPlayerHasMet = "NpcsPlayerHasMet"
    const val CurrentNpc = "CurrentNpc"
    const val CurrentPlace = "CurrentPlace"
    const val MetNumberOfNpcs ="MetNumberOfNpcs"
    const val VisitedPlaces = "VisitedPlaces"
    const val FoundKey = "FoundKey"
    const val MetOrcs = "MetOrcs"
    const val NumberOfVisitedPlaces = "NumberOfVisitedPlaces"
    const val CurrentNpcName = "CurrentNpcName"
    const val Score = "Score"
    const val KnownNames = "KnownNames"
	  const val GameWon = "GameWon"
    const val StoryStep = "StoryStep" //subfact
    const val NpcReactionScore = "ReactionScore" //subfact
	  const val PlayerTileX = "PlayerTileX"
    const val PlayerTileY = "PlayerTileY"
  }
}