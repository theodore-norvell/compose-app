package model.data.value

object NumberRendering {
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

    fun render(
        isNegative : Boolean,
        base : Int,
        length : Int,
        precision : Int,
        getDigit: (Int)->Byte
    ) = render(isNegative, base, length, precision, getDigit, 3, 3,',', ' ', '.')

    private fun render(
        isNegative : Boolean,
        base : Int,
        length : Int,
        precision : Int,
        getDigit: (Int)->Byte,
        groupLengthBefore : Int,
        groupLengthAfter : Int,
        separatorBefore : Char,
        separatorAfter : Char,
        radixPoint : Char  ) : String {
        fun toChar( digit : Byte ) : Char {
            check(digit in 0..<base)
            return if(digit < 10) '0'+ digit.toInt() else 'A' + (digit.toInt() - 10)
        }
        val finalBuilder = StringBuilder()
        if( isNegative) finalBuilder.append("-")
        // Digits before the dot
        run {
            val b = StringBuilder()
            if( length > precision) {
                var k = length-precision-1
                (precision..<length).forEach { b.append( toChar(getDigit(k) )) ; --k }
            } else {
                b.append('0')
            }
            // Transfer to the final builder in groups
            separate( b.toString(), finalBuilder, groupLengthBefore, separatorBefore, true)
        }
        // Radix point

        finalBuilder.append( radixPoint )
        run {
            val a = StringBuilder()
            (1 .. precision).forEach { a.append( getDigit(-it) ) }
            separate( a.toString(), finalBuilder, groupLengthAfter, separatorAfter, false)
        }

        return finalBuilder.toString()
    }
}