package model.data

import model.data.formula.BinaryOperation
import model.data.formula.Formula

fun applyBinaryOperator(op : BinaryOperator, left : Formula, right : Formula) : Formula {
    // TODO This is going to need some thought. For now, just something simple.
    // Propagate errors
    when( left.asError() ) {
        null -> return left
        else -> {
            when (right.asError()) {
                null -> return right
                // No errors
                else -> {
                    // If both operands can beå  converted to floating point, do so
                    val (r, l) = Pair(left.asFloatNumber(), right.asFloatNumber())
                    when {
                        r == null || l == null -> {
                            // If one or the other can not be reduced to floating point,
                            // return the formula as simplified as possible.

                            return BinaryOperation(op, left, right)
                        }
                        else -> {
                            // Both can be treated as floating point.
                            // Apply the operator.
                            when (op) {
                                BinaryOperator.ADD ->
                                    return TODO()

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
            }
        }
    }
}
