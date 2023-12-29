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
            if( imaginaryPart.isOne() )
                return rootMinus1
            else
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
    override fun divide( other : Value,  prefs : DisplayAndComputePreferences) : Value? {
        when( other ) {
            is ComplexNumberValue -> {
                //  c+di        (ac + bd) + (ad - cd)i
                // ------  = ----------------------------
                //  a+bi          a^2 + b^2
                val c = this.realPart
                val d = this.imaginaryPart
                val a = other.realPart
                val b = other.imaginaryPart
                val aa = a.times(a, prefs)
                val bb = b.times(b, prefs)
                val denom = aa.plus(bb, prefs)
                if( denom.isZero() )
                    return null
                else {
                    val ac = a.times(c, prefs)
                    val ad = a.times(d, prefs)
                    val bd = b.times(d, prefs)
                    val bc = b.times(c, prefs)
                    val newRealPart = ac.plus( bd, prefs ).dividedBy(denom, prefs)
                    val newImaginaryPart = ad.plus( bc.negated(), prefs ).dividedBy(denom, prefs)
                    return ComplexNumberValue(newRealPart, newImaginaryPart)
                }
            }
        }
    }

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