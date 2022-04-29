package mvvm

interface Command {
	val canExecute: Boolean
	var onCanExecuteChanged : ((Boolean) -> Unit)?
	val execute: (() -> Unit)
}