package model.state

data class ButtonDescription(val primaryOperation : ButtonOperation, val secondaryOperation: ButtonOperation)

data class ButtonOperation( val name : String, val clickAction : (CalculatorModel) -> Unit )
class Descriptions {
    companion object {
        val RCL = ButtonDescription(
                ButtonOperation("RCL") {
                    it.todo()
                },
                ButtonOperation( "TBD") {
                    it.todo()
                }
            )
        val STO = ButtonDescription(
            ButtonOperation("STO") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val X = ButtonDescription(
            ButtonOperation("X") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val Y = ButtonDescription(
            ButtonOperation("Y") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val Z = ButtonDescription(
            ButtonOperation("Z") {
                it.todo()
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
                it.todo()
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
            ButtonOperation("<-") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val DIVIDE = ButtonDescription(
            ButtonOperation("÷") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val MULTIPLY = ButtonDescription(
            ButtonOperation("×") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val ADD = ButtonDescription(
            ButtonOperation("+") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val SUBTRACT = ButtonDescription(
            ButtonOperation("-") {
                it.todo()
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
        val E10 = ButtonDescription(
            ButtonOperation("E") {
                it.todo()
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )
        val DIGIT = {d : Byte ->ButtonDescription(
            ButtonOperation(d.toString()) {
                it.appendDigit(d)
            },
            ButtonOperation( "TBD") {
                it.todo()
            }
        )}
    }

}
