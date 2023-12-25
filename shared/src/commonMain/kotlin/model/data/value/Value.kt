package model.data.value

import model.data.ComputePreferences
import model.data.DisplayPreferences

sealed class Value {
    abstract fun render(displayPrefs: DisplayPreferences): String

    abstract fun negate(): Value

    open fun add( other : Value, computePrefs : ComputePreferences) : Value? = null
}