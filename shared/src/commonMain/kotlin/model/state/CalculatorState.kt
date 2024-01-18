package model.state

import model.data.BinaryOperator
import model.data.DisplayAndComputePreferences
import model.data.Environment
import model.data.NumberDisplayMode
import model.data.formula.BinaryOperation
import model.data.formula.Formula
import model.data.formula.NumberBuilder
import model.data.formula.TopItem
import model.data.formula.ValueFormula
import model.data.formula.VariableReference
import model.data.value.ComplexNumberValue
import model.data.value.FlexNumber
import model.data.value.NormalFlexNumber

enum class EntryState {
    NORMAL, AFTER_ENTER
}

enum class EvalMode {
    VALUE, FORMULA;

    override fun toString() =
        when(this) {
            VALUE -> "Val"
            FORMULA -> "Form"
        }
}

private val defaultBase = 10
private val defaultDisplayMode = NumberDisplayMode.Auto

data class CalculatorModes (
    val base : Int = defaultBase,
    val displayMode : NumberDisplayMode = defaultDisplayMode,
    val entryState : EntryState = EntryState.AFTER_ENTER,
    val evalMode : EvalMode = EvalMode.VALUE
)

data class CalculatorState(
    val top : TopItem = NumberBuilder.zero( defaultBase ),
    val stack : List<Formula> = listOf(),
    val env : Environment = Environment(),
    val mode : CalculatorModes = CalculatorModes()
) {

    private fun makeDisplayAndComputePreferences() : DisplayAndComputePreferences {
        // Combine information from preferences and modes.
        when( mode.base ) {
            2 ->
                return DisplayAndComputePreferences(
                    base = mode.base,
                    mode = mode.displayMode,
                    maxDigits = 300,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 4,
                    groupLengthAfter = 4,
                    separatorBefore = " ",
                    separatorAfter = " ",
                    radixPoint = ".",
                    sizeLimit = 255
                )
            7, 8 ->
                return DisplayAndComputePreferences(
                    base = mode.base,
                    mode = mode.displayMode,
                    maxDigits = 100,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 3,
                    groupLengthAfter = 3,
                    separatorBefore = " ",
                    separatorAfter = " ",
                    radixPoint = ".",
                    sizeLimit = 255
                )
            16 ->
                return DisplayAndComputePreferences(
                    base = mode.base,
                    mode = mode.displayMode,
                    maxDigits = 100,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 2,
                    groupLengthAfter = 2,
                    separatorBefore = " ",
                    separatorAfter = " ",
                    radixPoint = ".",
                    sizeLimit = 255
                )
            else ->
                return DisplayAndComputePreferences(
                    base = mode.base,
                    mode = mode.displayMode,
                    maxDigits = 100,
                    maxLengthAfterPoint = 20,
                    groupLengthBefore = 3,
                    groupLengthAfter = 3,
                    separatorBefore = ",",
                    separatorAfter = " ",
                    radixPoint = ".",
                    sizeLimit = 255
                )
        }
    }

    fun renderTop( emitError: (String) -> Unit) : String {
        val prefs = makeDisplayAndComputePreferences()
        val toRender = when( mode.evalMode) {
            EvalMode.VALUE ->
                when( top ) {
                    // Suppress evaluation of variables when they are on top.
                    is VariableReference -> top
                    else -> top.eval(prefs, env, emitError)
                }
            EvalMode.FORMULA -> top
        }
        return toRender.render( prefs )
    }
    fun renderStack(emitError: (String) -> Unit) : List<String> {
        val prefs = makeDisplayAndComputePreferences()
        when( mode.evalMode ) {
            EvalMode.VALUE -> return stack.map{ it.eval(prefs, env, emitError).render(prefs) }
            EvalMode.FORMULA -> return stack.map{ it.render(prefs) }
        }
    }

    fun renderEnv(emitError: (String) -> Unit): List<Pair<String, String>> {
        val prefs = makeDisplayAndComputePreferences()
        val keys = env.keys().sortedBy {it}
        val toRender = when( mode.evalMode) {
            EvalMode.VALUE ->  keys.map {Pair(it, env.get(it)!!.eval(prefs,env, emitError)) }
            EvalMode.FORMULA -> keys.map {Pair(it, env.get(it)!!)}
        }
        return toRender.map {Pair(it.first, it.second.render(prefs))}
    }

    private fun close(): CalculatorState =
        when (top) {
            is Formula -> this
            is NumberBuilder -> {
                copy(top = top.toFormula())
            }
        }

    private fun ensureAfterEnter() =
        copy(mode = mode.copy(entryState = EntryState.AFTER_ENTER))

    private fun ensureOpen() =
        when (top) {
            is NumberBuilder -> this
            is Formula -> {
                val state0 = if (mode.entryState == EntryState.AFTER_ENTER) this else push(top)
                val state1 = state0.ensureReady()
                state1.copy(top = NumberBuilder.zero(mode.base))
            }
        }

    private fun ensureReady(): CalculatorState =
        close().run { copy(mode = mode.copy(entryState = EntryState.NORMAL)) }

    fun appendDigit(digit: Byte): CalculatorState =
        ensureOpen().run {
            when (top) {
                is Formula ->
                    this// Not actually possible
                is NumberBuilder -> {
                    if (top.canAppendDigit(mode.base, digit))
                        copy(top = top.appendDigit(mode.base, digit))
                    else
                        this }
            }
        }


    fun appendPoint(): CalculatorState =
        ensureOpen().run {
            when (top) {
                is Formula -> this
                is NumberBuilder -> {
                    copy(top = top.appendPoint())
                }
            }
        }

    fun startExponent(): CalculatorState =
        ensureOpen().run {
            when (top) {
                is Formula -> this
                is NumberBuilder -> copy(top = top.startExponent())
            }
        }

    fun enter() : CalculatorState {
        val f1 = top.toFormula()
        return copy(top = f1, stack = stack + f1).ensureAfterEnter()
    }

    //close().run{ push(top )}.run{ ensureAfterEnter() }

    private fun push(item: TopItem): CalculatorState {
        val f1 = top.toFormula()
        when( mode.entryState ) {
            EntryState.NORMAL -> return ensureReady().copy(top = item, stack = stack + f1)
            EntryState.AFTER_ENTER ->  return ensureReady().copy(top = item)
        }
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

    fun setDisplayMode(newMode: NumberDisplayMode): CalculatorState =
        ensureReady().run { copy(mode = mode.copy(displayMode = newMode)) }

    fun setEvalMode(newMode: EvalMode): CalculatorState =
        ensureReady().run { copy(mode = mode.copy(evalMode = newMode)) }

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
            push( VariableReference(name) )

    fun store(emitError: (String) -> Unit): CalculatorState =
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
                    emitError( "Store Not Allowed")
                    this
                }
            }
        }

    fun eval(emitError: (String) -> Unit): CalculatorState =
        ensureReady().run {
            val evaluated = top.eval( makeDisplayAndComputePreferences(), env, emitError )
            copy( top = evaluated )
        }

    fun clear(): CalculatorState {
        val newTop = NumberBuilder.zero( mode.base )
        return copy(top = newTop ).ensureAfterEnter()
    }

    fun imaginary(): CalculatorState =
        when {
            top is NumberBuilder && ! top.isFresh() -> {
                // Might want to avoid this if the Number builder is
                // completely fresh.
                copy(top = top.imaginary())
            }
            else  -> {
                val zero = NormalFlexNumber.mkZero( mode.base )
                val one = NormalFlexNumber.mkOne( mode.base )
                val newTop = ValueFormula( ComplexNumberValue(zero,one))
                push( newTop )
            }
        }


    fun drop(): CalculatorState =
        run {
            val newTop = stackTop()
            if( newTop == null ) copy( top = NumberBuilder.zero(mode.base) )
            else {
                copy( top = newTop, stack = popStack() )
            }
        }

    fun roll(): CalculatorState =
        ensureReady().run {
            if( stack.isNotEmpty()) {
                val newTop = stack.first()
                val newStack = stack.drop(1) + top.toFormula()
                copy( top = newTop, stack = newStack )
            } else this
        }
    fun enteringExponent(): Boolean
        = top is NumberBuilder && top.numberEntryState == NumberBuilder.NumberEntryState.EXPONENT




}
