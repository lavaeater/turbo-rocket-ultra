package mvvm

class DelegateCommand(val name: String,
                      val description: String = "",
                      override val execute: (() -> Unit),
                      override var onCanExecuteChanged: ((Boolean) -> Unit)? = null) : CommandBase()