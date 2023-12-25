import model.data.Environment
import model.data.formula.NumberBuilder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NumberBuilderTests {
    @Test fun makeFlexNumber() {
        val env = Environment()
        val zero = NumberBuilder.openZero(10)
        assertEquals( "0", zero.render())
        assertEquals( "0.", zero.toFormula().render())


        assertTrue( zero.canAppendDigit(10, 1))
        val negativeZero = zero.negate()
        assertEquals( "-0", negativeZero.render())
        assertEquals( "0.", negativeZero.toFormula().render())

        assertTrue( negativeZero.canAppendDigit(10, 1))
        val one = zero.appendDigit(10,1)
        assertEquals( "1", one.render())
        assertEquals( "1.", one.toFormula().render())

        val onePoint = one.appendPoint()
        assertEquals( "1.", onePoint.render())
        assertEquals( "1.", onePoint.toFormula().render())

        assertTrue( onePoint.canAppendDigit(10, 1) )
        val onePointTwo = onePoint.appendDigit(10, 2)
        assertEquals( "1.2", onePointTwo.render())
        assertEquals( "1.2", onePointTwo.toFormula().render())

        val twin = zero.appendDigit(10, 1).appendPoint().appendDigit(10, 2)
        assertEquals(onePointTwo.toFormula(), twin.toFormula())

        println( "Test makeFlexNumber done" )
    }

    @Test fun makeLongerNumber() {
        val env = Environment()
        val zero = NumberBuilder.openZero(10)
        assertEquals( "0", zero.render())
        assertEquals( "-0", zero.negate().render())

        val zeroZero = zero.appendDigit(10, 0)
        assertEquals( "0", zeroZero.render())
        assertEquals( "-0", zeroZero.negate().render())

        val one2 = zeroZero
            .appendDigit(10,1)
            .appendDigit(10,2)
        assertEquals( "012", one2.render())
        assertEquals( "-012", one2.negate().render())

        val one23 = one2
            .appendDigit(10,3)
        assertEquals( "0,123", one23.render())
        assertEquals( "-0,123", one23.negate().render())

        val one2345678 = one23
            .appendDigit(10,4)
            .appendDigit(10,5)
            .appendDigit(10,6)
            .appendDigit(10,7)
            .appendDigit(10,8)
        assertEquals( "012,345,678", one2345678.render())
        assertEquals( "-012,345,678", one2345678.negate().render())

        val one2345678Point86 = one2345678
            .appendPoint()
            .appendDigit(10,8)
            .appendDigit(10,6)
        assertEquals( "012,345,678.86", one2345678Point86.render())
        assertEquals( "-012,345,678.86", one2345678Point86.negate().render())

        val one2345678Point8642 = one2345678Point86
            .appendDigit(10,4)
            .appendDigit(10,2)
        assertEquals( "012,345,678.864 2", one2345678Point8642.render())
        assertEquals( "-012,345,678.864 2", one2345678Point8642.negate().render())

        val one2345678Point864205 = one2345678Point8642
            .appendDigit(10,0)
            .appendDigit(10,5)
        assertEquals( "012,345,678.864 205", one2345678Point864205.render())
        assertEquals( "-012,345,678.864 205", one2345678Point864205.negate().render())
        println( "Test makeLongerFlexNumber done")
    }
}
