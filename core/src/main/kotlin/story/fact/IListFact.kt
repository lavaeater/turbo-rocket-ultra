package story.fact

interface IListFact<T>: IFact<MutableSet<T>> {
  fun contains(value: T):Boolean
}