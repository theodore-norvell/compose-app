package model.state

import model.data.BinaryOperator

data class ButtonDescription(
    val primaryOperation : ButtonOperation,
    val secondaryOperation: ButtonOperation? = null,
    val weight : Float = 1f)

data class ButtonOperation( val name : String, val clickAction : (CalculatorModel) -> Unit )
class Descriptions {
    companion object {

        val CLEAR = ButtonDescription(
            ButtonOperation("CLEAR") {
                it.clear()
            }
        )

        val DROP = ButtonDescription(
            ButtonOperation("DROP") {
                it.drop()
            }
        )
        val EVAL = ButtonDescription(
                ButtonOperation("EVAL") {
                    it.eval()
                }
            )
        val SHIFT = ButtonDescription(
            ButtonOperation("⇧") {
                it.shift()
            },
            ButtonOperation( "⇩") {
                it.unshift()
            }
        )
        val STO = ButtonDescription(
            ButtonOperation("STO") {
                it.store()
            }
        )
        val X = ButtonDescription(
            ButtonOperation("x") {
                it.makeVarRef("x")
            }
        )
        val Y = ButtonDescription(
            ButtonOperation("y") {
                it.makeVarRef("y")
            }
        )
        val Z = ButtonDescription(
            ButtonOperation("z") {
                it.makeVarRef("z")
            }
        )
        val ENTER = ButtonDescription(
            ButtonOperation("ENTER") {
                it.enter()
            },
            weight = 2f
        )
        val NEGATE = ButtonDescription(
            ButtonOperation("+/-") {
                it.negate()
            }
        )
        val SWAP = ButtonDescription(
            ButtonOperation("SWAP") {
                it.swap()
            }
        )
        val UNDO = ButtonDescription(
            ButtonOperation("↶") {
                it.undo()
            },
            ButtonOperation( "↷") {
                it.redo()
            }
        )
        val DIVIDE = ButtonDescription(
            ButtonOperation("÷") {
                it.makeBinOp(BinaryOperator.DIVIDE)
            }
        )
        val MULTIPLY = ButtonDescription(
            ButtonOperation("×") {
                it.makeBinOp(BinaryOperator.MULTIPLY)
            }
        )
        val ADD = ButtonDescription(
            ButtonOperation("+") {
                it.makeBinOp(BinaryOperator.ADD)
            }
        )
        val SUBTRACT = ButtonDescription(
            ButtonOperation("-") {
                it.makeBinOp(BinaryOperator.SUBTRACT)
            }
        )
        val POINT = ButtonDescription(
            ButtonOperation(".") {
                it.appendPoint()
            }
        )
        val EXP = ButtonDescription(
            ButtonOperation("exp") {
                it.startExponent()
            }
        )
        val DIGIT = {d : Byte ->ButtonDescription(
            ButtonOperation(d.toString()) {
                println("DIGIT clicked")
                it.appendDigit(d)
            }
        )}
        val i = ButtonDescription(
            ButtonOperation("i") {
                it.imaginary()
            }
        )
        val TODO = ButtonDescription(
            ButtonOperation("TODO") {
                it.todo()
            },
            ButtonOperation("TODO") {
                it.todo()
            }
        )
    }

}
