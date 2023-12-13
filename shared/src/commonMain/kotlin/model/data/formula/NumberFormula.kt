package model.data.formula

import model.data.Environment
import model.data.arithmetic.AComplexNumber

data class NumberFormula(val value : AComplexNumber) : NumericFormula() { ///
    override fun render(env: Environment): String {
        // Perhaps worry about NaNs and infinities.
        return value.render()
    }

    override fun expand(env: Environment): Formula {
        return this
    }

    override fun evaluate(env: Environment): Formula {
        return this
    }

    override fun freeVars(): Set<String> {
        return emptySet()
    }

    override fun asFloatNumber() : NumberFormula? { return this }

    override fun isClosed() : Boolean = value.isClosed()

    override fun canAppendDigit(base : Int, digit: Byte): Boolean = value.canAppendDigit( base, digit )

    override fun appendDigit(base: Int, digit: Byte) = NumberFormula( value.appendDigit( base, digit ) )

    override fun appendPoint() = NumberFormula( value.appendPoint( ) )

    override fun close() = NumberFormula( value.close() )

    override fun negate(): Formula = NumberFormula( value.negate() )
}