package model.state

import model.data.Environment
import model.data.formula.Formula
import model.data.formula.NumberBuilder
import model.data.formula.TopItem

enum class EntryState {
    NORMAL, AFTER_ENTER
}

private val defaultBase = 10

data class CalculatorModes (
    val base : Int = defaultBase,
    val entryState : EntryState = EntryState.AFTER_ENTER
)

data class CalculatorState(
    val top : TopItem = NumberBuilder.openZero( defaultBase ),
    val stack : List<Formula> = listOf(),
    val env : Environment = Environment(),
    val mode : CalculatorModes = CalculatorModes()
) {
    private fun close(): CalculatorState =
        when( top ) {
            is Formula -> this
            is NumberBuilder -> copy( top = top.toFormula() )
        }

    private fun ensureAfterEnter() =
        copy( mode = mode.copy(entryState = EntryState.AFTER_ENTER) )

    private fun ensureOpen() =
        when (top) {
            is NumberBuilder -> this
            is Formula -> {
                val state0 = if (mode.entryState == EntryState.AFTER_ENTER) this else push(top)
                val state1 = state0.ensureReady()
                state1.copy(top = NumberBuilder.openZero( mode.base ))
            }
        }


    private fun ensureReady() : CalculatorState =
        close().run { copy( mode = mode.copy( entryState = EntryState.NORMAL)) }

    fun appendDigit(digit : Byte) : CalculatorState =
        ensureOpen().run {
            when( top ) {
                is Formula ->
                    this// Not actually possible
                is NumberBuilder ->
                    if (top.canAppendDigit(mode.base, digit))
                        copy(top = top.appendDigit(mode.base, digit))
                    else
                        this
            } }

    fun appendPoint() : CalculatorState =
        ensureOpen().run {
            when( top ) {
                is Formula -> this
                is NumberBuilder ->  copy( top = top.appendPoint( ) ) } }

    fun enter() =
        when( top ) {
            is Formula -> push(top ).run{ ensureAfterEnter() }
            is NumberBuilder -> push(top.toFormula()).run{ ensureAfterEnter() }
        }

        //close().run{ push(top )}.run{ ensureAfterEnter() }

    private fun push(item : TopItem ) : CalculatorState {
        val f1 = when( top ) {
            is NumberBuilder -> top.toFormula()
            is Formula -> top
        }
        return ensureReady().copy( top = item, stack = stack+f1 )
    }

    fun swap() =
        ensureReady().run {
            if( stack.isEmpty() ) this
            else {
                val f1 = when( top ) {
                    is NumberBuilder -> top.toFormula()
                    is Formula -> top }
                copy( top = stack.last(), stack = stack.subList(0,stack.size-1) + f1 ) } }

    fun negate(): CalculatorState = copy( top = top.negate() )
}