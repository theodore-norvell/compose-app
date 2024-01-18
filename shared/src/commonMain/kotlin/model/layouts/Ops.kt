package model.layouts

import kotlin.math.E
import kotlin.math.PI
import model.data.BinaryOperator

class Ops {
    companion object {

        fun digits(name : String, digits : List<Byte>) =
            ButtonOperation(name) {
                for( d in digits ) it.appendDigit(d)
            }

        val ADD =
            ButtonOperation("+") {
                it.makeBinOp(BinaryOperator.ADD)
            }

        val POW = ButtonOperation( "b^a") { it.makeBinOp( BinaryOperator.POW) }

        val CLEAR =
            ButtonOperation("CLEAR") {
                it.clear()
            }

        val DIGIT = {d : Byte ->
            ButtonOperation(d.toString(16).uppercase()) {
                it.appendDigit(d)
            }
        }

        val DIVIDE =
            ButtonOperation("÷") {
                it.makeBinOp(BinaryOperator.DIVIDE)
            }

        val DROP =
            ButtonOperation("DROP") {
                it.drop()
            }

        val ENTER =
            ButtonOperation("ENTER") {
                it.enter()
            }

        val EVAL =
                ButtonOperation("EVAL") {
                    it.eval()
                }

        val EXP =
            ButtonOperation("exp") {
                it.startExponent()
            }

        val i =
            ButtonOperation("i") {
                it.imaginary()
            }

        val MULTIPLY =
            ButtonOperation("×") {
                it.makeBinOp(BinaryOperator.MULTIPLY)
            }

        val NEGATE =
            ButtonOperation("+/-") {
                it.negate()
            }

        val POINT =
            ButtonOperation(".") {
                it.appendPoint()
            }

        val REDO =
            ButtonOperation( "↷") {
                it.redo()
            }


        val ROLL =
            ButtonOperation("ROLL") {
                it.roll()
            }

        val SHIFT =
            ButtonOperation("⇧") {
                it.shift()
            }

        val SUBTRACT =
            ButtonOperation("-") {
                it.makeBinOp(BinaryOperator.SUBTRACT)
            }

        val STO =
            ButtonOperation("STO") {
                it.store()
            }

        val SWAP =
            ButtonOperation("SWAP") {
                it.swap()
            }

        val TODO =
            ButtonOperation("TODO") {
                it.todo()
            }


        val UNSHIFT =
            ButtonOperation( "⇩") {
                it.unshift()
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

        val UNDO =
            ButtonOperation("↶") {
                it.undo()
            }
    }

}
