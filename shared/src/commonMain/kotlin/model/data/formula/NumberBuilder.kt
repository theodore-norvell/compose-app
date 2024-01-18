package model.data.formula

import model.data.DisplayAndComputePreferences
import model.data.Environment
import model.data.value.ComplexNumberValue
import model.data.value.FlexNumber
import model.data.value.NormalFlexNumber
import model.data.value.NumberRendering

class NumberBuilder private constructor (
    val numberEntryState : NumberEntryState,
    val isNegative : Boolean,
    val base : Int,
    val lengthAfterPoint : Int,
    val digits : List<Byte>,
    val exponent : Int,
    val exponentSign: Int,
    val isImaginary: Boolean = false )
: TopItem()
// NB can't be data class as it has a private constructor
{
    enum class NumberEntryState { BEFORE_POINT, AFTER_POINT, EXPONENT }
    companion object {
        fun zero(base: Int): NumberBuilder = NumberBuilder( NumberEntryState.BEFORE_POINT, false, base, 0, emptyList(), 0, +1)
    }

    override fun asNumberBuilder() : NumberBuilder = this

    fun isFresh() =
        numberEntryState == NumberEntryState.BEFORE_POINT && !isNegative && digits.isEmpty()

    fun canAppendDigit(base: Int, digit: Byte): Boolean {
        return when( numberEntryState ) {
            NumberEntryState.BEFORE_POINT, NumberEntryState.AFTER_POINT ->
                base == this.base && digit in 0 ..< base
            NumberEntryState.EXPONENT  ->
                digit in 0..<10
        }
    }

    private fun convertBaseTo(base : Int, prefs: DisplayAndComputePreferences ) : NumberBuilder {
        if( base == this.base ) return this
        else {
            val number =  NormalFlexNumber.create(isNegative, this.base, lengthAfterPoint, digits, exponent*exponentSign)
            val inNewBase = number.convertedToBase( prefs )
            val newDigits = inNewBase.digits
            val newLengthAfterPoint = newDigits.size - inNewBase.exponent
            when (numberEntryState) {
                NumberEntryState.BEFORE_POINT ->
                    return copy( base = base, lengthAfterPoint = newLengthAfterPoint, digits = newDigits )
                NumberEntryState.AFTER_POINT ->
                    return copy( base = base, lengthAfterPoint = newLengthAfterPoint, digits = newDigits )

                NumberEntryState.EXPONENT -> {
                    return copy( base = base, lengthAfterPoint = newLengthAfterPoint, digits = newDigits,
                        exponent = 0)
                }
            }
        }
    }

    fun appendDigit(base: Int, digit: Byte): NumberBuilder {
        // Note that appending a digit to the number means prepending the digit
        // to our digit list.
        if(canAppendDigit(base, digit)) {
            val newDigits = listOf(digit) + digits
            when (numberEntryState) {
                NumberEntryState.BEFORE_POINT -> return copy(digits = newDigits)
                NumberEntryState.AFTER_POINT -> return copy(
                    lengthAfterPoint = lengthAfterPoint + 1,
                    digits = newDigits
                )

                NumberEntryState.EXPONENT -> {
                    val newExponent = 10*exponent + digit.toInt()
                    return copy( exponent = newExponent )
                }
            }
        } else
            return this
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

    fun appendDigits(digits : List<Byte>) : NumberBuilder {
        var nb = this
        for( d in digits ) nb = nb.appendDigit(nb.base, d)
        return nb
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

    override fun render(displayPrefs: DisplayAndComputePreferences): String {
        if( displayPrefs.base != this.base ) {
            return this.convertBaseTo(displayPrefs.base, displayPrefs).render(displayPrefs)
        } else {
            val length = digits.size
            val realString = when (numberEntryState) {
                NumberEntryState.BEFORE_POINT ->
                    NumberRendering.render(
                        isNegative,
                        base,
                        length,
                        lengthAfterPoint,
                        { getDigit(it) },
                        false,
                        displayPrefs
                    )

                NumberEntryState.AFTER_POINT ->
                    NumberRendering.render(
                        isNegative,
                        base,
                        length,
                        lengthAfterPoint,
                        { getDigit(it) },
                        true,
                        displayPrefs
                    )

                NumberEntryState.EXPONENT -> {
                    val mantissa = NumberRendering.render(
                        isNegative,
                        base,
                        length,
                        lengthAfterPoint,
                        { getDigit(it) },
                        true,
                        displayPrefs
                    )
                    val sign = if (exponentSign < 0) "-" else ""
                    val exp = exponent.toString(10)
                    mantissa + "e" + sign + exp
                }
            }
            if( isImaginary ) return realString + "i" else return realString
        }
    }

    fun imaginary() : NumberBuilder
        = copy( isImaginary = ! isImaginary)

    fun toFlexNumber() : NormalFlexNumber
        = NormalFlexNumber.create(isNegative, base, lengthAfterPoint, digits, exponent*exponentSign)

    override fun toFormula(): Formula {
        // This always makes a FlexNumber.  We might want to make other kinds of numbers
        // depending on the mode.
        val a = NormalFlexNumber.create(isNegative, base, lengthAfterPoint, digits, exponent*exponentSign)
        val b = NormalFlexNumber.mkZero(base)
        val value = if(isImaginary) ComplexNumberValue(b,a) else ComplexNumberValue(a, b)
        return ValueFormula(value)
    }

    override fun eval(
        prefs : DisplayAndComputePreferences,
        env: Environment,
        emitError: (String) -> Unit
    ): TopItem = this



    private fun copy(numberEntryState : NumberEntryState = this.numberEntryState,
                     isNegative: Boolean = this.isNegative,
                     base : Int = this.base,
                     lengthAfterPoint : Int = this.lengthAfterPoint,
                     digits : List<Byte> = this. digits,
                     exponent : Int = this.exponent,
                     exponentSign : Int = this.exponentSign,
                     isImaginary : Boolean = this.isImaginary
    ) = NumberBuilder(
        numberEntryState,
        isNegative,
        base,
        lengthAfterPoint,
        digits,
        exponent,
        exponentSign,
        isImaginary)



    override fun toString(): String {
        return "NumberBuilder()"
    }
}
