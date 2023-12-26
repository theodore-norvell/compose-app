package model.state

import model.data.BinaryOperator
import model.data.DisplayPreferences
import model.data.NumberDisplayMode

class CalculatorModel : Observable() {
    private var state = CalculatorState()
    private var buttons : List<List<ButtonDescription>> = standardButtonLayout()
    private val errors : MutableList<String> = MutableList(0) { "" }

    private var errorCounter = 0
    private fun emitError(message: String) {
        errors.add( "$message $errorCounter" ) ;
        errorCounter += 1
        updateState(state)
    }

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
            listOf(Descriptions.DIGIT(0), Descriptions.POINT, Descriptions.EXP, Descriptions.ADD)

        )
    }

    fun top() = state.top
    fun stack() = state.stack

    private fun makeDisplayPreferences() : DisplayPreferences {
        // Combine information from preferences and modes.
        when( state.mode.base ) {
            2 ->
                return DisplayPreferences(
                    base = state.mode.base,
                    mode = state.mode.displayMode,
                    maxDigits = 300,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 4,
                    groupLengthAfter = 4,
                    separatorBefore = ' ',
                    separatorAfter = ' ',
                    radixPoint = '.'
                )
            7, 8 ->
                return DisplayPreferences(
                    base = state.mode.base,
                    mode = state.mode.displayMode,
                    maxDigits = 100,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 3,
                    groupLengthAfter = 3,
                    separatorBefore = ' ',
                    separatorAfter = ' ',
                    radixPoint = '.'
                )
             16 ->
                return DisplayPreferences(
                    base = state.mode.base,
                    mode = state.mode.displayMode,
                    maxDigits = 100,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 2,
                    groupLengthAfter = 2,
                    separatorBefore = ' ',
                    separatorAfter = ' ',
                    radixPoint = '.'
                )
            else ->
                return DisplayPreferences(
                    base = state.mode.base,
                    mode = state.mode.displayMode,
                    maxDigits = 100,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 3,
                    groupLengthAfter = 3,
                    separatorBefore = ',',
                    separatorAfter = ' ',
                    radixPoint = '.'
                )
        }
    }
    fun renderTop() : String = state.top.render(makeDisplayPreferences())
    fun renderStack() : List<String> =  stack().map{ it.render(makeDisplayPreferences()) }


    fun renderEnv(): List<Pair<String, String>> {
        val keys = env().keys().sortedBy {it}
        return keys.map {Pair(it, env().get(it)!!.render(makeDisplayPreferences()))}
    }

    fun env() = state.env

    fun mode() = state.mode

    fun buttons() = buttons

    private fun updateState( newState : CalculatorState ) {
        state = newState
        // Here the buttons should be updated based on the new state.
        notifyAllOservers()
    }

    fun todo() = emitError( "TODO This is a very long and detailed error message. How do I look?" )

    fun appendDigit(digit : Byte ) = updateState( state.appendDigit( digit ) )

    fun enter() = updateState( state.enter() )

    fun appendPoint() =  updateState( state.appendPoint() )

    fun startExponent() = updateState( state.startExponent() )
    fun negate() = updateState( state.negate() )

    fun swap() = updateState( state.swap() )
    fun nextError() : String? = if( errors.isEmpty() ) null else errors[0]
    fun cancelError() {
        if( errors.isNotEmpty() ) {
            errors.removeAt(0)
            notifyAllOservers() }
    }

    fun setBase(newBase: Int) =
        // Need to update the keyboard layout too.
        updateState( state.setBase(newBase) )

    fun setDisplayMode( newMode : NumberDisplayMode) =
        updateState( state.setDisplayMode( newMode  ))

    fun makeBinOp(op: BinaryOperator) = updateState( state.mkBinOp(op) )
    fun makeVarRef(name: String) = updateState( state.mkVarRef( name ))
    fun store()  = updateState( state.store({str -> emitError(str)}) )
}