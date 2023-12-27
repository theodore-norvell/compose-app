package model.data.formula

import model.data.ComputePreferences
import model.data.DisplayPreferences
import model.data.Environment

sealed class TopItem {
    abstract fun asNumberBuilder() : NumberBuilder?

    abstract fun negate() : TopItem

    abstract fun render(displayPrefs: DisplayPreferences): String

    abstract fun toFormula(): Formula

    abstract fun eval( computePrefers: ComputePreferences, env : Environment, emitError : (String) -> Unit ) : TopItem
    open fun asVariable(): VariableReference? = null
}