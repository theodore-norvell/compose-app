package model.data.formula

import model.data.Environment
import model.data.value.Value

data class ValueFormula(val value : Value) : Formula() { ///
    override fun render(env: Environment): String {
        return value.render()
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

    override fun asFloatNumber() : ValueFormula { return this }


    override fun negate(): Formula = ValueFormula( value.negate() )
}