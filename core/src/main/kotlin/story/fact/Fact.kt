package story.fact

class StringFact(override val key: String, override var value: String) : IFact<String>

class IntFact(override val key: String, override var value: Int) : IFact<Int>

class BooleanFact(override val key: String, override var value: Boolean) : IFact<Boolean>

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

fun String.parseToBoolean() : Boolean {
  return this.replace("Boolean:", "").toBoolean()
}

fun String.parseToInt() : Int {
  val int = this.replace("Int:", "").toIntOrNull()
  return if (int == null) 0 else int!!
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