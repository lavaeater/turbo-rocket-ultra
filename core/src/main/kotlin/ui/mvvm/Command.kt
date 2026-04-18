package ui.mvvm

interface Command {
	var canExecute: Boolean
	var onCanExecuteChanged : ((Boolean) -> Unit)?
	val execute: (() -> Unit)
}