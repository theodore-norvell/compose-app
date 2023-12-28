package model.data


import model.data.formula.ErrorFormula
import model.data.formula.Formula
import model.data.formula.UnaryOperation
import model.data.formula.ValueFormula

fun applyUnaryOperator(op : UnaryOperator,right : Formula,
                       prefs : DisplayAndComputePreferences,) : Formula {

    val default = UnaryOperation(op, right)
    when(val r = right.asValue()) {
        null -> {
            return default
        }
        else -> {
            // Both can be treated as floating point.
            // Apply the operator.
            when (op) {
                UnaryOperator.NEGATE ->
                    return when( val resultValue = r.negate() ) {
                        null -> ErrorFormula( "Operand not negatable", default)
                        else -> ValueFormula(resultValue)
                    }
            }
        }
    }
}
