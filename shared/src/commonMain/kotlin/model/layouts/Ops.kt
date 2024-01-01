package model.layouts

import androidx.compose.ui.text.toUpperCase
import model.data.BinaryOperator

class Ops {
    companion object {

        val CLEAR =
            ButtonOperation("CLEAR") {
                it.clear()
            }

        val DROP =
            ButtonOperation("DROP") {
                it.drop()
            }

        val EVAL =
                ButtonOperation("EVAL") {
                    it.eval()
                }
        val SHIFT =
            ButtonOperation("⇧") {
                it.shift()
            }
        val UNSHIFT =
            ButtonOperation( "⇩") {
                it.unshift()
            }

        val STO =
            ButtonOperation("STO") {
                it.store()
            }

        val X =
            ButtonOperation("x") {
                it.makeVarRef("x")
            }

        val Y =
            ButtonOperation("y") {
                it.makeVarRef("y")
            }

        val Z =
            ButtonOperation("z") {
                it.makeVarRef("z")
            }

        val ENTER =
            ButtonOperation("ENTER") {
                it.enter()
            }

        val NEGATE =
            ButtonOperation("+/-") {
                it.negate()
            }

        val SWAP =
            ButtonOperation("SWAP") {
                it.swap()
            }

        val ROLL =
            ButtonOperation("ROLL") {
                it.roll()
            }

        val UNDO =
            ButtonOperation("↶") {
                it.undo()
            }

        val REDO =
            ButtonOperation( "↷") {
                it.redo()
            }

        val DIVIDE =
            ButtonOperation("÷") {
                it.makeBinOp(BinaryOperator.DIVIDE)
            }

        val MULTIPLY =
            ButtonOperation("×") {
                it.makeBinOp(BinaryOperator.MULTIPLY)
            }

        val ADD =
            ButtonOperation("+") {
                it.makeBinOp(BinaryOperator.ADD)
            }

        val SUBTRACT =
            ButtonOperation("-") {
                it.makeBinOp(BinaryOperator.SUBTRACT)
            }

        val POINT =
            ButtonOperation(".") {
                it.appendPoint()
            }

        val EXP =
            ButtonOperation("exp") {
                it.startExponent()
            }

        val DIGIT = {d : Byte ->
            ButtonOperation(d.toString(16).uppercase()) {
                it.appendDigit(d)
            }
        }

        fun digits(name : String, digits : List<Byte>) =
            ButtonOperation(name) {
                for( d in digits ) it.appendDigit(d)
            }

        val i =
            ButtonOperation("i") {
                it.imaginary()
            }

        val TODO =
            ButtonOperation("TODO") {
                it.todo()
            }
    }

}
