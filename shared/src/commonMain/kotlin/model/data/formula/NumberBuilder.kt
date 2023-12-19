package model.data.formula

import model.data.Environment
import model.data.value.ComplexNumberValue
import model.data.value.FlexNumberValue
import model.data.value.NumberRendering

class NumberBuilder private constructor (
    private val numberEntryState : NumberEntryState,
    private val isNegative : Boolean,
    private val base : Int,
    private val lengthAfterPoint : Int,
    private val digits : List<Byte>,
    private val exponent : Int,
    private val exponentSign: Int )
: TopItem()
// NB can't be data class as it has a private constructor
{
    enum class NumberEntryState { BEFORE_POINT, AFTER_POINT, EXPONENT }
    companion object {
        fun openZero(base: Int): NumberBuilder = NumberBuilder( NumberEntryState.BEFORE_POINT, false, base, 0, emptyList(), 0, +1)
    }

    override fun asNumberBuilder() : NumberBuilder = this
    fun canAppendDigit(base: Int, digit: Byte): Boolean {
        return when( numberEntryState ) {
            NumberEntryState.BEFORE_POINT, NumberEntryState.AFTER_POINT ->
                base == this.base && digit in 0 ..< base
            NumberEntryState.EXPONENT  ->
                digit in 0..<10
        }
    }

    fun appendDigit(base: Int, digit: Byte): NumberBuilder {
        // Note that appending a digit to the number means prepending the digit
        // to our digit list.
        return if(canAppendDigit(base, digit)) {
            val newDigits = listOf(digit) + digits
            when (numberEntryState) {
                NumberEntryState.BEFORE_POINT -> copy(digits = newDigits)
                NumberEntryState.AFTER_POINT -> copy(
                    lengthAfterPoint = lengthAfterPoint + 1,
                    digits = newDigits
                )

                NumberEntryState.EXPONENT -> {
                    val newExponent = 10*exponent + digit.toInt()
                    copy( exponent = newExponent )
                }
            }
        } else this
    }

    fun appendPoint(): NumberBuilder {
        return when( numberEntryState ) {
            NumberEntryState.BEFORE_POINT -> copy( numberEntryState = NumberEntryState.AFTER_POINT )
            NumberEntryState.AFTER_POINT -> this
            NumberEntryState.EXPONENT -> this
        }
    }

    fun startExponent() : NumberBuilder =
        when( numberEntryState ) {
            NumberEntryState.BEFORE_POINT, NumberEntryState.AFTER_POINT ->
                copy( numberEntryState = NumberEntryState.EXPONENT )
            NumberEntryState.EXPONENT -> this
        }

    override fun negate() : NumberBuilder {
        return when( numberEntryState ) {
            NumberEntryState.BEFORE_POINT, NumberEntryState.AFTER_POINT ->
                copy( isNegative = !isNegative )
            NumberEntryState.EXPONENT ->
                copy( exponentSign = exponentSign * -1 )
        }
    }

    /**
     * Give the digit corresponding to base to the k.
     * E.g. getDigit(0) gives the digit to the left of
     * the radix point, while getDigit(-1).
     * There is no consideration of the exponent here.
     */
    private fun getDigit(k : Int) : Byte {
        val i = k + lengthAfterPoint
        return if( i >= digits.size) 0 else if( i < 0 ) 0 else digits[i]
    }

    override fun render( env : Environment): String {
        val length = digits.size
        return when (numberEntryState) {
            NumberEntryState.BEFORE_POINT ->
                NumberRendering.render(isNegative, base, length, lengthAfterPoint, { getDigit(it) }, false)
            NumberEntryState.AFTER_POINT ->
                NumberRendering.render(isNegative, base, length, lengthAfterPoint, { getDigit(it) })
            NumberEntryState.EXPONENT -> {
                val mantissa = NumberRendering.render(
                    isNegative,
                    base,
                    length,
                    lengthAfterPoint,
                    { getDigit(it) })
                val sign = if (exponentSign < 0) "-" else ""
                val exp = exponent.toString(10)
                mantissa + "e" + sign + exp
            }
        }
    }

    override fun toFormula(): Formula {
        // This always makes a FlexNumber.  We might want to make other kinds of numbers
        // depending on the mode.
        val real = FlexNumberValue.create(isNegative, base, lengthAfterPoint, digits, exponent*exponentSign)
        val imaginary = FlexNumberValue.mkZero(base)
        val value = ComplexNumberValue(real, imaginary)
        return ValueFormula(value)
    }

    private fun copy(numberEntryState : NumberEntryState = this.numberEntryState,
                     isNegative: Boolean = this.isNegative,
                     base : Int = this.base,
                     lengthAfterPoint : Int = this.lengthAfterPoint,
                     digits : List<Byte> = this. digits,
                     exponent : Int = this.exponent,
                     exponentSign : Int = this.exponentSign
    ) = NumberBuilder(
        numberEntryState,
        isNegative,
        base,
        lengthAfterPoint,
        digits,
        exponent,
        exponentSign)



    override fun toString(): String {
        return "NumberBuilder()"
    }
}
