package model.data.formula

import model.data.DisplayPreferences
import model.data.Environment
import model.data.UnaryOperator

abstract class Formula : TopItem() {

    override fun asNumberBuilder() : NumberBuilder? = null

    override fun toFormula() : Formula = this
    abstract override fun render(displayPrefs: DisplayPreferences): String

    abstract fun expand( env : Environment) : Formula

    abstract fun evaluate( env : Environment) : Formula

    abstract fun freeVars( ) : Set<String>

    open fun asError() : ErrorFormula? = null

    open fun asFloatNumber() : ValueFormula? = null

    // Bigger number means lower precedence.
    open fun precedence() : Int = 0
    override fun negate(): Formula = UnaryOperation( op = UnaryOperator.NEGATE, this )

}