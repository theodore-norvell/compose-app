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
        lengthAfterPoint : Int,
        getDigit: (Int)->Byte,
        includeRadixPoint : Boolean = true
    ) = render(isNegative, base, length, lengthAfterPoint, getDigit, includeRadixPoint, 3, 3,',', ' ', '.')

    private fun render( isNegative : Boolean,
                        base : Int,
                        length : Int,
                        lengthAfterPoint : Int,
                        getDigit: (Int)->Byte,
                        includeRadixPoint: Boolean,
                        groupLengthBefore : Int,
                        groupLengthAfter : Int,
                        separatorBefore : Char,
                        separatorAfter : Char,
                        radixPoint : Char  )
    : String {

        fun toChar( digit : Byte ) : Char {
            check(digit in 0..<base)
            return if(digit < 10) '0'+ digit.toInt() else 'A' + (digit.toInt() - 10)
        }

        check( includeRadixPoint ||  lengthAfterPoint==0 )
        val finalBuilder = StringBuilder()
        if( isNegative) finalBuilder.append("-")
        // Digits before the dot
        run {
            val b = StringBuilder()
            if( length > lengthAfterPoint) {
                var k = length-lengthAfterPoint-1
                (lengthAfterPoint..<length).forEach { b.append( toChar(getDigit(k) )) ; --k }
            } else {
                b.append('0')
            }
            // Transfer to the final builder in groups
            separate( b.toString(), finalBuilder, groupLengthBefore, separatorBefore, true)
        }
        // Radix point
        if( includeRadixPoint) {
            finalBuilder.append(radixPoint)
                val a = StringBuilder()
                (1..lengthAfterPoint).forEach { a.append(getDigit(-it)) }
                separate(a.toString(), finalBuilder, groupLengthAfter, separatorAfter, false)
        }

        return finalBuilder.toString()
    }
}