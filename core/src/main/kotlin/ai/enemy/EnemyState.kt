package ai.enemy

sealed class EnemyEvent {
    object StartSeeking: EnemyEvent()
    object SpottedPlayer : EnemyEvent()
    object NoticedSomething : EnemyEvent()
    object TookDamage : EnemyEvent()
    object AttackingSomeone : EnemyEvent()
    object CooledDown : EnemyEvent()
    object GaveUp : EnemyEvent()
}

sealed class EnemyState {
    object ChasePlayer : EnemyState()
    object Ambling : EnemyState()
    object Seeking : EnemyState()
    object Attacking : EnemyState()
    object CoolDown : EnemyState()
    object MovingTowards : EnemyState()
}