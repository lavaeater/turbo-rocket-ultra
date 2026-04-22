package screens.command

fun command(name: String, init: CommandMap.() -> Unit): CommandMap {
    val command = CommandMap(name)
    command.init()
    return command
}