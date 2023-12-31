package model.data

data class DisplayAndComputePreferences(
    val base : Int = 10,
    val mode : NumberDisplayMode = NumberDisplayMode.Auto,
    val maxDigits : Int = 10,
    val maxLengthAfterPoint : Int = 10,
    val groupLengthBefore : Int = 3,
    val groupLengthAfter : Int = 3,
    val separatorBefore : Char = ',',
    val separatorAfter : Char = ' ',
    val radixPoint : Char = '.',
    val sizeLimit : Int = 20
)