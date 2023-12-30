package model.state

import model.data.BinaryOperator

data class ButtonDescription(val primaryOperation : ButtonOperation, val secondaryOperation: ButtonOperation)

data class ButtonOperation( val name : String, val clickAction : (CalculatorModel) -> Unit )
class Descriptions {
    companion object {

        val CLEAR = ButtonDescription(
            ButtonOperation("CLEAR") {
                it.clear()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )

        val DROP = ButtonDescription(
            ButtonOperation("DROP") {
                it.drop()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val EVAL = ButtonDescription(
                ButtonOperation("EVAL") {
                    it.eval()
                },
                ButtonOperation( "TBD") {
                    it.todo()
                }
            )
        val SECOND = ButtonDescription(
            ButtonOperation("⇧") {
                it.todo()
            },
            ButtonOperation( "⇩") {
                it.todo()
            }
        )
        val STO = ButtonDescription(
            ButtonOperation("STO") {
                it.store()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val X = ButtonDescription(
            ButtonOperation("x") {
                it.makeVarRef("x")
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val Y = ButtonDescription(
            ButtonOperation("y") {
                it.makeVarRef("y")
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val Z = ButtonDescription(
            ButtonOperation("z") {
                it.makeVarRef("z")
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val ENTER = ButtonDescription(
            ButtonOperation("ENTER") {
                it.enter()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val NEGATE = ButtonDescription(
            ButtonOperation("+/-") {
                it.negate()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val SWAP = ButtonDescription(
            ButtonOperation("SWAP") {
                it.swap()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val UNDO = ButtonDescription(
            ButtonOperation("↶") {
                it.todo()
            },
            ButtonOperation( "↷") {
                it.todo()
            }
        )
        val DIVIDE = ButtonDescription(
            ButtonOperation("÷") {
                it.makeBinOp(BinaryOperator.DIVIDE)
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val MULTIPLY = ButtonDescription(
            ButtonOperation("×") {
                it.makeBinOp(BinaryOperator.MULTIPLY)
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val ADD = ButtonDescription(
            ButtonOperation("+") {
                it.makeBinOp(BinaryOperator.ADD)
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val SUBTRACT = ButtonDescription(
            ButtonOperation("-") {
                it.makeBinOp(BinaryOperator.SUBTRACT)
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val POINT = ButtonDescription(
            ButtonOperation(".") {
                it.appendPoint()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val EXP = ButtonDescription(
            ButtonOperation("E") {
                it.startExponent()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val DIGIT = {d : Byte ->ButtonDescription(
            ButtonOperation(d.toString()) {
                println("DIGIT clicked")
                it.appendDigit(d)
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )}
        val i = ButtonDescription(
            ButtonOperation("i") {
                it.imaginary()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
    }

}
