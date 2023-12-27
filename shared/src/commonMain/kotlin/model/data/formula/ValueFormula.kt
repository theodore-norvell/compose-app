package model.data.formula

import model.data.ComputePreferences
import model.data.DisplayPreferences
import model.data.Environment
import model.data.value.Value

data class ValueFormula(val value : Value) : Formula() { ///
    override fun render(displayPrefs: DisplayPreferences): String {
        return value.render(displayPrefs)
    }

    override fun expand(env: Environment): Formula {
        return this
    }

    override fun eval(
        computePrefs: ComputePreferences,
        env: Environment,
        emitError: (String) -> Unit
    ): Formula {
        return this
    }

    override fun freeVars(): Set<String> {
        return emptySet()
    }

    override fun asValue() : Value = value


    override fun negate(): Formula = ValueFormula( value.negate() )
}