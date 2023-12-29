
import model.data.DisplayAndComputePreferences
import model.data.formula.NumberBuilder
import model.data.value.ComplexNumberValue
import model.data.value.FlexNumber
import org.junit.Assert.assertEquals
import org.junit.Test
class ArithmeticTests {

    val prefs = DisplayAndComputePreferences()
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
        val result = NumberBuilder.zero(base).appendDigits( digitList.reversed() ).toFlexNumber()
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
        val expected = NumberBuilder.zero(10)
            .appendDigit(10, 2)
            .appendDigit(10, 3)
            .appendPoint()
            .appendDigit(10, 0)
            .appendDigit(10, 0)
            .appendDigit(10, 1)
            .toFlexNumber()

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

    @Test fun multiplyOneDigit() {
        val thirteenBase2 = NumberBuilder.zero(2)
            .appendDigit(2, 1)
            .appendDigit(2, 1)
            .appendDigit(2, 0)
            .appendDigit(2, 1)
            .toFlexNumber()

        val thirteenTimes11 = thirteenBase2.times(11)

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

    @Test fun multiplyOneDigit1() {
        // Calculate 2 ^ 1000 in base 2
        var acc = toValue(2, 1)
        for( i in 0 ..< 1000)
            acc = acc.times(2)

        assertEquals( 1001, acc.exponent )
        for( i in 0 ..< 1000)
            assertEquals(0, acc.getDigit(i).toInt() )

        assertEquals( 1, acc.getDigit(1000).toInt() )
    }

    @Test fun convert0() {
        val twenty3Base10 = toValue( 10, 23)

        var actual = twenty3Base10.convertedToBase(prefs.copy(base=2))
        var expected = toValue( 2, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(prefs.copy(base=7))
        expected = toValue( 7, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(prefs.copy(base=16))
        expected = toValue( 16, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(prefs.copy(base=10))
        expected = toValue( 10, 23)
        assertEquals(expected, actual)

        actual = twenty3Base10.convertedToBase(prefs.copy(base=13))
        expected = toValue( 13, 23)
        assertEquals(expected, actual)
    }

    @Test fun convert1() {
        val n23001 = toValue( 10, 23001)
        val twenty3Point0001Base10 = n23001.dividedBy(1000,5, false).first

        var actual = twenty3Point0001Base10.convertedToBase(prefs.copy(base=2))
        var expected = NumberBuilder.zero(2)
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
            .toFlexNumber()
        assertEquals(expected, actual)
    }

    private fun addHelper(a: FlexNumber, b: FlexNumber, c : FlexNumber, base : Int, size : Int) {
        val prefs = DisplayAndComputePreferences(base=base, sizeLimit=size)
        var x = a
        var y = b
        var expected = c.convertedToBase(prefs)
        var actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = c
        y = a.negated()
        expected = b.convertedToBase(prefs)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = c
        y = b.negated()
        expected  = a.convertedToBase(prefs)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = a.negated()
        y = b.negated()
        expected  = c.negated().convertedToBase(prefs)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = c.negated()
        y = a
        expected  = b.negated().convertedToBase(prefs)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = c.negated()
        y = b
        expected  = a.negated().convertedToBase(prefs)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = a
        y = a.negated()
        expected  = FlexNumber.mkZero(base)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = b
        y = b.negated()
        expected  = FlexNumber.mkZero(base)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)

        x = c
        y = c.negated()
        expected  = FlexNumber.mkZero(base)
        actual = x.plus( y, prefs)
        assertEquals(expected, actual)
    }

    @Test fun add0() {
        val nNeg9 = toValue( 10, 9).negated()
        val nNeg18 = toValue( 10, 18 ).negated()

        var expected = nNeg18
        var actual = nNeg9.plus( nNeg9, prefs)
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

    @Test fun multiply0() {
        // 7 times 8 in base 10
        var a = toValue( 10, 7 )
        var b = toValue( 10, 8 )
        var actual = a.times( b, prefs )
        var expected = toValue( 10, 56 )
        assertEquals( expected, actual )

        a = toValue( 10, 3782 )
        b = toValue( 10, 4782 )
        actual = a.times( b, prefs )
        expected = toValue( 10, 18085524 )
        assertEquals( expected, actual )

        actual = a.times( b, prefs.copy( sizeLimit = 5) )
        expected = toValue( 10, 18085000 )
        assertEquals( expected, actual )

        a = toValue( 10, 0 )
        b = toValue( 10,  0)
        actual = a.times( b, prefs )
        expected = toValue( 10,  0)
        assertEquals( expected, actual )
    }
    @Test fun multiply1() {
        // 7 times 8 in base 10
        var a = NumberBuilder.zero(10)
            .appendDigit(10, 1)
            .appendDigit(10, 9)
            .appendPoint()
            .appendDigit(10, 7)
            .appendDigit(10, 8)
            .toFlexNumber()

        var b = NumberBuilder.zero(10)
            .appendDigit(10, 2)
            .appendDigit(10, 6)
            .appendPoint()
            .appendDigit(10, 4)
            .appendDigit(10, 3)
            .toFlexNumber()

        var actual = a.times( b, prefs )
        var expected = NumberBuilder.zero(10)
            .appendDigit(10, 5)
            .appendDigit(10, 2)
            .appendDigit(10, 2)
            .appendPoint()
            .appendDigit(10, 7)
            .appendDigit(10, 8)
            .appendDigit(10, 5)
            .appendDigit(10, 4)
            .toFlexNumber()

        assertEquals( expected, actual )

        actual = FlexNumber.mkZero(10).times(a, prefs )
        expected = FlexNumber.mkZero(10)
        assertEquals( expected, actual )

        actual = b.times(FlexNumber.mkZero(10), prefs )
        expected = FlexNumber.mkZero(10)
        assertEquals( expected, actual )
    }

    @Test fun complexNumberValueMultiply0() {
        val prefs = DisplayAndComputePreferences(
            base = 10,
            maxDigits = 100,
            maxLengthAfterPoint = 20,
            groupLengthBefore = 3,
            groupLengthAfter = 3,
            separatorBefore = ',',
            separatorAfter = ' ',
            radixPoint = '.',
            sizeLimit = 255
        )
        var a = NumberBuilder.zero(10)
            .appendDigit(10, 4)
            .appendPoint()
            .appendDigit(10, 5)
            .toFlexNumber()

        var actualNumber = a.times(a, prefs)

        var expectedNumber = NumberBuilder.zero(10)
            .appendDigit(10, 2)
            .appendDigit(10, 0)
            .appendPoint()
            .appendDigit(10, 2)
            .appendDigit(10, 5)
            .toFlexNumber()

        assertEquals(expectedNumber, actualNumber)

        actualNumber = actualNumber.plus( FlexNumber.mkZero(10), prefs )

        assertEquals(expectedNumber, actualNumber)

        var aComplex = ComplexNumberValue(a, FlexNumber.mkZero(10))

        var actual = aComplex.multiply(aComplex, prefs )
        var expectedComplex = ComplexNumberValue(expectedNumber, FlexNumber.mkZero(10))

        assertEquals(expectedComplex, actual)
        val aRendered = actual?.render(prefs)

        assertEquals("20.25", aRendered)
    }

    @Test fun divide0() {
        // 186 / 3
        var a = NumberBuilder.zero(10)
            .appendDigit(10, 1)
            .appendDigit(10, 8)
            .appendDigit(10, 6)
            .toFlexNumber()

        var b = NumberBuilder.zero(10)
            .appendDigit(10, 3)
            .toFlexNumber()

        var actual = a.dividedBy( b, prefs )
        var expected = NumberBuilder.zero(10)
            .appendDigit(10, 6)
            .appendDigit(10, 2)
            .toFlexNumber()

        assertEquals( expected, actual )

        // 999 / 3
        a = NumberBuilder.zero(10)
            .appendDigit(10, 9)
            .appendDigit(10, 9)
            .appendDigit(10, 9)
            .toFlexNumber()

        b = NumberBuilder.zero(10)
            .appendDigit(10, 3)
            .toFlexNumber()

        actual = a.dividedBy( b, prefs )
        expected = NumberBuilder.zero(10)
            .appendDigit(10, 3)
            .appendDigit(10, 3)
            .appendDigit(10, 3)
            .toFlexNumber()

        assertEquals( expected, actual )



        // 5 / 2
        a = NumberBuilder.zero(10)
            .appendDigit(10, 5)
            .toFlexNumber()

        b = NumberBuilder.zero(10)
            .appendDigit(10, 2)
            .toFlexNumber()

        actual = a.dividedBy( b, prefs )
        expected = NumberBuilder.zero(10)
            .appendDigit(10, 2)
            .appendPoint()
            .appendDigit(10, 5)
            .toFlexNumber()

        assertEquals( expected, actual )

        // 200 / 7
        a = NumberBuilder.zero(10)
            .appendDigit(10, 2)
            .appendDigit(10, 0)
            .appendDigit(10, 0)
            .toFlexNumber()

        b = NumberBuilder.zero(10)
            .appendDigit(10, 7)
            .toFlexNumber()

        actual = a.dividedBy( b, prefs )
        expected = NumberBuilder.zero(10)
            .appendDigit(10, 2)
            .appendDigit(10, 8)
            .appendPoint()
            .appendDigit(10, 5)
            .appendDigit(10, 7)
            .appendDigit(10, 1)
            .appendDigit(10, 4)
            .appendDigit(10, 2)
            .appendDigit(10, 8)
            .appendDigit(10, 5)
            .appendDigit(10, 7)
            .appendDigit(10, 1)
            .appendDigit(10, 4)
            .appendDigit(10, 2)
            .appendDigit(10, 8)
            .appendDigit(10, 5)
            .appendDigit(10, 7)
            .appendDigit(10, 1)
            .appendDigit(10, 4)
            .appendDigit(10, 2)
            .appendDigit(10, 8)
            .toFlexNumber()

        assertEquals( expected, actual )


        a = toValue(7, 4)
        b = toValue(7, 55)
        val c = toValue(7, 4 * 55)
        actual = c.dividedBy(b, prefs.copy(base=7))
        assertEquals(a, actual)
    }

    @Test fun divide1() {
        // Dividing integers to get integers
        for (base in listOf<Int>(2, 7, 8, 10, 12, 16)) {
            for (aInt in 0..<1000) {
                val a = toValue(base, aInt)
                for (bInt in 1..<1000) {
                    val b = toValue(base, bInt)
                    val c = toValue(base, aInt * bInt)
                    val actual = c.dividedBy(b, prefs.copy(base=base))
                    assertEquals(a, actual)
                }
            }
        }
    }
}