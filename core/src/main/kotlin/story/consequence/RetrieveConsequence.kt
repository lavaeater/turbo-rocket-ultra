package story.consequence

interface RetrieveConsequence<out T>: Consequence {
  fun retrieve() : T
}