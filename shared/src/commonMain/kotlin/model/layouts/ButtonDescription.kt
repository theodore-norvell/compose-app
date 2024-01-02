package model.layouts

import androidx.compose.ui.graphics.Color

data class ButtonDescription(
    val primaryOperation : ButtonOperation,
    val secondaryOperation: ButtonOperation? = null,
    val weight : Float = 1f,
    val enabledBGColor : Color = Color.hsl(220.0f, 0.80f, 0.34f),
    val shiftedEnabledBGColor : Color = Color.hsl(220.0f, 0.80f, 0.50f),
    val disabledBGColor : Color = Color.hsl(220.0f, 0.30f, 0.30f),
    val disabledTextColor : Color = Color.LightGray,
    val enabledTextColor : Color = Color.Yellow,
    )