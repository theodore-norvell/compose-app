package model.data

import model.data.formula.BinaryOperation
import model.data.formula.ErrorFormula
import model.data.formula.Formula
import model.data.formula.ValueFormula

fun applyBinaryOperator(op : BinaryOperator,
                        left : Formula,
                        right : Formula,
                        prefs : DisplayAndComputePreferences)
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
                    when( val resultValue =  r.add(l, prefs ) ) {
                        // null indicates failure
                        null -> return ErrorFormula("incompatible operands", default)
                        else -> return ValueFormula( resultValue )
                    }

                BinaryOperator.DIVIDE ->
                    when( val resultValue =  r.divide(l, prefs ) ) {
                        // null indicates failure.  Including divide by 0.
                        null -> return ErrorFormula("incompatible operands", default)
                        else -> return ValueFormula( resultValue )
                    }

                BinaryOperator.MULTIPLY ->
                    when( val resultValue =  r.multiply(l, prefs ) ) {
                        // null indicates failure
                        null -> return ErrorFormula("incompatible operands", default)
                        else -> return ValueFormula( resultValue )
                    }

                BinaryOperator.SUBTRACT ->
                    when( val resultValue =  r.subtract(l, prefs ) ) {
                        // null indicates failure
                        null -> return ErrorFormula("incompatible operands", default)
                        else -> return ValueFormula( resultValue )
                    }

            }
        }
    }
}
