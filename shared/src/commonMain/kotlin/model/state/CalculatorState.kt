package model.state

import io.ktor.utils.io.bits.withMemory
import model.data.formula.FloatNumber
import model.data.Environment
import model.data.formula.Formula

class CalculatorState : Observable() {
    private var stack : MutableList<Formula> = MutableList( 1) { FloatNumber(0.0) }
    private val env = Environment()
    private var count = 42.0
    fun stack() = stack

    fun renderStack() = stack.map{ it.render(env) }

    //fun env() = env
    fun pushSomething() {
        stack.add(0, FloatNumber( count ))
        count += 1.0
        notifyAllOservers()
    }
}