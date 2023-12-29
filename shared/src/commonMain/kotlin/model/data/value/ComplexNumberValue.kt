package model.data.value

import model.data.DisplayAndComputePreferences

data class ComplexNumberValue  (
    val realPart : ANumber,
    val imaginaryPart : ANumber
)
: Value()
{
    override fun render(displayPrefs: DisplayAndComputePreferences): String {
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

    override fun add( other : Value, prefs : DisplayAndComputePreferences) : Value? {
        when( other ) {
            is ComplexNumberValue -> {
                val newRealPart = this.realPart.plus(other.realPart, prefs)
                val newImaginaryPart = this.imaginaryPart.plus(other.imaginaryPart, prefs)
                return ComplexNumberValue(newRealPart, newImaginaryPart)
            }
        }
    }

    // TODO Complete
    override fun divide( other : Value,  prefs : DisplayAndComputePreferences) : Value? = null

    override fun multiply( other : Value,  prefs : DisplayAndComputePreferences) : Value? {
        when( other ) {
            is ComplexNumberValue -> {
                val a = this.realPart
                val b = this.imaginaryPart
                val c = other.realPart
                val d = other.imaginaryPart
                val ac = a.times(c, prefs)
                val ad = a.times(d, prefs)
                val bd = b.times(d, prefs)
                val bc = b.times(c, prefs)
                val newRealPart = ac.plus( bd, prefs )
                val newImaginaryPart = ad.plus( bc, prefs )
                return ComplexNumberValue(newRealPart, newImaginaryPart)
            }
        }
    }

    override fun subtract( other : Value,  prefs : DisplayAndComputePreferences) : Value? =
        when ( val negative = other.negate() ) {
            null -> null
            else ->add( negative, prefs)
        }
}