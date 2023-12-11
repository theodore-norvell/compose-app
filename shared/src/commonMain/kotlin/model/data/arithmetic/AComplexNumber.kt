package model.data.arithmetic

data class AComplexNumber (val realPart : ANumber, val imaginaryPart : ANumber )

abstract class ANumber(val isNegative : Boolean,
                   val base : Int,
                   val length : Int,
                   val isDotted : Boolean,
                   val precision : Int,
                   digitsInput : List<Byte>) {

    val digits : List<Byte>  // The digits from LSD to MSD.  Invariant the MSD is not 0.
    init{
        check( base > 1)
        check( base < 36 )
        check( precision == 0 || isDotted)
        var lastNonZero = 1 + (digitsInput.findLast { it.toInt() != 0 } ?: -1)
        digits = if( lastNonZero == digitsInput.size ) digitsInput else digitsInput.take( lastNonZero )
        check( digits.isEmpty() || digits[digits.size-1].toInt() != 0)
        check( digits.all{ it.toInt() >= 0 } )
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
        val wholeGroups = str.length % groupLength
        var i = str.length - groupLength*wholeGroups

        (0..< i).forEach{ finalBuilder.append( str[it]) ; ++i }
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
        if( isDotted ) {
            finalBuilder.append( radixPoint )
            run {
                val a = StringBuilder()
                (0 ..< precision).forEach { a.append( if( it> digits.size) '0' else toChar(digits[it]) ) }
                separate( a.toString(), finalBuilder, groupLengthAfter, separatorAfter)
            }
        }
        return finalBuilder.toString()
    }
}

class FlexNumber(isNegative : Boolean,
                 base : Int,
                 length : Int,
                 isDotted : Boolean,
                 precision : Int,
                 digitsInput : List<Byte>)
    : ANumber( isNegative, base, length, isDotted, precision, digitsInput ) {


    fun canAppend(base: Int, digit: Byte): Boolean {
        return base == this.base && digit <= 0 && digit < base;
    }

    fun append(base: Int, digit: Byte): FlexNumber {
        // Note that appending a digit to the number means prepending the digit
        // to our digit list.
        check(canAppend(base, digit)) { "FlexNumber: bad append" }
        val newDigits = listOf(digit) + digits
        val newPrecision = if (isDotted) precision + 1 else precision
        return FlexNumber(isNegative, base, length + 1, isDotted, newPrecision, newDigits)
    }

    fun copy(isNegative : Boolean = this.isNegative,
             base : Int = this.base,
             length : Int = this.length,
             isDotted : Boolean = this.isDotted,
             precision : Int = this.precision,
             digitsInput : List<Byte> = this. digits
    ) = FlexNumber(isNegative,
            base,
            length,
            isDotted,
            precision,
            digitsInput)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false
        return true
    }

    override fun hashCode(): Int {
        return this::class.hashCode()
    }

    override fun toString(): String {
        return "FlexNumber()"
    }
}
