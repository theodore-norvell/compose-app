import model.data.value.FlexNumber
import model.data.value.NumberEntryState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FlexNumberTests {
    @Test fun makeFlexNumber() {
        val zero = FlexNumber.openZero(10)
        assertEquals( "0", zero.render() )
        assertTrue( zero.canAppendDigit(10, 1))
        assertTrue( zero.digits.isEmpty() )
        assertFalse( zero.isNegative )

        val negativeZero = zero.negate()
        assertEquals( "0", negativeZero.render() )
        assertTrue( negativeZero.canAppendDigit(10, 1))
        assertTrue( negativeZero.digits.isEmpty() )
        assertFalse( negativeZero.isNegative )

        assertEquals(zero , negativeZero )
        assertEquals(zero.hashCode() , negativeZero.hashCode() )

        val one = zero.appendDigit(10,1)
        assertEquals( "1", one.render() )
        assertTrue( one.numberEntryState == NumberEntryState.BEFORE_POINT )

        val onePoint = one.appendPoint()
        assertTrue( onePoint.numberEntryState == NumberEntryState.AFTER_POINT )
        assertEquals( "1.", onePoint.render() )
        assertTrue( onePoint.canAppendDigit(10, 1) )

        val onePointTwo = onePoint.appendDigit(10, 2)
        assertTrue( onePointTwo.numberEntryState == NumberEntryState.AFTER_POINT )
        assertEquals( "1.2", onePointTwo.render() )

        val twin = zero.appendDigit(10, 1).appendPoint().appendDigit(10, 2)

        assertEquals(onePointTwo, twin)
        assertEquals(onePointTwo.hashCode(), twin.hashCode() )

        println( "Test makeFlexNumber done")
    }

    @Test fun makeLongerFlexNumber() {
        val zero = FlexNumber.openZero(10)
        assertEquals( "0", zero.render() )
        assertEquals( "0", zero.negate().render() )
        assertEquals( zero, zero.negate() )

        val zeroZero = zero.appendDigit(10, 0)
        assertEquals( "0", zeroZero.render() )
        assertEquals( "0", zeroZero.negate().render() )
        assertNotEquals(zero, zeroZero)

        val one2 = zeroZero
            .appendDigit(10,1)
            .appendDigit(10,2)
        assertEquals( "012", one2.render() )
        assertEquals( "-012", one2.negate().render() )

        val one23 = one2
            .appendDigit(10,3)
        assertEquals( "0,123", one23.render() )
        assertEquals( "-0,123", one23.negate().render() )

        val one2345678 = one23
            .appendDigit(10,4)
            .appendDigit(10,5)
            .appendDigit(10,6)
            .appendDigit(10,7)
            .appendDigit(10,8)
        assertEquals( "012,345,678", one2345678.render() )
        assertEquals( "-012,345,678", one2345678.negate().render() )

        val one2345678Point86 = one2345678
            .appendPoint()
            .appendDigit(10,8)
            .appendDigit(10,6)
        assertEquals( "012,345,678.86", one2345678Point86.render() )
        assertEquals( "-012,345,678.86", one2345678Point86.negate().render() )

        val one2345678Point8642 = one2345678Point86
            .appendDigit(10,4)
            .appendDigit(10,2)
        assertEquals( "012,345,678.864 2", one2345678Point8642.render() )
        assertEquals( "-012,345,678.864 2", one2345678Point8642.negate().render() )

        val one2345678Point864205 = one2345678Point8642
            .appendDigit(10,0)
            .appendDigit(10,5)
        assertEquals( "012,345,678.864 205", one2345678Point864205.render() )
        assertEquals( "-012,345,678.864 205", one2345678Point864205.negate().render() )
        println( "Test makeLongerFlexNumber done")
    }
}
