package model.data.value

import model.data.ComputePreferences
import kotlin.math.max
import kotlin.math.min


/**
 * Base class for all numbers.  A number is not a value, but
 * it can be a component of a ComplexNumber, which is a value.
 */

sealed class ANumber {

    abstract fun render() : String

    abstract fun isZero() : Boolean
    abstract fun negated() : ANumber

    abstract fun add( other : ANumber, computePrefs : ComputePreferences) : ANumber
}
sealed class AFixedOrFlexibleNumber constructor(
                                                val isNegative : Boolean,
                                                val base : Int,
                                                val digits : List<Byte>,
                                                val exponent : Int ) : ANumber() {
    init{
        check( base > 1)
        check( base <= 36 )
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

    abstract override fun negated() : AFixedOrFlexibleNumber
}

class FlexNumber
    private constructor (
        isNegative : Boolean,
        base : Int,
        digitsInput : List<Byte>,
        exponent : Int )
    : AFixedOrFlexibleNumber( isNegative, base, digitsInput, exponent )
{
    companion object {
        // Factory used so that Flex numbers are guaranteed normalized.
        fun create(
            isNegative: Boolean,
            base: Int,
            lengthAfterPoint: Int,
            digits: List<Byte>,
            exponent : Int
        ): FlexNumber {
            check(base > 1)
            check(base <= 36)
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

            return FlexNumber(
                isNegative2,
                base,
                digits2,
                exponent2
            )
        }

        fun mkZero(base: Int): FlexNumber = create(false, base, 0, emptyList(), 0)
    }

    override fun negated() : FlexNumber {
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
                                                { getDigit(it+displayExponent) },
                                                digitsAfter > 0)
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
        if (other is FlexNumber) {
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

    /** Divide by a small natural number
     *
     * result.first is the quotient
     * result.second is the remainder
     *
     * The number of output digits is size by default.
     *
     *  When sizeBasedOnInput is true, the division is stopped after
     *  size input digit have been processed.  This is useful for
     *  integer division, in which case the size should be the number
     *  of digits before the radix point (i.e. this.exponent) and sizeBasedOnInput should be true
     *  For example dividing decimal 11 by 2 with size 2 gives 5.5 with 0 remainder by default,
     *  but 5 with remainder 1 when sizeBasedOnInput is true.
     *
     *  When this.isNegative, both the result and the remainder will be negative or zero.
     *  For example, -11 div 2, with output size 2 and sizeBasedOnInput, gives -5 with remainder -1.
     *  Thus we have q = Trunc( x/y ) and r = x - q*y for integer division.
     *
     * */
    fun dividedBy(n : Int, size : Int, sizeBasedOnInput : Boolean = false ) : Pair<FlexNumber, Int> {
        // n must be such that n*base <= the maximum integer
        check( n >= 0)
        // TODO check that n is not too big.
        val newDigitsRev = MutableList<Byte>(0) { 0 }
        // MSD at right, so work right to left
        var carry : Int = 0
        var i = digits.size - 1
        var k = 0 // Count the number of digits emitted, not counting any initial zeros.
        var nonZeroEmitted = sizeBasedOnInput
        while( k < size ) {
            val d : Int = if( i >= 0) digits[i].toInt() else 0
            val d1 = base * carry + d
            val newDigit = (d1 / n)
            newDigitsRev += newDigit.toByte()
            carry = d1 % n
            nonZeroEmitted = nonZeroEmitted || (newDigit != 0)
            if( nonZeroEmitted ) k += 1
            i -= 1
        }
        val quotient = copy( digits = newDigitsRev.asReversed().toList())
        if( this.isNegative ) carry = - carry
        return Pair( quotient, carry)
    }

    /** Multiply by a small natural number */
    fun multipliedBy(n : Int) : FlexNumber {
        // n must be such that n*base <= the maximum integer
        val newDigits = MutableList<Byte>(0) { 0 }
        // MSD at right, so work left to right
        var carry : Int = 0
        var i = 0
        while( i < digits.size || carry > 0 ){
            // Inv carry < n
            val d : Int = if( i < digits.size ) digits[i].toInt() else 0
            // d < base
            val p = (d * n) + carry
            // p <= (base-1)*n + (n-1) == base*n - 1
            val d1 = p % base
            newDigits += d1.toByte()
            carry = p/base
            // p/base <= (base*n-1)/base < n
            // carry < n
            i += 1
        }
        // Now new digits may be longer than digits, so we may need to adjust the exponent
        val newExp = (newDigits.size - digits.size) + exponent
        return copy( digits = newDigits.toList(), exponent = newExp )
    }

    fun isAnInteger() = digits.size <= exponent

    fun digitsBeforePoint() = exponent

    fun digitsAfterPoint() = max(0,digits.size-exponent)

    fun convertedToBase(b : Int, size : Int ) : FlexNumber {
        if( b == base ) {
            // Todo. Limit the result size.
            return this
        } else if( isNegative ) {
            val negResult = this.negated().convertedToBase( b, size)
            return negResult.negated()
        } else {
            // First ensure that n is an integer equal to this.base^k * this number for some non-negative k
            val digitsAfterPoint = digits.size - exponent
            val k = if( digitsAfterPoint > 0 ) digitsAfterPoint else 0
            var n = if( digitsAfterPoint > 0 ) copy( exponent = digits.size ) else this

            // Here n represents this.base^k * this number and n represents an integer.

            // Now figure out the digits of n in base b.
            val newDigits = MutableList<Byte>(0){0}
            while( ! n.isZero() ) {
                val (newN, remainder) = n.dividedBy(b, n.exponent, true)
                newDigits += remainder.toByte()
                n = newN
            }
            var result = FlexNumber.create(isNegative, b, 0, newDigits, 0)
            // Here result represents this.base^k * this number.

            // Finally, we need to divide by this.base^k
            // TODO. This could be sped up by dividing by a power of this.base that is not too big.
            for( i in (0..<k).reversed() ) {
                // Each division results in some truncation, so we use extra digit
                // except in the very final division.  The number of extra digits
                // should probably log in base b of this.base times the number of remaining
                // divisions or something like that.  I'm just going to use 5 times the
                // number of remaining divisions.
                // TODO calculate extraDigits more intelligently
                val extraDigits = 4*i
                result = result.dividedBy(this.base, size+extraDigits, false).first
            }
            return result
        }
    }

    fun add(other : FlexNumber, base : Int, size : Int ) : FlexNumber {
        val x = other.convertedToBase(base, size)
        val y = this.convertedToBase( base, size )
        val digitsAfterPoint : Int = min( x.digitsAfterPoint(), y.digitsAfterPoint())
        val inputDigitsBeforePoint : Int = max( x.digitsBeforePoint(), y.digitsBeforePoint() )
        val mX = if( x.isNegative ) -1 else 1
        val mY = if( y.isNegative ) -1 else 1
        val newDigits0 = MutableList<Byte>(0){0}
        var k = -digitsAfterPoint
        var carry0 = 0
        while( k <= inputDigitsBeforePoint ) {
            check( carry0 == 0 || carry0 == 1 || carry0 == -1 || carry0 == -2)
            val dX = x.getDigit(k) * mX
            val dY = y.getDigit(k) * mY

            var s0 = dX + dY + carry0
            if(s0 < -base) {
                s0 += 2*base ;
                carry0 = -2 ;
            } else if( s0 < 0 ) {
                s0 += base; carry0 = -1  }
            else if( s0 >= base) {
                s0 -= base; carry0 = +1  }
            else {
                carry0 = 0 }

            newDigits0 += s0.toByte()

            k += 1
        }
        check( carry0 == 0 || carry0 == -1 )

        // The problem is that, if the result is negative, we we have it in
        // in 10's (or base's) complement.   We need to convert it back to
        // a positive magnitude by subtracting the from 10000...000 where the
        // number of zeros is the number of digits in newDigits0.
        // E.g. If we add 12.3 to -456.7, we should have 555.6 with a carry out of -1,
        // Subtracting this from 1000.0 we get 444.4, which just needs to be negated.
        val resultIsNegative : Boolean = carry0 == -1
        if( resultIsNegative ) {
            var carry = 0
            var i = 0
            while( i < newDigits0.size ) {
                check( carry == 0 || carry == -1)
                var d =  carry - newDigits0[i]
                if( d < 0 ) {
                    d += base ; carry = -1
                } else {
                    carry = 0
                }
                newDigits0[i] = d.toByte()
                i += 1
            }
        }
        // TODO limit the result size to size.
        val result = FlexNumber.create(resultIsNegative, base, digitsAfterPoint, newDigits0.toList(), 0)
        return result
    }



    override fun add(other: ANumber, computePrefs: ComputePreferences) : ANumber {
        when( other ) {
            is FlexNumber -> return this.add( other, computePrefs.base, computePrefs.size )
        }
    }
}
