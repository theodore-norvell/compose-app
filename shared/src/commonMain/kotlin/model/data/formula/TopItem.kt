package model.data.formula


import model.data.DisplayAndComputePreferences
import model.data.Environment

sealed class TopItem {
    abstract fun asNumberBuilder() : NumberBuilder?

    abstract fun negate() : TopItem

    abstract fun render(displayPrefs: DisplayAndComputePreferences): String

    abstract fun toFormula(): Formula

    abstract fun eval( prefs : DisplayAndComputePreferences, env : Environment, emitError : (String) -> Unit ) : TopItem
    open fun asVariable(): VariableReference? = null
}