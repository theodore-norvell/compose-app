package model.data.formula

import model.data.DisplayAndComputePreferences
import model.data.Environment

data class ErrorFormula(val message : String, val formula : Formula ) : Formula() {
    override fun render(displayPrefs: DisplayAndComputePreferences): String {
        return "Err[$message](${formula.render(displayPrefs)})"
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
        return formula.freeVars()
    }

    override fun asError() : ErrorFormula { return this }
}