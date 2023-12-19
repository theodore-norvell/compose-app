package model.state

import model.data.BinaryOperator
import model.data.Environment
import model.data.formula.BinaryOperation
import model.data.formula.Formula
import model.data.formula.NumberBuilder
import model.data.formula.TopItem
import model.data.formula.VariableReference

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
        when (top) {
            is Formula -> this
            is NumberBuilder -> copy(top = top.toFormula())
        }

    private fun ensureAfterEnter() =
        copy(mode = mode.copy(entryState = EntryState.AFTER_ENTER))

    private fun ensureOpen() =
        when (top) {
            is NumberBuilder -> this
            is Formula -> {
                val state0 = if (mode.entryState == EntryState.AFTER_ENTER) this else push(top)
                val state1 = state0.ensureReady()
                state1.copy(top = NumberBuilder.openZero(mode.base))
            }
        }


    private fun ensureReady(): CalculatorState =
        close().run { copy(mode = mode.copy(entryState = EntryState.NORMAL)) }

    fun appendDigit(digit: Byte): CalculatorState =
        ensureOpen().run {
            when (top) {
                is Formula ->
                    this// Not actually possible
                is NumberBuilder ->
                    if (top.canAppendDigit(mode.base, digit))
                        copy(top = top.appendDigit(mode.base, digit))
                    else
                        this
            }
        }

    fun appendPoint(): CalculatorState =
        ensureOpen().run {
            when (top) {
                is Formula -> this
                is NumberBuilder -> copy(top = top.appendPoint())
            }
        }

    fun startExponent(): CalculatorState =
        ensureOpen().run {
            when (top) {
                is Formula -> this
                is NumberBuilder -> copy(top = top.startExponent())
            }
        }

    fun enter() =
        when (top) {
            is Formula -> push(top).run { ensureAfterEnter() }
            is NumberBuilder -> push(top.toFormula()).run { ensureAfterEnter() }
        }

    //close().run{ push(top )}.run{ ensureAfterEnter() }

    private fun push(item: TopItem): CalculatorState {
        val f1 = top.toFormula()
        return ensureReady().copy(top = item, stack = stack + f1)
    }

    fun swap() =
        ensureReady().run {
            if (stack.isEmpty()) this
            else {
                val f1 = when (top) {
                    is NumberBuilder -> top.toFormula()
                    is Formula -> top
                }
                copy(top = stack.last(), stack = stack.subList(0, stack.size - 1) + f1)
            }
        }

    fun stackTop() : Formula? =
        if( stack.isEmpty() ) null else stack.last()

    fun popStack() : List<Formula> = stack.dropLast(1)
    fun negate(): CalculatorState = copy(top = top.negate())

    fun setBase(newBase: Int): CalculatorState =
        ensureReady().run { copy(mode = mode.copy(base = newBase)) }

    fun mkBinOp(op: BinaryOperator): CalculatorState =
        ensureReady().run {
            val left = stackTop()
            if( left == null ) this
            else {
                val right = top.toFormula()
                val newTop = BinaryOperation( op, left, right)
                copy( top = newTop, stack = popStack() )
            }
        }

    fun mkVarRef(name: String): CalculatorState =
        ensureReady().run{
            push( VariableReference(name) )
        }

    fun store(): CalculatorState =
        ensureReady().run {
            val topAsVar = top.asVariable()
            if( topAsVar == null ) this
            else if( stack.isEmpty() ) this
            else {
                val f = stack.last()
                val name = topAsVar.variableName
                if( env.canPut( name, f )) {
                    copy( top = f, stack = stack.dropLast(1), env = env.put(name, f))
                } else {
                    // TODO emit error
                    this
                }
            }
        }
}
