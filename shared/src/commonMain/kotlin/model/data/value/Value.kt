package model.data.value

import model.data.ComputePreferences

sealed class Value {
    abstract fun render(): String

    abstract fun negate(): Value

    open fun add( other : Value, computePrefs : ComputePreferences) : Value? = null
}