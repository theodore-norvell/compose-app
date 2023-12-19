package model.data.formula

import model.data.Environment

data class VariableReference(val variableName : String ) : Formula() {
    override fun asVariable(): VariableReference? = this
    
    override fun render(env: Environment): String {
        return variableName
    }

    override fun expand(env: Environment): Formula {
        val got : Formula? = env.get( variableName )
        return when( got ) {
            null -> this
            else -> got.expand( env )
        }
    }

    override fun evaluate(env: Environment): Formula {
        TODO("Not yet implemented")
    }

    override fun freeVars(): Set<String> {
        return setOf( variableName )
    }
}