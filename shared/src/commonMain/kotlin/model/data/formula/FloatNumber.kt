package model.data.formula

import model.data.Environment

data class FloatNumber( val value : Double ) : NumericFormula() { ///
    override fun render(env: Environment): String {
        // Perhaps worry about NaNs and infinities.
        return value.toString()
    }

    override fun expand(env: Environment): Formula {
        return this
    }

    override fun evaluate(env: Environment): Formula {
        return this
    }

    override fun freeVars(): Set<String> {
        return emptySet()
    }

    override fun asFloatNumber() : FloatNumber? { return this }
}