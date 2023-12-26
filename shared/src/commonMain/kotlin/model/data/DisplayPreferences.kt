package model.data

data class DisplayPreferences(
    val base : Int,
    val mode : NumberDisplayMode,
    val maxDigits : Int,
    val maxLengthAfterPoint : Int,
    val groupLengthBefore : Int,
    val groupLengthAfter : Int,
    val separatorBefore : Char,
    val separatorAfter : Char,
    val radixPoint : Char ,
)