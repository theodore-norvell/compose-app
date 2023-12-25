package model.data.formula

import model.data.DisplayPreferences
import model.data.Environment

data class ErrorFormula(val message : String, val formula : Formula ) : Formula() {
    override fun render(displayPrefs: DisplayPreferences): String {
        return "Err[$message](${formula.render()})"
    }

    override fun expand(env: Environment): Formula {
        return this
    }

    override fun evaluate(env: Environment): Formula {
        return this
    }

    override fun freeVars(): Set<String> {
        return formula.freeVars()
    }

    override fun asError() : ErrorFormula { return this }
}