package model.data.formula

import model.data.Environment

data class ErrorFormula(val message : String ) : Formula {
    override fun render(env: Environment): String {
        return message
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

    override fun asError() : ErrorFormula? { return this }
}