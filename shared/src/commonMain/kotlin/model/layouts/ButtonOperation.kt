package model.layouts

import model.state.CalculatorModel

data class ButtonOperation( val name : String, val enabled : Boolean = true, val clickAction : (CalculatorModel) -> Unit  )