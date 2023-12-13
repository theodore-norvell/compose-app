package model.data.arithmetic

data class AComplexNumber (
    val realPart : ANumber,
    val imaginaryPart : ANumber )
{
    fun render(): String {
        // TODO, accommodate display preferences
        val groupLengthBefore : Int = 3
        val groupLengthAfter : Int = 3
        val separatorBefore : Char = ','
        val separatorAfter : Char = ' '
        val radixPoint : Char = '.'
        val rootMinus1 = "i"
        if( imaginaryPart.isZero() ) {
            return realPart.render(groupLengthBefore, groupLengthAfter, separatorBefore, separatorAfter, radixPoint)
        } else if( realPart.isZero()) {
            return imaginaryPart.render(groupLengthBefore, groupLengthAfter, separatorBefore, separatorAfter, radixPoint) + " " + rootMinus1
        } else {
            return "($realPart.render(groupLengthBefore, groupLengthAfter, separatorBefore, separatorAfter, radixPoint)) + " +
                    "$imaginaryPart.render(groupLengthBefore, groupLengthAfter, separatorBefore, separatorAfter, radixPoint)) $rootMinus1)"
        }
    }

    fun isClosed() : Boolean = realPart.isClosed()
    fun canAppendDigit(base : Int, digit: Byte): Boolean = realPart.canAppendDigit( base, digit )
    fun appendDigit(base: Int, digit: Byte): AComplexNumber = copy( realPart = realPart.appendDigit(base, digit) )
    fun close(): AComplexNumber = copy( realPart = realPart.close(), imaginaryPart = imaginaryPart.close())
    fun appendPoint(): AComplexNumber = copy( realPart = realPart.appendPoint() )
    fun negate(): AComplexNumber = copy( realPart = realPart.negate(), imaginaryPart = imaginaryPart.negate())

    companion object {
        fun openZero(base : Int): AComplexNumber = AComplexNumber( FlexNumber.openZero(base), FlexNumber.closedZero(base))
    }
}

enum class NumberEntryState { CLOSED, BEFORE_POINT, AFTER_POINT, EXPONENT }

abstract class ANumber protected constructor(
                        val numberEntryState : NumberEntryState,
                        val isNegative : Boolean,
                       val base : Int,
                       val length : Int,
                       val precision : Int,
                       val digits : List<Byte>) {
    init{
        check( base > 1)
        check( base < 36 )
        check( numberEntryState != NumberEntryState.BEFORE_POINT || precision == 0)
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

    fun isZero() : Boolean = digits.isEmpty()

    fun render() = render(3, 3,',', ' ', '.')
    private fun separate(str: String, finalBuilder: StringBuilder, groupLength : Int, separator: Char, excessAtStart : Boolean) {
        val wholeGroups = str.length / groupLength
        val rem = str.length - groupLength*wholeGroups
        var i = 0

        if(excessAtStart)
                (0..< rem).forEach{
                    finalBuilder.append( str[it]) ; ++i }
        var skipNextSeparator = i==0
        (0 ..< wholeGroups ).forEach {
            if( ! skipNextSeparator ) {finalBuilder.append(separator) }
            skipNextSeparator = false
            (i ..< (i+groupLength)).forEach { finalBuilder.append(str[it]) }
            i += groupLength
        }
        if( i < str.length ) {
            // There are left overs
            check( !excessAtStart )
            if( i > 0) finalBuilder.append(separator)
            (i..< str.length).forEach{
                finalBuilder.append( str[it]) ; ++i } }
    }

    fun render(groupLengthBefore : Int, groupLengthAfter : Int, separatorBefore : Char, separatorAfter : Char, radixPoint : Char  ) : String {
        fun toChar( digit : Byte ) : Char {
            return if(digit < 10) '0'+ digit.toInt() else 'A' + (digit.toInt() - 10)
        }
        val finalBuilder = StringBuilder()
        if( isNegative) finalBuilder.append("-")
        // Digits before the dot
        run {
            val b = StringBuilder()
            if( length > precision) {
                var k = length-precision-1
                (precision..<length).forEach { b.append( getDigit(k) ) ; --k }
            } else {
                b.append('0')
            }
            // Transfer to the final builder in groups
            separate( b.toString(), finalBuilder, groupLengthBefore, separatorBefore, true)
        }
        // Radix point
        if( numberEntryState != NumberEntryState.BEFORE_POINT ) {
            finalBuilder.append( radixPoint )
            run {
                val a = StringBuilder()
                (1 .. precision).forEach { a.append( getDigit(-it) ) }
                separate( a.toString(), finalBuilder, groupLengthAfter, separatorAfter, false)
            }
        }
        return finalBuilder.toString()
    }

    override fun equals(other: Any?): Boolean {
        // This is not numerical equality, as it includes
        // precision, length, and pointedNess
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other is FlexNumber) {
            return  other.numberEntryState == numberEntryState
                    && other.isNegative == isNegative
                    && other.base == base
                    && other.length == length
                    && other.precision == precision
                    && other.digits == digits
        } else {
            return false
        }
    }
    override fun hashCode(): Int {
        val a = if(isNegative) 13 else 11
        val b = base
        val c = length
        val d = when( numberEntryState ) {
            NumberEntryState.CLOSED -> 23
            NumberEntryState.BEFORE_POINT -> 29
            NumberEntryState.AFTER_POINT -> 31
            NumberEntryState.EXPONENT -> 33
        }
        val e = precision
        val f = digits.hashCode()
        return a + 257*(b + 257*(c + 257*(d + 257*(e + 257*f))))
    }



    abstract fun canAppendDigit(base: Int, digit: Byte): Boolean

    abstract fun appendDigit(base: Int, digit: Byte): FlexNumber

    abstract fun appendPoint(): FlexNumber

    abstract fun negate() : FlexNumber
    abstract fun isClosed(): Boolean
    abstract fun close(): ANumber
}

