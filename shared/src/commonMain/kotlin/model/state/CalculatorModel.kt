package model.state

import model.data.BinaryOperator
import model.data.NumberDisplayMode

class CalculatorModel : Observable() {
    private var state = CalculatorState()
    private var buttons : List<List<ButtonDescription>> = standardButtonLayout()
    private val errors : MutableList<String> = MutableList(0) { "" }
    private val undoStack = mutableListOf<CalculatorState>()
    private val redoStack = mutableListOf<CalculatorState>()
    private var shifted = false

    private fun emitError(message: String) {
        errors.add( "$message" )
        updateState(state)
    }

    private fun standardButtonLayout(): List<List<ButtonDescription>> {
        return listOf(
            listOf(
                Descriptions.SHIFT,
                Descriptions.EVAL,
                Descriptions.STO,
                Descriptions.X,
                Descriptions.Y,
                Descriptions.Z
            ),
            listOf(
                Descriptions.ENTER,
                Descriptions.DROP,
                Descriptions.SWAP,
                Descriptions.UNDO),
            listOf(
                Descriptions.i,
                Descriptions.DIGIT(7),
                Descriptions.DIGIT(8),
                Descriptions.DIGIT(9),
                Descriptions.DIVIDE
            ),
            listOf(
                Descriptions.NEGATE,
                Descriptions.DIGIT(4),
                Descriptions.DIGIT(5),
                Descriptions.DIGIT(6),
                Descriptions.MULTIPLY
            ),
            listOf(
                Descriptions.CLEAR,
                Descriptions.DIGIT(1),
                Descriptions.DIGIT(2),
                Descriptions.DIGIT(3),
                Descriptions.SUBTRACT
            ),
            listOf(
                Descriptions.TODO,
                Descriptions.DIGIT(0),
                Descriptions.POINT,
                Descriptions.EXP,
                Descriptions.ADD)
        )
    }

    fun renderTop() : String = state.renderTop({str -> emitError(str)})
    fun renderStack() : List<String> =  state.renderStack({str -> emitError(str)})


    fun renderEnv(): List<Pair<String, String>> = state.renderEnv({str -> emitError(str)})

    fun env() = state.env

    fun mode() = state.mode

    fun buttons() = buttons

    private fun updateState( newState : CalculatorState ) {
        redoStack.clear()
        undoStack += state
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

    fun setEvalMode( newMode : EvalMode) =
        updateState( state.setEvalMode( newMode  ))

    fun makeBinOp(op: BinaryOperator) = updateState( state.mkBinOp(op) )
    fun makeVarRef(name: String) = updateState( state.mkVarRef( name ))
    fun store()  = updateState( state.store({str -> emitError(str)}) )
    fun eval()  = updateState( state.eval({str -> emitError(str)}) )
    fun clear() = updateState( state.clear() )
    fun drop() = updateState( state.drop() )
    fun imaginary() = updateState( state.imaginary() )
    fun undo() {
        if( undoStack.isNotEmpty() ) {
            redoStack += state
            state = undoStack.last()
            undoStack.removeLast()
            notifyAllOservers()
        }
    }

    fun redo() {
        if( redoStack.isNotEmpty() ) {
            undoStack += state
            state = redoStack.last()
            redoStack.removeLast()
            notifyAllOservers()
        }
    }

    fun shifted(): Boolean = shifted
    fun shift() {
        shifted = true
        notifyAllOservers()
    }

    fun unshift() {
        shifted = false
        notifyAllOservers()
    }
}