import model.data.DisplayAndComputePreferences
import model.data.Environment
import model.data.NumberDisplayMode
import model.data.formula.NumberBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberTests {
    private val prefs = DisplayAndComputePreferences(
        base = 10,
        mode = NumberDisplayMode.Auto,
        maxDigits = 10,
        maxLengthAfterPoint = 10,
        groupLengthAfter = 3,
        groupLengthBefore = 3,
        separatorBefore = ',',
        separatorAfter = ' ',
        radixPoint = '.'
    )
    @Test fun makeFlexNumber() {
        val env = Environment()
        val zero = NumberBuilder.zero(10)
        assertEquals( "0", zero.render(prefs))
        assertEquals( "0.", zero.toFormula().render(prefs))

        assertTrue( zero.canAppendDigit(10, 1))
        val negativeZero = zero.negate()
        assertEquals( "-0", negativeZero.render(prefs))
        assertEquals( "0.", negativeZero.toFormula().render(prefs))

        assertTrue( negativeZero.canAppendDigit(10, 1))
        val one = zero.appendDigit(10,1)
        assertEquals( "1", one.render(prefs))
        assertEquals( "1.", one.toFormula().render(prefs))

        val onePoint = one.appendPoint()
        assertEquals( "1.", onePoint.render(prefs))
        assertEquals( "1.", onePoint.toFormula().render(prefs))

        assertTrue( onePoint.canAppendDigit(10, 1) )
        val onePointTwo = onePoint.appendDigit(10, 2)
        assertEquals( "1.2", onePointTwo.render(prefs))
        assertEquals( "1.2", onePointTwo.toFormula().render(prefs))

        val twin = zero.appendDigit(10, 1).appendPoint().appendDigit(10, 2)
        assertEquals(onePointTwo.toFormula(), twin.toFormula())

        println( "Test makeFlexNumber done" )
    }

    @Test fun makeLongerNumber() {
        val env = Environment()
        val zero = NumberBuilder.zero(10)
        assertEquals( "0", zero.render(prefs))
        assertEquals( "-0", zero.negate().render(prefs))

        val zeroZero = zero.appendDigit(10, 0)
        assertEquals( "0", zeroZero.render(prefs))
        assertEquals( "-0", zeroZero.negate().render(prefs))

        val one2 = zeroZero
            .appendDigit(10,1)
            .appendDigit(10,2)
        assertEquals( "012", one2.render(prefs))
        assertEquals( "-012", one2.negate().render(prefs))

        val one23 = one2
            .appendDigit(10,3)
        assertEquals( "0,123", one23.render(prefs))
        assertEquals( "-0,123", one23.negate().render(prefs))

        val one2345678 = one23
            .appendDigit(10,4)
            .appendDigit(10,5)
            .appendDigit(10,6)
            .appendDigit(10,7)
            .appendDigit(10,8)
        assertEquals( "012,345,678", one2345678.render(prefs))
        assertEquals( "-012,345,678", one2345678.negate().render(prefs))

        val one2345678Point86 = one2345678
            .appendPoint()
            .appendDigit(10,8)
            .appendDigit(10,6)
        assertEquals( "012,345,678.86", one2345678Point86.render(prefs))
        assertEquals( "-012,345,678.86", one2345678Point86.negate().render(prefs))

        val one2345678Point8642 = one2345678Point86
            .appendDigit(10,4)
            .appendDigit(10,2)
        assertEquals( "012,345,678.864 2", one2345678Point8642.render(prefs))
        assertEquals( "-012,345,678.864 2", one2345678Point8642.negate().render(prefs))

        val one2345678Point864205 = one2345678Point8642
            .appendDigit(10,0)
            .appendDigit(10,5)
        assertEquals( "012,345,678.864 205", one2345678Point864205.render(prefs))
        assertEquals( "-012,345,678.864 205", one2345678Point864205.negate().render(prefs))
        println( "Test makeLongerFlexNumber done")
    }


    @Test fun renderInVariousBases() {
        val base2Prefs = prefs.copy( base = 2, mode = NumberDisplayMode.NoExponent,
                maxDigits = 100, groupLengthBefore = 4, separatorBefore = ' ',
                groupLengthAfter = 4, separatorAfter = ' ')
        val base7Prefs = prefs.copy( base = 7, mode = NumberDisplayMode.NoExponent)
        val base8Prefs = prefs.copy( base = 8, mode = NumberDisplayMode.NoExponent)
        val base10Prefs = prefs
        val base16Prefs = prefs.copy( base = 16, mode = NumberDisplayMode.NoExponent,
            maxDigits = 25, groupLengthBefore = 2, separatorBefore = ' ',
            groupLengthAfter = 2, separatorAfter = ' ')

        // 12 base 10
        var testNumber = NumberBuilder.zero(10)
            .appendDigit(10, 1)
            .appendDigit( 10, 2)
            .toValue()
        var renderedInBase2 = testNumber.render(base2Prefs)
        var renderedInBase7 = testNumber.render(base7Prefs)
        var renderedInBase8 = testNumber.render(base8Prefs)
        var renderedInBase10 = testNumber.render(prefs)
        var renderedInBase16 = testNumber.render(base16Prefs)
        assertEquals("1100.", renderedInBase2)
        assertEquals("15.", renderedInBase7)
        assertEquals("14.", renderedInBase8)
        assertEquals("12.", renderedInBase10)
        assertEquals("C.", renderedInBase16)

        // ABCD123 base 16
        testNumber = NumberBuilder.zero(16)
            .appendDigit(16, 10)
            .appendDigit( 16, 11)
            .appendDigit( 16, 12)
            .appendDigit( 16, 13)
            .appendDigit( 16, 1)
            .appendDigit( 16, 2)
            .appendDigit( 16, 3)
            .toValue()

        renderedInBase2 = testNumber.render(base2Prefs)
        renderedInBase7 = testNumber.render(base7Prefs)
        renderedInBase8 = testNumber.render(base8Prefs)
        renderedInBase10 = testNumber.render(prefs)
        renderedInBase16 = testNumber.render(base16Prefs)
        assertEquals("1010 1011 1100 1101 0001 0010 0011.", renderedInBase2)
        assertEquals("4,315,135,234.", renderedInBase7)
        assertEquals("1,257,150,443.", renderedInBase8)
        assertEquals("180,146,467.", renderedInBase10)
        assertEquals("A BC D1 23.", renderedInBase16)

        // 11110000.101 base 2
        testNumber = NumberBuilder.zero(2)
            .appendDigit(2, 1)
            .appendDigit( 2, 1)
            .appendDigit( 2, 1)
            .appendDigit( 2, 1)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendDigit( 2, 0)
            .appendPoint()
            .appendDigit( 2, 1)
            .appendDigit( 2, 0)
            .appendDigit( 2, 1)
            .toValue()
        renderedInBase2 = testNumber.render(base2Prefs)
        renderedInBase7 = testNumber.render(base7Prefs)
        renderedInBase8 = testNumber.render(base8Prefs)
        renderedInBase10 = testNumber.render(prefs)
        renderedInBase16 = testNumber.render(base16Prefs)
        assertEquals("1111 0000.101", renderedInBase2)
        assertEquals("462.424 242 4", renderedInBase7)
        assertEquals("360.5", renderedInBase8)
        assertEquals("240.625", renderedInBase10)
        assertEquals("F0.A", renderedInBase16)
    }

    @Test fun renderNoZerosAtFront() {
        val base16Prefs = DisplayAndComputePreferences(
            base = 16,
            mode = NumberDisplayMode.NoExponent,
            maxDigits = 100,
            maxLengthAfterPoint = 20,
            groupLengthBefore = 2,
            groupLengthAfter = 2,
            separatorBefore = ' ',
            separatorAfter = ' ',
            radixPoint = '.'
        )
        val base10Prefs = DisplayAndComputePreferences(
            base = 10,
            mode = NumberDisplayMode.NoExponent,
            maxDigits = 100,
            maxLengthAfterPoint = 20,
            groupLengthBefore = 3,
            groupLengthAfter = 3,
            separatorBefore = ',',
            separatorAfter = ' ',
            radixPoint = '.'
        )
        val testNumber = NumberBuilder.zero(10)
            .appendDigit(10, 4)
            .appendDigit(10, 5)
            .appendPoint()
            .appendDigit(10, 2)
            .appendDigit(10, 3)
            .appendDigit(10, 4)
            .appendDigit(10, 5)
            .toValue()

        val renderedInBase10 = testNumber.render(base10Prefs)
        assertEquals("45.234 5", renderedInBase10)

        val renderedInBase16 = testNumber.render(base16Prefs)
        assertEquals("2D.3C 08 31 26 E9 78 D4 FD F3 B6", renderedInBase16)
    }
}
