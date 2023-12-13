package model.state

import model.data.formula.NumberFormula
import model.data.Environment
import model.data.arithmetic.AComplexNumber
import model.data.formula.Formula

enum class EntryState {
    NORMAL, AFTER_ENTER
}

data class CalculatorModes (
    val base : Int = 10,
    val entryState : EntryState = EntryState.AFTER_ENTER
)

data class CalculatorState(
    val top : Formula = NumberFormula( AComplexNumber.openZero( 10 ) ),
    val stack : List<Formula> = listOf(),
    val env : Environment = Environment(),
    val mode : CalculatorModes = CalculatorModes()
) {
    private fun close(): CalculatorState =
        copy( top = top.close() )

    private fun ensureAfterEnter() =
        copy( mode = mode.copy(entryState = EntryState.AFTER_ENTER) )

    private fun ensureOpen() =
        if( top.isClosed() ) {
            val state0 = if( mode.entryState == EntryState.AFTER_ENTER ) this else push( top )
            val state1 = state0.ensureReady()
            state1.copy( top = NumberFormula(AComplexNumber.openZero( mode.base )) )
        }
        else this

    private fun ensureReady() : CalculatorState =
        close().run { copy( mode = mode.copy( entryState = EntryState.NORMAL)) }

    fun appendDigit(digit : Byte) : CalculatorState =
        ensureOpen().run {
            if( top.isClosed() ) {
                this
            } else if( top.canAppendDigit( mode.base, digit ) ) {
                copy( top = top.appendDigit( mode.base, digit ) )
            } else {
                this
            }
    }

    fun appendPoint() : CalculatorState =
        ensureOpen().run {
            if( top.isClosed() ) this else copy( top = top.appendPoint( ) )
        }


    fun enter() = close().run{ push(top )}.run{ ensureAfterEnter() }

    fun push( f : Formula ) = ensureReady().copy( top = f, stack = stack+top )

    fun swap() =
        ensureReady().run {
            if( stack.isEmpty() ) this
            else copy( top = stack.last(), stack = stack.subList(0,stack.size-1) + top ) }

}