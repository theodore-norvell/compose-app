package model.data.formula

import model.data.DisplayAndComputePreferences
import model.data.Environment

data class VariableReference(val variableName : String ) : Formula() {
    override fun asVariable(): VariableReference? = this
    
    override fun render(displayPrefs: DisplayAndComputePreferences): String {
        return variableName
    }

    override fun expand(env: Environment): Formula {
        val got : Formula? = env.get( variableName )
        return when( got ) {
            null -> this
            else -> got.expand( env )
        }
    }

    override fun eval(
        prefs : DisplayAndComputePreferences,
        env: Environment,
        emitError: (String) -> Unit
    ): Formula {
        return when( val contents = env.get(this.variableName) ) {
            null -> this
            else -> contents.eval( prefs, env,emitError )
        }
    }

    override fun freeVars(): Set<String> {
        return setOf( variableName )
    }
}