package model.data


import model.data.formula.BinaryOperation
import model.data.formula.ErrorFormula
import model.data.formula.NumberFormula
import model.data.formula.Formula
import model.data.formula.UnaryOperation

fun applyUnaryOperator(op : UnaryOperator,right : Formula) : Formula {
    // TODO This is going to need some thought. For now, just something simple.
    // Propagate errors
    when (right.asError()) {
        null -> return right
        // No errors
        else -> {
            // If both operands can beå  converted to floating point, do so
            val r = right.asFloatNumber()
            when {
                r == null -> {
                    // If one or the other can not be reduced to floating point,
                    // return the formula as simplified as possible.

                    return UnaryOperation(op, right)
                }
                else -> {
                    // Both can be treated as floating point.
                    // Apply the operator.
                    when (op) {
                        UnaryOperator.NEGATE ->
                            return TODO()
                    }
                }
            }
        }
    }
}
