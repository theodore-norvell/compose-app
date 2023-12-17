package model.data.value

sealed class Value {
    abstract fun render(): String

    abstract fun negate(): Value
}