class FlexNumber
    private constructor (
                numberEntryState : NumberEntryState,
                isNegative : Boolean,
                base : Int,
                length : Int,
                precision : Int,
                digitsInput : List<Byte>)
    : ANumber( numberEntryState, isNegative, base, length, precision, digitsInput )
{
    companion object {
        // Factory used so that Flex numbers are guaranteed normalized.
        fun create( numberEntryState : NumberEntryState,
                    isNegative : Boolean,
                    base : Int,
                    length : Int,
                    precision : Int,
                    digits : List<Byte>) : FlexNumber  {
            check( base > 1)
            check( base < 36 )
            check( numberEntryState != NumberEntryState.BEFORE_POINT || precision == 0)
            val lastNonZero = 1 + (digits.indexOfLast { it.toInt() != 0 } ?: -1)
            val newDigits = if( lastNonZero == digits.size ) digits else digits.take( lastNonZero )
            val newIsNegative = if(newDigits.isEmpty()) false else isNegative
            return FlexNumber(  numberEntryState,
                                newIsNegative,
                                base,
                                length,
                                precision,
                                newDigits )
        }

        fun openZero(base: Int): ANumber = create( NumberEntryState.BEFORE_POINT, false, base, 0,0, emptyList())

        fun closedZero(base: Int): ANumber = create( NumberEntryState.CLOSED, false, base, 0,0, emptyList())
    }

    override fun canAppendDigit(base: Int, digit: Byte): Boolean {
        return when( numberEntryState ) {
            NumberEntryState.BEFORE_POINT, NumberEntryState.AFTER_POINT, NumberEntryState.EXPONENT  ->
                base == this.base && 0 <= digit && digit < base
            NumberEntryState.CLOSED -> false
        }
    }

    override fun appendDigit(base: Int, digit: Byte): FlexNumber {
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

    override fun appendPoint(): FlexNumber {
        return when( numberEntryState ) {
            NumberEntryState.CLOSED -> this
            NumberEntryState.BEFORE_POINT -> copy( numberEntryState = NumberEntryState.AFTER_POINT )
            NumberEntryState.AFTER_POINT -> this
            NumberEntryState.EXPONENT -> TODO()
        }
    }

    override fun negate() : FlexNumber {
        return copy( isNegative = !isNegative )
    }

    override fun isClosed(): Boolean =
        numberEntryState == NumberEntryState.CLOSED

    override fun close(): ANumber =
        copy( numberEntryState = NumberEntryState.CLOSED )

    private fun copy( numberEntryState : NumberEntryState = this.numberEntryState,
                      isNegative: Boolean = this.isNegative,
                     base : Int = this.base,
                     length : Int = this.length,
                     precision : Int = this.precision,
                     digits : List<Byte> = this. digits
    ) = FlexNumber.create(
            numberEntryState,
            isNegative,
            base,
            length,
            precision,
            digits)



    override fun toString(): String {
        return "FlexNumber()"
    }
}
