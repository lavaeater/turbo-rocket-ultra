package ui.mvvm

import kotlin.properties.Delegates

abstract class CommandBase : Command {
	override var canExecute: Boolean by Delegates.observable(true) { _, _, newValue ->
		onCanExecuteChanged?.invoke(newValue)
	}
}