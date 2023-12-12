package model.state

import model.data.formula.ErrorFormula
import model.data.formula.Formula

class CalculatorModel : Observable() {
    private var state = CalculatorState()
    private var buttons : List<List<ButtonDescription>> = standardButtonLayout()

    private fun standardButtonLayout(): List<List<ButtonDescription>> {
        return listOf(
            listOf(
                Descriptions.RCL,
                Descriptions.STO,
                Descriptions.X,
                Descriptions.Y,
                Descriptions.Z
            ),
            listOf(Descriptions.ENTER, Descriptions.NEGATE, Descriptions.SWAP, Descriptions.UNDO),
            listOf(
                Descriptions.DIGIT(7),
                Descriptions.DIGIT(8),
                Descriptions.DIGIT(9),
                Descriptions.DIVIDE
            ),
            listOf(
                Descriptions.DIGIT(4),
                Descriptions.DIGIT(5),
                Descriptions.DIGIT(6),
                Descriptions.MULTIPLY
            ),
            listOf(
                Descriptions.DIGIT(1),
                Descriptions.DIGIT(2),
                Descriptions.DIGIT(3),
                Descriptions.SUBTRACT
            ),
            listOf(Descriptions.DIGIT(0), Descriptions.POINT, Descriptions.E10, Descriptions.ADD)

        )
    }

    fun top() = state.top
    fun stack() = state.stack

    fun renderStack() : List<String> =  stack().map{ it.render(env()) } + top().render(env())

    fun env() = state.env

    fun mode() = state.mode

    fun buttons() = buttons

    private fun updateState( newState : CalculatorState ) {
        state = newState
        // Here the buttons should be updated based on the new state.
        notifyAllOservers()
    }

    private fun error( message : String  )
        = updateState( state.push( ErrorFormula(message) ) )

    fun todo() = error( "TODO")

    fun addDigit( digit : Byte ) {
        // TODO take care of exponents
        val state0 = state.ensureEntering() ;
        val state1 = state0.appendDigit(digit) ;
        updateState( state1 )
    }

    fun enter() {
        val state0 = state.ensureReady()
        val state1 = state0.push(state0.top)
        updateState( state1)
    }
}