package turbofacts

object NewFactTester {
    val rules = listOf(TurboRule().apply {
        this.criteria.add(Criterion.BooleanCriteria.Any.IsTrue("Enemy.*.ReachedWayPoint"))
        this.consequence = {
            //inject<AudioPlayer>().playNextIfEmpty(AudioChannels.simultaneous, Assets.newSoundEffects.getRandomSoundFor("zombies","groans"))
        }
    })

}