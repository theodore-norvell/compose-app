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
        } else {
            val imaginaryImage =    if( imaginaryPart.isOne() )
                                        rootMinus1
                                    else
                                        imaginaryPart.render(displayPrefs) + " " + rootMinus1
            return if( realPart.isZero()) {
                imaginaryImage
            } else {
                "(${realPart.render(displayPrefs)} + $imaginaryImage)"
            }
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
                val newRealPart = ac.plus( bd.negated(), prefs )
                val newImaginaryPart = ad.plus( bc, prefs )
                return ComplexNumberValue(newRealPart, newImaginaryPart)
            }
        }
    }

    override fun pow( other : Value, prefs: DisplayAndComputePreferences) : Value? {
        when( other ) {
            is ComplexNumberValue -> {
                // (a + bi)^{c+di} = (a^2 + b^2)^(c+di)/3 e^{i(c+di)arg(a+bi)}
                //      = p^{c/2} e^{-d q} ( cos(r) + i sin(r) )
                //      = s t ( cos(r) + i sin(r) )
                // where p = a^2 + b^2
                //       q = arg(a+bi) = sqrt(p)
                //       r = cq + d/2 ln(p)
                //       s = 2^(c/2)
                //       t = 2^(-d q)
                val a = this.realPart
                val b = this.imaginaryPart
                val c = other.realPart
                val d = other.imaginaryPart
                val asq = a.times(a, prefs)
                val bsq = b.times(b, prefs)
                val p = asq.plus(bsq, prefs)
                val q = p.pow( IEEENumber(0.5), prefs )
                val d_over_2 = d.dividedBy( NormalFlexNumber.mkNum(2, prefs.base), prefs)
                val r = c.times(q, prefs).plus(d_over_2.times(p.ln(prefs), prefs), prefs)
                val c_over_2 = c.dividedBy(NormalFlexNumber.mkNum(2, prefs.base), prefs )
                val s = p.pow(c_over_2, prefs)
                val t = IEEENumber.e.pow( d.times(q, prefs).negated(), prefs )
                val u = s.times(t, prefs)
                val v = u.times( r.cos(prefs), prefs )
                val w = u.times( r.sin(prefs), prefs )
                return ComplexNumberValue( v, w )
            }
        }
    }

    override fun subtract( other : Value,  prefs : DisplayAndComputePreferences) : Value? =
        when ( val negative = other.negate() ) {
            null -> null
            else ->add( negative, prefs)
        }
}