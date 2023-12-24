package model.data

data class ComputePreferences(
    val base : Int, // Must be in 2..36
    val size : Int // Preferred size of result
)
