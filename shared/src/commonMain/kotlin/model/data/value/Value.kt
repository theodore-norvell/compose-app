package model.data.value

import model.data.DisplayAndComputePreferences

sealed class Value {
    abstract fun render(displayPrefs: DisplayAndComputePreferences): String

    open fun negate(): Value? = null
    open fun reciprocal(): Value? = null

    open fun add( other : Value, prefs : DisplayAndComputePreferences) : Value? = null

    open fun divide( other : Value,  prefs : DisplayAndComputePreferences) : Value? = null

    open fun multiply( other : Value,  prefs : DisplayAndComputePreferences) : Value? = null

    open fun pow( other : Value,  prefs : DisplayAndComputePreferences) : Value? = null

    open fun subtract( other : Value,  prefs : DisplayAndComputePreferences) : Value? = null
}