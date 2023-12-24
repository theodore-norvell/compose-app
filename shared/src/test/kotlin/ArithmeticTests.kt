
import model.data.formula.NumberBuilder
import model.data.value.FlexNumber
import org.junit.Assert.assertEquals
import org.junit.Test
class ArithmeticTests {
    fun <T>iterate( foo : T, fs : List<(T)->T> ) : T {
        var foobar = foo
        for( f in fs ) foobar = f(foobar)
        return foobar
    }

    fun NumberBuilder.appendDigits( digits : List<Byte>) : NumberBuilder {
        var nb = this
        for( d in digits ) nb = nb.appendDigit(nb.base, d)
        return nb
    }


    private fun toValue(base: Int, n: Int) : FlexNumber {
        var todo = if( n < 0 ) -n else n
        val digitList = MutableList<Byte>(0){0}
        while (todo != 0) {
            digitList += (todo % base).toByte()
            todo = todo / base
        }
        val result = NumberBuilder.openZero(base).appendDigits( digitList.reversed() ).toValue()
        return if( n < 0) result.negated() else result
    }

    private fun divideCheck(base : Int, dividend : Int, divisor : Int ) {
        // Check with integer quotient and remainder.
        val dividendValue = toValue(base, dividend)
        val (quotient, remainder) = dividendValue.dividedBy( divisor, dividendValue.exponent, true)
        val expectedValue = toValue( base,  if( dividend < 0 ) -( (-dividend)/divisor) else (dividend/divisor))
        val expectedRemainder = if( dividend < 0 ) ((-dividend)%divisor) else (dividend%divisor)
        assertEquals( expectedValue, quotient )
        assertEquals( expectedRemainder, remainder)
    }

    @Test fun divideWithRemainder() {
        divideCheck( 10, 3210, 7)
        divideCheck( 2, 3210, 7)
        divideCheck( 7, 3210, 7)
        divideCheck( 10, 12345, 1)
        divideCheck( 10, 12345, 6)
        divideCheck( 10, 12345, 16)
        divideCheck( 16, 12345, 1)
        divideCheck( 16, 12345, 6)
        divideCheck( 13, 12345, 16)
        divideCheck( 13, 12345, 1)
        divideCheck( 13, 12345, 6)
        divideCheck( 13, 12345, 16)
    }

    @Test fun divideWithFraction0() {
        val oneBase10 = toValue(10, 1)
        val (quotient10, remainder10) = oneBase10.dividedBy( 7, 10, false)
        assertEquals( 0, quotient10.exponent )

        assertEquals( 0, quotient10.getDigit(0).toInt())
        assertEquals( 1, quotient10.getDigit(-1).toInt())
        assertEquals( 4, quotient10.getDigit(-2).toInt())
        assertEquals( 2, quotient10.getDigit(-3).toInt())
        assertEquals( 8, quotient10.getDigit(-4).toInt())
        assertEquals( 5, quotient10.getDigit(-5).toInt())
        assertEquals( 7, quotient10.getDigit(-6).toInt())
        assertEquals( 1, quotient10.getDigit(-7).toInt())
        assertEquals( 4, quotient10.getDigit(-8).toInt())
        assertEquals( 2, quotient10.getDigit(-9).toInt())
        assertEquals( 8, quotient10.getDigit(-10).toInt())
        assertEquals( 0, quotient10.getDigit(-11).toInt())

        assertEquals( 4, remainder10)
    }

    @Test fun divideIntegerToNonInteger() {

        val n23001 = toValue( 10, 23001)
        val (actual, remainder) = n23001.dividedBy(1000,5, false)
        val expected = NumberBuilder.openZero(10)
            .appendDigit(10, 2)
            .appendDigit(10, 3)
            .appendPoint()
            .appendDigit(10, 0)
            .appendDigit(10, 0)
            .appendDigit(10, 1)
            .toValue()

        assertEquals(expected, actual)
        assertEquals(0, remainder)
    }

    @Test fun divideWithFraction7() {
        val oneBase7 = toValue(7, 1)
        val (quotient7, remainder7) = oneBase7.dividedBy( 7, 10, false)
        assertEquals( 0, quotient7.exponent )

        assertEquals( 0, quotient7.getDigit(0).toInt())
        assertEquals( 1, quotient7.getDigit(-1).toInt())
        assertEquals( 0, quotient7.getDigit(-2).toInt())
        assertEquals( 0, quotient7.getDigit(-3).toInt())
        assertEquals( 0, quotient7.getDigit(-4).toInt())
        assertEquals( 0, quotient7.getDigit(-5).toInt())
        assertEquals( 0, quotient7.getDigit(-6).toInt())
        assertEquals( 0, quotient7.getDigit(-7).toInt())
        assertEquals( 0, quotient7.getDigit(-8).toInt())
        assertEquals( 0, quotient7.getDigit(-9).toInt())
        assertEquals( 0, quotient7.getDigit(-10).toInt())

        assertEquals( 0, remainder7)
    }

