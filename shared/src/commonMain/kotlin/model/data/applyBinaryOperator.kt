package model.data

import model.data.formula.BinaryOperation
import model.data.formula.Formula
import model.data.formula.ValueFormula

fun applyBinaryOperator(op : BinaryOperator,
                        left : Formula,
                        right : Formula,
                        computePrefs : ComputePreferences)
: Formula {
    // TODO This is going to need some thought. For now, just something simple.
    // Propagate errors
    // If both operands can beå  converted to floating point, do so
    val default = BinaryOperation(op, left, right)
    val (r, l) = Pair(left.asValue(), right.asValue())
    when {
        r == null || l == null -> {
            // If one or the other can not be reduced to floating point,
            // return the formula as simplified as possible.

            return default
        }
        else -> {
            // Both can be treated as floating point.
            // Apply the operator.
            when (op) {
                BinaryOperator.ADD ->
                    when( val resultValue =  r.add(l, computePrefs ) ) {
                        // TODO. Why would this be null
                        null -> return default
                        else -> return ValueFormula( resultValue )
                    }

                BinaryOperator.DIVIDE ->
                    return TODO()

                BinaryOperator.MULTIPLY ->
                    return TODO()

                BinaryOperator.SUBTRACT ->
                    return TODO()

            }
        }
    }
}
