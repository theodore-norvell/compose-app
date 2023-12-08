package model.state

import model.data.Environment
import model.data.formula.FloatNumber
import model.data.formula.Formula

class CalculatorState : Observable() {
    private var stack : MutableList<Formula> = MutableList( 1) { FloatNumber(0.0) }

    private val env = Environment()
    fun stack() = stack

    fun env() = env
    fun pushSomething() {
        stack.add(0, FloatNumber(42.0) )
        notifyAllOservers()
    }
}