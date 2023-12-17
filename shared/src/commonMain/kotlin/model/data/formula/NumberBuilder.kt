package model.data.formula

import model.data.Environment
import model.data.value.AComplexNumber
import model.data.value.FlexNumber
import model.data.value.NumberRendering

class NumberBuilder private constructor (
        private val numberEntryState : NumberEntryState,
        private val isNegative : Boolean,
        private val base : Int,
        private val length : Int,
        private val precision : Int,
        private val digits : List<Byte>)
: TopItem()
// NB can't be data class as it has a private constructor
{
    enum class NumberEntryState { CLOSED, BEFORE_POINT, AFTER_POINT, EXPONENT }
    companion object {
        // Factory used so that NumberBuilders are guaranteed normalized.
        fun create(numberEntryState : NumberEntryState,
                   isNegative : Boolean,
                   base : Int,
                   length : Int,
                   precision : Int,
                   digits : List<Byte>) : NumberBuilder {
            check( base > 1)
            check( base < 36 )
            check( numberEntryState != NumberEntryState.BEFORE_POINT || precision == 0)
            val lastNonZero = 1 + digits.indexOfLast { it.toInt() != 0 }
            val newDigits = if( lastNonZero == digits.size ) digits else digits.take( lastNonZero )
            val newIsNegative = if(newDigits.isEmpty()) false else isNegative
            return NumberBuilder(  numberEntryState,
                newIsNegative,
                base,
                length,
                precision,
                newDigits )
        }


        fun openZero(base: Int): NumberBuilder = create( NumberEntryState.BEFORE_POINT, false, base, 0,0, emptyList())
    }

    override fun asNumberBuilder() : NumberBuilder = this
    fun canAppendDigit(base: Int, digit: Byte): Boolean {
        return when( numberEntryState ) {
            NumberEntryState.BEFORE_POINT, NumberEntryState.AFTER_POINT, NumberEntryState.EXPONENT  ->
                base == this.base && 0 <= digit && digit < base
            NumberEntryState.CLOSED -> false
        }
    }

    fun appendDigit(base: Int, digit: Byte): NumberBuilder {
        // Note that appending a digit to the number means prepending the digit
        // to our digit list.
        if(canAppendDigit(base, digit)) {
            val newDigits = listOf(digit) + digits
            return when (numberEntryState) {
                NumberEntryState.CLOSED -> this
                NumberEntryState.BEFORE_POINT -> copy(length = length + 1, digits = newDigits)
                NumberEntryState.AFTER_POINT -> copy(
                    length = length + 1,
                    precision = precision + 1,
                    digits = newDigits
                )

                NumberEntryState.EXPONENT -> TODO()
            }
        } else return this
    }

    fun appendPoint(): NumberBuilder {
        return when( numberEntryState ) {
            NumberEntryState.CLOSED -> this
            NumberEntryState.BEFORE_POINT -> copy( numberEntryState = NumberEntryState.AFTER_POINT )
            NumberEntryState.AFTER_POINT -> this
            NumberEntryState.EXPONENT -> TODO()
        }
    }

    override fun negate() : NumberBuilder {
        return copy( isNegative = !isNegative )
    }

    /**
     * Give the digit corresponding to base to the k.
     * E.g. getDigit(0) gives the digit to the left of
     * the radix point, while getDigit(-1)
     */
    private fun getDigit(k : Int) : Byte {
        val i = k + precision
        return if( i >= digits.size) 0 else if( i < 0 ) 0 else digits[i]
    }

    override fun render( env : Environment): String =
        NumberRendering.render( isNegative, base,length,precision, {getDigit(it)} )

    fun toFormula(): Formula {
        // This always makes a FlexNumber.  We might want to make other kinds of numbers
        // depending on the mode.
        val real = FlexNumber.create(isNegative, base, length, precision, digits)
        val imaginary = FlexNumber.mkZero(base)
        val value = AComplexNumber(real, imaginary)
        return ValueFormula(value)
    }

    private fun copy(numberEntryState : NumberEntryState = this.numberEntryState,
                     isNegative: Boolean = this.isNegative,
                     base : Int = this.base,
                     length : Int = this.length,
                     precision : Int = this.precision,
                     digits : List<Byte> = this. digits
    ) = create(
        numberEntryState,
        isNegative,
        base,
        length,
        precision,
        digits)



    override fun toString(): String {
        return "NumberBuilder()"
    }
}
