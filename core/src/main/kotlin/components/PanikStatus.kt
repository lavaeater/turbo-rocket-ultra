package components

sealed class PanikStatus(val name: String) {
    object NotStarted : PanikStatus("Not started")
    object Paniking: PanikStatus("Paniiik")
}