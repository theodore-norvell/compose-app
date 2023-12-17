package model.data.value


/**
 * Base class for all numbers.  A number is not a value, but
 * it can be a component of a ComplexNumber, which is a value.
 */

sealed class ANumber {

    abstract fun render() : String

    abstract fun isZero() : Boolean
    abstract fun negate() : ANumber

}
sealed class AFixedOrFlexibleNumber constructor(
                        val isNegative : Boolean,
                       val base : Int,
                       val length : Int,
                       val precision : Int,
                       val digits : List<Byte>) : ANumber() {
    init{
        check( base > 1)
        check( base < 36 )
        check( digits.isEmpty() || digits[digits.size-1].toInt() != 0)
        check( digits.isEmpty() || ! digits.all{ it.toInt() == 0 } )
        check( digits.isNotEmpty() || !isNegative )
    }

    /**
     * Give the digit corresponding to base to the k.
     * E.g. getDigit(0) gives the digit to the left of
     * the radix point, while getDigit(-1)
     */
    fun getDigit(k : Int) : Byte {
        val i = k + precision
        return if( i >= digits.size) 0 else if( i < 0 ) 0 else digits[i]
    }

    override fun isZero() : Boolean = digits.isEmpty()



    override fun hashCode(): Int {
        val a = if(isNegative) 13 else 11
        val b = base
        val c = length
        val e = precision
        val f = digits.hashCode()
        return a + 257*(b + 257*(c + 257*(e + 257*f)))
    }

    abstract override fun negate() : AFixedOrFlexibleNumber
}

class FlexNumber
    private constructor (
                isNegative : Boolean,
                base : Int,
                length : Int,
                precision : Int,
                digitsInput : List<Byte>)
    : AFixedOrFlexibleNumber( isNegative, base, length, precision, digitsInput )
{
    companion object {
        // Factory used so that Flex numbers are guaranteed normalized.
        fun create(
            isNegative: Boolean,
            base: Int,
            length: Int,
            precision: Int,
            digits: List<Byte>
        ): FlexNumber {
            check(base > 1)
            check(base < 36)
            val lastNonZero = 1 + digits.indexOfLast { it.toInt() != 0 }
            val newDigits = if (lastNonZero == digits.size) digits else digits.take(lastNonZero)
            val newIsNegative = if (newDigits.isEmpty()) false else isNegative
            return FlexNumber(
                newIsNegative,
                base,
                length,
                precision,
                newDigits
            )
        }

        fun mkZero(base: Int): ANumber = create(false, base, 0, 0, emptyList())
    }

    override fun negate() : FlexNumber {
        return copy( isNegative = !isNegative )
    }

    override fun render(): String =
        NumberRendering.render( isNegative, base,length,precision, {getDigit(it)} )

    private fun copy( isNegative: Boolean = this.isNegative,
                     base : Int = this.base,
                     length : Int = this.length,
                     precision : Int = this.precision,
                     digits : List<Byte> = this. digits
    ) = create(
            isNegative,
            base,
            length,
            precision,
            digits)


    override fun equals(other: Any?): Boolean {
        // This is not numerical equality, as it includes
        // precision, length, and pointedNess
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other is FlexNumber) {
            return  other.isNegative == isNegative
                    && other.base == base
                    && other.length == length
                    && other.precision == precision
                    && other.digits == digits
        } else {
            return false
        }
    }

    override fun toString(): String {
        return "FlexNumber()"
    }
}
