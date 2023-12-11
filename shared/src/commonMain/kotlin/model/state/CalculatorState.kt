package model.state

import model.data.formula.FloatNumber
import model.data.Environment
import model.data.formula.Formula

sealed class EntryStates {
    data object READY : EntryStates();

}

data class CalculatorModes (
    val base : Int = 10,
    val entryState : EntryStates = EntryStates.READY
)

data class CalculatorState(
    val stack : List<Formula> = listOf( FloatNumber(0.0) ),
    val env : Environment = Environment(),
    val mode : CalculatorModes = CalculatorModes()
)