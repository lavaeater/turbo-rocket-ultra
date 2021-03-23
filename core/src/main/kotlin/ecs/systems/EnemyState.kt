package ecs.systems

sealed class EnemyState {
    object ChasePlayer : EnemyState()
    object Ambling : EnemyState()
    object Seeking : EnemyState()
    object FollowAFriend : EnemyState() {

    }
}