package mvvm

abstract class ViewModelBase : NotifyPropertyChanged {

	private val propertyChangedHandlers = mutableListOf<(String, Any) -> Unit>()

	override fun addPropertyChangedHandler(handler: (propertyName: String, newValue: Any) -> Unit) {
		propertyChangedHandlers.add(handler)
	}

	override fun propertyChanged(propertyName: String, newValue: Any) {
		for(handler in propertyChangedHandlers)
			handler.invoke(propertyName, newValue)
	}
}