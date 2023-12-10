package model.state

import model.data.formula.FloatNumber
import model.data.Environment
import model.data.formula.Formula

data class CalculatorState(
    val stack : List<Formula> = listOf( FloatNumber(0.0) ),
    val env : Environment = Environment()
)