    @Test fun multiply0() {
        val thirteenBase2 = NumberBuilder.openZero(2)
            .appendDigit(2, 1)
            .appendDigit(2, 1)
            .appendDigit(2, 0)
            .appendDigit(2, 1)
            .toValue()

        val thirteenTimes11 = thirteenBase2.multipliedBy(11)

        assertEquals( 8, thirteenTimes11.exponent )

        assertEquals( 1, thirteenTimes11.getDigit(7).toInt())
        assertEquals( 0, thirteenTimes11.getDigit(6).toInt())
        assertEquals( 0, thirteenTimes11.getDigit(5).toInt())
        assertEquals( 0, thirteenTimes11.getDigit(4).toInt())
        assertEquals( 1, thirteenTimes11.getDigit(3).toInt())
        assertEquals( 1, thirteenTimes11.getDigit(2).toInt())
        assertEquals( 1, thirteenTimes11.getDigit(1).toInt())
        assertEquals( 1, thirteenTimes11.getDigit(0).toInt())
        assertEquals( 0, thirteenTimes11.getDigit(-1).toInt())

    }

    @Test fun multiply1() {
        // Calculate 2 ^ 1000 in base 2
        var acc = toValue(2, 1)
        for( i in 0 ..< 1000)
            acc = acc.multipliedBy(2)

        assertEquals( 1001, acc.exponent )
        for( i in 0 ..< 1000)
            assertEquals(0, acc.getDigit(i).toInt() )

        assertEquals( 1, acc.getDigit(1000).toInt() )
    }

    @Test fun convert0() {
        val twenty3Base10 = toValue( 10, 23)

        var actual = twenty3Base10.convertedToBase(2, 5)
        var expected = toValue( 2, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(7, 5)
        expected = toValue( 7, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(16, 5)
        expected = toValue( 16, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(10, 5)
        expected = toValue( 10, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(13, 5)
        expected = toValue( 13, 23)
        assertEquals(expected, actual)
    }

    @Test fun convert1() {
        val n23001 = toValue( 10, 23001)
        val twenty3Point0001Base10 = n23001.dividedBy(1000,5, false).first

        var actual = twenty3Point0001Base10.convertedToBase(2, 33)
        var expected = NumberBuilder.openZero(2)
            .appendDigit(2, 1)
            .appendDigit(2, 0)
            .appendDigit(2, 1)
            .appendDigit(2, 1)
            .appendDigit(2, 1)
            .appendPoint()
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 1)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 1)
            .appendDigit( 2, 1)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 1)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 1)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 1)
            .appendDigit( 2, 1)
            .toValue()
        assertEquals(expected, actual)
    }

    private fun addHelper(a: FlexNumber, b: FlexNumber, c : FlexNumber, base : Int, size : Int) {

        var x = a
        var y = b
        var expected = c.convertedToBase( base, size )
        var actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = c
        y = a.negated()
        expected = b.convertedToBase( base, size )
        actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = c
        y = b.negated()
        expected  = a.convertedToBase( base, size )
        actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = a.negated()
        y = b.negated()
        expected  = c.negated().convertedToBase( base, size )
        actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = c.negated()
        y = a
        expected  = b.negated().convertedToBase( base, size )
        actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = c.negated()
        y = b
        expected  = a.negated().convertedToBase( base, size )
        actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = a
        y = a.negated()
        expected  = FlexNumber.mkZero(base)
        actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = b
        y = b.negated()
        expected  = FlexNumber.mkZero(base)
        actual = x.add( y, base, size)
        assertEquals(expected, actual)

        x = c
        y = c.negated()
        expected  = FlexNumber.mkZero(base)
        actual = x.add( y, base, size)
        assertEquals(expected, actual)
    }

    @Test fun add0() {
        val nNeg9 = toValue( 10, 9).negated()
        val nNeg18 = toValue( 10, 18 ).negated()

        var expected = nNeg18
        var actual = nNeg9.add( nNeg9, 10, 10)
        assertEquals(expected, actual)


        val n23001 = toValue( 10, 23001)
        val n79999 = toValue( 10, 79999)
        val n103000  = toValue( 10, 103000)

        addHelper(n23001,n79999,n103000, 10, 10)

        for( a in -100..100) {
            for (b in -100..100) {
                val c = a + b
                for (base in 2..36) {
                    addHelper( toValue( base, a), toValue( base, b), toValue( base, c), base, 10)
                }
            }
        }

        for( a in -100..100) {
            for (b in -100..100) {
                val c = a + b
                for (baseA in listOf(2, 7, 8, 10, 16 )) {
                    for (baseB in listOf(2, 7, 8, 10, 16)) {
                        for (baseC in listOf(2, 7, 8, 10, 16)) {
                            for (baseOut in listOf(2, 7, 8, 10, 16)) {
                                addHelper(
                                    toValue(baseA, a),
                                    toValue(baseB, b),
                                    toValue(baseC, c),
                                    baseOut,
                                    10
                                )
                            }
                        }
                    }
                }
            }
            println( "Done a = $a")
        }
    }
}