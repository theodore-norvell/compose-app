package model.data.formula

import model.data.DisplayPreferences

sealed class TopItem {
    abstract fun asNumberBuilder() : NumberBuilder?

    abstract fun negate() : TopItem

    abstract fun render(displayPrefs: DisplayPreferences): String

    abstract fun toFormula(): Formula
    open fun asVariable(): VariableReference? = null
}