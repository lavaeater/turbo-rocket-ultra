package mvvm

/**
 * Interface to
 */
interface NotifyPropertyChanged {
	fun addPropertyChangedHandler(handler: (propertyName: String, newValue: Any) -> Unit)
	fun propertyChanged(propertyName: String, newValue: Any)
}