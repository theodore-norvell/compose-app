package model.data.arithmetic

data class AComplexNumber (val realPart : ANumber, val imaginaryPart : ANumber )

abstract class ANumber protected constructor(
                        val isNegative : Boolean,
                       val base : Int,
                       val length : Int,
                       val isPointed : Boolean,
                       val precision : Int,
                       val digits : List<Byte>) {
    init{
        check( base > 1)
        check( base < 36 )
        check( precision == 0 || isPointed)
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

    fun render() = render(3, 3,',', ' ', '.')
    private fun separate(str: String, finalBuilder: StringBuilder, groupLength : Int, separator: Char) {
        val wholeGroups = str.length / groupLength
        var i = str.length - groupLength*wholeGroups

        (0..< i).forEach{ finalBuilder.append( str[it]) }
        var atStart = i==0
        (0 ..< wholeGroups ).forEach {
            if( ! atStart ) {finalBuilder.append(separator); atStart = false }
            (i ..< (i+groupLength)).forEach { finalBuilder.append(str[it]) }
            i += groupLength
        }
        check( i == str.length )
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
            if( digits.size > precision) {
                digits.drop(precision).forEach { b.append(toChar(it)) }
            } else if(digits.size == precision ) {
                b.append('0')
            } else { // digits.size < precision
                (1..precision - digits.size).forEach { b.append('0') }
            }
            // Transfer to the final builder in groups
            separate( b.toString(), finalBuilder, groupLengthBefore, separatorBefore)
        }
        // Radix point
        if( isPointed ) {
            finalBuilder.append( radixPoint )
            run {
                val a = StringBuilder()
                (0 ..< precision).forEach { a.append( if( it> digits.size) '0' else toChar(digits[it]) ) }
                separate( a.toString(), finalBuilder, groupLengthAfter, separatorAfter)
            }
        }
        return finalBuilder.toString()
    }

    override fun hashCode(): Int {
        val a = if(isNegative) 13 else 11
        val b = base
        val c = length
        val d = if(isNegative) 17 else 19
        val e = precision
        val f = digits.hashCode()
        return a + 257*(b + 257*(c + 257*(d + 257*(e + 257*f))))
    }
}

class FlexNumber
    private constructor (
                isNegative : Boolean,
                 base : Int,
                 length : Int,
                 isPointed : Boolean,
                 precision : Int,
                 digitsInput : List<Byte>)
    : ANumber( isNegative, base, length, isPointed, precision, digitsInput ) {

    companion object {
        // Factory used so that Flex numbers are guaranteed normalized.
        fun create( isNegative : Boolean,
                         base : Int,
                         length : Int,
                         isPointed : Boolean,
                         precision : Int,
                         digits : List<Byte>) : FlexNumber  {
            check( base > 1)
            check( base < 36 )
            check( precision == 0 || isPointed)
            val lastNonZero = 1 + (digits.findLast { it.toInt() != 0 } ?: -1)
            val newDigits = if( lastNonZero == digits.size ) digits else digits.take( lastNonZero )
            val newIsNegative = if(newDigits.isEmpty()) false else isNegative
            return FlexNumber( newIsNegative,
                                base,
                                length,
                                isPointed,
                                precision,
                                newDigits )
        }
    }

    fun canAppend(base: Int, digit: Byte): Boolean {
        return base == this.base && 0 <= digit && digit < base
    }

    fun append(base: Int, digit: Byte): FlexNumber {
        // Note that appending a digit to the number means prepending the digit
        // to our digit list.
        check(canAppend(base, digit)) { "FlexNumber: bad append" }
        val newDigits = listOf(digit) + digits
        val newPrecision = if (isPointed) precision + 1 else precision
        return FlexNumber(isNegative, base, length + 1, isPointed, newPrecision, newDigits)
    }

    fun addPoint(): FlexNumber {
        return copy( precision = 0, isPointed = true )
    }

    fun negate() : FlexNumber {
        return copy( isNegative = !isNegative )
    }

    private fun copy(isNegative : Boolean = this.isNegative,
                     base : Int = this.base,
                     length : Int = this.length,
                     isPointed : Boolean = this.isPointed,
                     precision : Int = this.precision,
                     digitsInput : List<Byte> = this. digits
    ) = FlexNumber.create(isNegative,
            base,
            length,
            isPointed,
            precision,
            digitsInput)

    override fun equals(other: Any?): Boolean {
        // This is not numerical equality, as it includes
        // precision, length, and pointedNess
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        if (other is FlexNumber) {
            return other.isNegative == isNegative
                    && other.base == base
                    && other.length == length
                    && other.isPointed == isPointed
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
