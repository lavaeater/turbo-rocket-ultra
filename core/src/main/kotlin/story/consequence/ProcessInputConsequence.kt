package story.consequence

interface ProcessInputConsequence : Consequence {
  fun <T> processInput(value: T)
}