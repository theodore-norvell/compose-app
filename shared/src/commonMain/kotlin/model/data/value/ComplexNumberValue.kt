package model.data.value

import model.data.ComputePreferences
import model.data.DisplayPreferences

data class ComplexNumberValue  (
    val realPart : ANumber,
    val imaginaryPart : ANumber
)
: Value()
{
    override fun render(displayPrefs: DisplayPreferences): String {
        val rootMinus1 = "i"
        if( imaginaryPart.isZero() ) {
            return realPart.render(displayPrefs)
        } else if( realPart.isZero()) {
            return imaginaryPart.render(displayPrefs) + " " + rootMinus1
        } else {
            return "(${realPart.render(displayPrefs)} + ${imaginaryPart.render(displayPrefs)} $rootMinus1)"
        }
    }
    override fun negate(): ComplexNumberValue = copy( realPart = realPart.negated(), imaginaryPart = imaginaryPart.negated())

    override fun add( other : Value, computePrefs : ComputePreferences) : Value? {
        when( other ) {
            is ComplexNumberValue -> {
                val newRealPart = this.realPart.add(other.realPart, computePrefs)
                val newImaginaryPart = this.imaginaryPart.add(other.imaginaryPart, computePrefs)
                return ComplexNumberValue(newRealPart, newImaginaryPart)
            }
        }
    }
}