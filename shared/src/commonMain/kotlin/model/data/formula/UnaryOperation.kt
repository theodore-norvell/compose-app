package model.data.formula

import model.data.DisplayAndComputePreferences
import model.data.Environment
import model.data.UnaryOperator
import model.data.applyUnaryOperator

data class UnaryOperation(val op : UnaryOperator, val right : Formula) : Formula() {
    override fun render(displayPrefs: DisplayAndComputePreferences): String {
        // TODO Eventually something more complex than a string will be needed
        // Or perhaps return a string in a latex subset language and leave
        // the rendering to boxes to a layer closer to the graphics.
        var r = right.render(displayPrefs)
        if( op.precedence() < right.precedence()) r = "($r)"
        return "$op$r"
    }

    override fun expand(env: Environment): Formula {
        return UnaryOperation( op, right.expand(env) )
    }

    override fun eval(
        prefs : DisplayAndComputePreferences,
        env: Environment,
        emitError: (String) -> Unit
    ): Formula {
        val rightEvaluated = right.eval(prefs, env, emitError)
        return applyUnaryOperator(op, rightEvaluated, prefs)
    }

    override fun freeVars(): Set<String> {
        return right.freeVars()
    }

    override fun precedence(): Int {
        return op.precedence()
    }


    override fun negate(): Formula = if( op == UnaryOperator.NEGATE ) right else super.negate()
}