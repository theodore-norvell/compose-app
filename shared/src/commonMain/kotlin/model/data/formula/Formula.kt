package model.data.formula

import model.data.DisplayAndComputePreferences
import model.data.Environment
import model.data.UnaryOperator
import model.data.value.Value

abstract class Formula : TopItem() {

    override fun asNumberBuilder() : NumberBuilder? = null

    override fun toFormula() : Formula = this
    abstract override fun render(displayPrefs: DisplayAndComputePreferences): String

    abstract fun expand( env : Environment) : Formula

    abstract override fun eval(
        prefs : DisplayAndComputePreferences,
        env: Environment,
        emitError: (String) -> Unit
    ) : Formula

    abstract fun freeVars( ) : Set<String>

    open fun asError() : ErrorFormula? = null

    open fun asValue() : Value? = null

    // Bigger number means lower precedence.
    open fun precedence() : Int = 0

    override fun negate(): Formula = UnaryOperation( op = UnaryOperator.NEGATE, this )

}