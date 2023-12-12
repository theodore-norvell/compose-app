package model.state

import model.data.formula.NumberFormula
import model.data.Environment
import model.data.arithmetic.AComplexNumber
import model.data.formula.Formula

data class CalculatorModes (
    val base : Int = 10
)

data class CalculatorState(
    val top : Formula = NumberFormula( AComplexNumber.openZero( 10 ) ),
    val stack : List<Formula> = listOf(),
    val env : Environment = Environment(),
    val mode : CalculatorModes = CalculatorModes()
) {
    fun appendDigit(digit : Byte) : CalculatorState {
        return if( top.isClosed() ) {
                    this
                } else if( top.canAppendDigit( mode.base, digit ) ) {
                    copy( top = top.appendDigit( mode.base, digit ) )
                } else {
                    this
                }
    }

    fun ensureEntering() =
        if( top.isClosed() ) copy(
                                top = NumberFormula(AComplexNumber.openZero( mode.base )),
                                stack = stack+top )
        else this

    fun ensureReady() =
        if( top.isClosed() ) this
        else copy( top = top.close()  )
    fun push( f : Formula ) = this.ensureReady().copy( top = f, stack = stack+top )
}