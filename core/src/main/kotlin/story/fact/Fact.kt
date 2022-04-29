package story.fact

abstract class AbstractFact<T>:IFact<T> {
  override fun toString(): String {
    return "$key: ${value.toString()}"
  }
}

class StringFact(override val key: String, override var value: String) : AbstractFact<String>()

class IntFact(override val key: String, override var value: Int) : AbstractFact<Int>()

class FloatFact(override val key: String, override var value: Float): AbstractFact<Float>()

class BooleanFact(override val key: String, override var value: Boolean) : AbstractFact<Boolean>()

class ListFact(override val key: String, override var value: MutableSet<String> = mutableSetOf()) : IListFact<String> {
  override fun contains(v: String): Boolean {
    return value.contains(v)
  }
}

fun MutableSet<String>.serializeToString() :String {
  return this.joinToString("|", "List:")
}

fun Int.serializeToString() : String {
  return "Int:$this"
}

fun Boolean.serializeToString(): String {
  return "Boolean:$this"
}

fun Float.serializeToString() : String {
  return "Float:$this"
}

fun String.serializeToString(): String {
  return "String:$this"
}

fun String.isBoolean() : Boolean{
  return this.contains("Boolean:")
}

fun String.isString() : Boolean {
  return this.contains("String:")
}

fun String.isInt() : Boolean {
  return this.contains("Int:")
}

fun String.isFloat(): Boolean {
  return this.contains("Float:")
}

fun String.parseToBoolean() : Boolean {
  return this.replace("Boolean:", "").toBoolean()
}

fun String.parseToInt() : Int {
  return replace("Int:", "").toIntOrNull() ?: 0
}

fun String.parseToFloat(): Float {
  return replace("Float:", "").toFloatOrNull() ?: 0f
}

fun String.parseToString() : String {
  return this.replace("String:", "")
}

fun String.isList() : Boolean {
  return this.contains("List:")
}

fun String.toMutableSet() : MutableSet<String> {
  return this.replace("List:","").split("|").toMutableSet()
}