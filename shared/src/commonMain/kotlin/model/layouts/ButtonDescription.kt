package model.layouts

data class ButtonDescription(
    val primaryOperation : ButtonOperation,
    val secondaryOperation: ButtonOperation? = null,
    val weight : Float = 1f)