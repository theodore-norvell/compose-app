package model.data.formula

import model.data.DisplayAndComputePreferences
import model.data.Environment
import model.data.value.Value

data class ValueFormula(val value : Value) : Formula() { ///
    override fun render(displayPrefs: DisplayAndComputePreferences): String {
        return value.render(displayPrefs)
    }

    override fun expand(env: Environment): Formula {
        return this
    }

    override fun eval(
        prefs : DisplayAndComputePreferences,
        env: Environment,
        emitError: (String) -> Unit
    ): Formula {
        return this
    }

    override fun freeVars(): Set<String> {
        return emptySet()
    }

    override fun asValue() : Value = value
}