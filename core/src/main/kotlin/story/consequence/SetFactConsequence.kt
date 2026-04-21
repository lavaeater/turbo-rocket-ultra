package story.consequence

import turbofacts.factsOfTheWorld

class SetFactConsequence(val key: String, val value: String, val typeCode: String) : Consequence {
    override val consequenceType = ConsequenceType.ApplyFactsConsequence

    override fun apply() {
        when (typeCode) {
            "b" -> factsOfTheWorld().setBooleanFact(value.toBoolean(), key)
            "i" -> factsOfTheWorld().setIntFact(value.toInt(), key)
            "f" -> factsOfTheWorld().setFloatFact(value.toFloat(), key)
            else -> factsOfTheWorld().setStringFact(value, key)
        }
    }

    fun toTextToken() = "setFact $key $value $typeCode"
}
