package model.data.formula

import model.data.Environment
import model.data.BinaryOperator
import model.data.DisplayPreferences
import model.data.applyBinaryOperator

data class BinaryOperation(val op : BinaryOperator, val left : Formula, val right : Formula) : Formula() {
    override fun render(displayPrefs: DisplayPreferences): String {
        // TODO Eventually something more complex than a string will be needed
        // Or perhaps return a string in a latex subset language and leave
        // the rendering to boxes to a layer closer to the graphics.
        var l = left.render(displayPrefs)
        var r = right.render(displayPrefs)
        // TODO worry about right associative, associative, and non-associative operators
        if( op.precedence() < left.precedence()) l = "($l)"
        if( op.precedence() <= right.precedence()) r = "($r)"
        return "$l$op$r"
    }

    override fun expand(env: Environment): Formula {
        return BinaryOperation( op, left.expand(env), right.expand(env) )
    }

    override fun evaluate(env: Environment): Formula {
        val leftEvaluated = left.evaluate( env  )
        val rightEvaluated = right.evaluate( env )
        val evaluated = applyBinaryOperator( op, leftEvaluated, rightEvaluated)
        return evaluated
    }

    override fun freeVars(): Set<String> {
        return left.freeVars().union(right.freeVars())
    }

    override fun precedence(): Int {
        return op.precedence()
    }
}