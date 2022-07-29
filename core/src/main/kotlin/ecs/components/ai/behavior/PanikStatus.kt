package ecs.components.ai.behavior

sealed class PanikStatus(val name: String) {
    object NotStarted : PanikStatus("Not started")
    object Paniking: PanikStatus("Paniiik")
}