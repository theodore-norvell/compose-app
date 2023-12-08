package model.state

open class Observable() {
    // Signal - Slot

    class Connection

    private val callbacks = mutableMapOf<Connection, (Connection) -> Unit>()

    protected fun notifyAllOservers() {
        for(c in callbacks.keys) callbacks[c]!!(c)
    }

    fun connect(callback: (Connection) -> Unit) : Connection {
        val connection = Connection()
        callbacks[connection] = callback
        return connection
    }

    fun disconnect(connection : Connection) {
        callbacks.remove(connection)
    }
}