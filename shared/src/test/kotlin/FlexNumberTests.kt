import model.data.arithmetic.FlexNumber
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class FlexNumberTests {
    @Test fun makeFlexNumber() {
        val zero = FlexNumber.create( true, 10, 10, false, 0, listOf(0,0,0) )
        assertEquals( "0", zero.render() )
        assertTrue( zero.canAppend(10, 1))
        assertTrue( zero.digits.isEmpty() )
        assertFalse( zero.isNegative )

        val negativeZero = zero.negate()
        assertEquals( "0", negativeZero.render() )
        assertTrue( negativeZero.canAppend(10, 1))
        assertTrue( negativeZero.digits.isEmpty() )
        assertFalse( negativeZero.isNegative )

        assertEquals(zero , negativeZero )
        assertEquals(zero.hashCode() , negativeZero.hashCode() )

        val one = zero.append(10,1)
        assertEquals( "1", one.render() )
        assertFalse( one.isPointed )

        val onePoint = one.addPoint()
        assertTrue( onePoint.isPointed )
        assertEquals( "1.", onePoint.render() )
        assertTrue( onePoint.canAppend(10, 1) )

        val onePointTwo = onePoint.append(10, 2)
        assertTrue( onePointTwo.isPointed )
        assertEquals( "1.2", onePointTwo.render() )

        val twin = zero.append(10, 1).addPoint().append(10, 2)

        assertEquals(onePointTwo, twin)
        assertEquals(onePointTwo.hashCode(), twin.hashCode() )
    }
}
