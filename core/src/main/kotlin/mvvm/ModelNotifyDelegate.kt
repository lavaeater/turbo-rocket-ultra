package mvvm

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Returns a property delegate for a read/write property that calls a specified callback function when changed.
 * @param initialValue the initial value of the property.
 *  has already been changed when this callback is invoked.
 *
 */
fun <R: NotifyPropertyChanged, T> notifyChanged(initialValue: T):
		ReadWriteProperty<R, T> = NotifyProperty(initialValue)



/**
 * Implements the core logic of a property delegate for a read/write property that calls callback functions when changed.
 * @param initialValue the initial value of the property.
 */
open class NotifyProperty<R: NotifyPropertyChanged, T>(initialValue: T) : ReadWriteProperty<R, T> {
	private var value = initialValue

	/**
	 * Only returns true if newValue is different from oldValue.
	 */
	protected open fun beforeChange(property: KProperty<*>, oldValue: T, newValue: T): Boolean = oldValue != newValue

	override fun getValue(thisRef: R, property: KProperty<*>): T {
		return value
	}

	override fun setValue(thisRef: R, property: KProperty<*>, value: T) {
		val oldValue = this.value
		if (!beforeChange(property, oldValue, value)) {
			return
		}
		this.value = value
		thisRef.propertyChanged(property.name, value as Any)
	}
}