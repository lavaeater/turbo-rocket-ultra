package mvvm

import kotlin.properties.Delegates

abstract class CommandBase : Command {
	override val canExecute: Boolean by Delegates.observable(true) { _, _, newValue ->
		onCanExecuteChanged?.invoke(newValue)
	}
}