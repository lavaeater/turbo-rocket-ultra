package story.fact

interface IFact<T> {
  val key:String
  var value: T
}