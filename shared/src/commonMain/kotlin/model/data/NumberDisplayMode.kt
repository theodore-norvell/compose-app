package model.data

enum class NumberDisplayMode{
    Scientific, Engineering, NoExponent, Auto;

    override fun toString() =
        when( this ) {
            Scientific -> "Sci"
            Engineering -> "Eng"
            NoExponent -> "Int"
            Auto -> "Auto"
        }
}