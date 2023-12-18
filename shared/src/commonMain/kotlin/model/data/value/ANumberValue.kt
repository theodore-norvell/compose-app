package model.data.value

import kotlin.math.max


/**
 * Base class for all numbers.  A number is not a value, but
 * it can be a component of a ComplexNumber, which is a value.
 */

sealed class ANumberValue {

    abstract fun render() : String

    abstract fun isZero() : Boolean
    abstract fun negate() : ANumberValue

}
sealed class AFixedOrFlexibleNumberValue constructor(
                                                val isNegative : Boolean,
                                                val base : Int,
                                                val digits : List<Byte>,
                                                val exponent : Int ) : ANumberValue() {
    init{
        check( base > 1)
        check( base < 36 )
        // If digits is not empty the most significant digit is  not 0.
        check( digits.isEmpty() || digits[digits.size-1].toInt() != 0)
        // If all digits are 0 then digits is empty
        check( digits.isEmpty() || ! digits.all{ it.toInt() == 0 } )
        // If digits is empty, then the number is not negative
        check( digits.isNotEmpty() || !isNegative )
    }

    /**
     * Give the digit corresponding to base to the k.
     * E.g. getDigit(0) gives the digit to the left of
     * the radix point, while getDigit(-1).  This
     * Considers the exponent.  E.g.  78.65 is represented
     * As .7865e2. I.e. exponent is 2 and digits is [5, 6, 8, 7]
     * The following result are expected
     * getDigit(-2) = 5
     * getDigit(-1) = 6
     * getDigit(0) = 8
     * getDigit(1) = 7 and
     * getDigit(k) = 0 for all other values.
     */
    fun getDigit(k : Int) : Byte {
        val i = k + digits.size - exponent
        return if( i >= digits.size) 0 else if( i < 0 ) 0 else digits[i]
    }

    override fun isZero() : Boolean = digits.isEmpty()

    override fun hashCode(): Int {
        val a = if(isNegative) 13 else 11
        val b = base
        val f = digits.hashCode()
        val g = exponent
        return a + 257*(b + 257*(f+257*g))
    }

    abstract override fun negate() : AFixedOrFlexibleNumberValue
}

class FlexNumberValue
    private constructor (
        isNegative : Boolean,
        base : Int,
        digitsInput : List<Byte>,
        exponent : Int )
    : AFixedOrFlexibleNumberValue( isNegative, base, digitsInput, exponent )
{
    companion object {
        // Factory used so that Flex numbers are guaranteed normalized.
        fun create(
            isNegative: Boolean,
            base: Int,
            lengthAfterPoint: Int,
            digits: List<Byte>,
            exponent : Int
        ): FlexNumberValue {
            check(base > 1)
            check(base < 36)
            // Drop the 0 digits from the most significant end -- the far end
            // Example input 00,123,000.00  e 0
            //     So digits is [0,0,0,0,0,3,2,1,0,0], exponent is 0 and lengthAfterPoint is 2
            val lastNonZero = 1 + digits.indexOfLast { it.toInt() != 0 }
            val digits1 = if (lastNonZero == digits.size) digits else digits.take(lastNonZero)
            val digitsBeforePoint1 = digits1.size - lengthAfterPoint
            // Example
            //      digits1 is [0,0,0,0,0,3,2,1], digitsBeforePoint1 is 8-2 == 6

            // Shift the Radix point to the end of the list.
            val exponent1 = exponent + digitsBeforePoint1
            // exponent1 is 6, so our number is .12300000 e 6

            // Drop any zeros from the least significant end -- the near end
            val firstNonZero = digits1.indexOfFirst { it.toInt() != 0  }
            val numberToDrop = if(firstNonZero == -1) digits1.size else firstNonZero
            val digits2 = digits1.drop(numberToDrop)
            // Example
            //     digits2 is [3,2,1].  Our number is .123 e 6

            // If the number is 0, it should not be negative.
            val isNegative2 = if (digits2.isEmpty()) false else isNegative
            // If the number is 1 its exponent should be 1
            val exponent2 = if (digits2.isEmpty()) 1 else exponent1

            return FlexNumberValue(
                isNegative2,
                base,
                digits2,
                exponent2
            )
        }

        fun mkZero(base: Int): ANumberValue = create(false, base, 0, emptyList(), 0)
    }

    override fun negate() : FlexNumberValue {
        return copy( isNegative = !isNegative )
    }

    override fun render(): String {
        // Engineering notation
        val digitsBefore : Int = (exponent-1).mod(3 ) + 1
        val displayExponent = exponent - digitsBefore
        check(displayExponent % 3 == 0)
        check(digitsBefore in 1..3) { "exponent is $exponent digitsBefore is $digitsBefore" }
        val digitsToDisplay : Int = max(digitsBefore, digits.size)
        val digitsAfter = digitsToDisplay - digitsBefore
        val mantissa = NumberRendering.render( isNegative,
                                                base,
                                                digitsToDisplay,
                                                digitsAfter,
                                                { getDigit(it+displayExponent) })
        if ( displayExponent == 0 ) {
            return mantissa
        } else {
            val expPart = displayExponent.toString()
            return mantissa + "e" + expPart
        }
    }


    private fun copy(isNegative: Boolean = this.isNegative,
                     base : Int = this.base,
                     digits : List<Byte> = this. digits,
                     exponent: Int = this.exponent
    ) = create(
            isNegative,
            base,
            digits.size,
            digits,
            exponent )


    override fun equals(other: Any?): Boolean {
        // This is not numerical equality, as it includes
        // precision, length, and pointedNess
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other is FlexNumberValue) {
            return  other.isNegative == isNegative
                    && other.base == base
                    && other.digits == digits
                    && other.exponent == exponent
        } else {
            return false
        }
    }

    override fun toString(): String {
        return "FlexNumber($isNegative,$base,$digits,$exponent)"
    }
}
