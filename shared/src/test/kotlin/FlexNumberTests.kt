import model.data.arithmetic.FlexNumber
import model.data.arithmetic.NumberEntryState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    }
}
