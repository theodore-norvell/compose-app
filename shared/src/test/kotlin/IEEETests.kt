
import model.data.DisplayAndComputePreferences
import model.data.formula.NumberBuilder
import model.data.value.ComplexNumberValue
import model.data.value.FlexNumber
import model.data.value.IEEENumber
import org.junit.Assert.assertEquals
import org.junit.Test
class IEEETests {

    val prefs = DisplayAndComputePreferences().copy( maxDigits = 15, maxLengthAfterPoint = 15 )


    @Test
    fun testIEEEAsFlexNumber() {
        var ieee = IEEENumber( 41.0 / 32.0 )
        var actual = ieee.asFlexible(prefs)
        var actualString = actual.render( prefs )
        var expectedString = "1.28125"
        assertEquals(expectedString, actualString )

        val twoPow10 = 1024.0
        val twoPow20 = twoPow10 * twoPow10
        val twoPow30 = twoPow20 * twoPow10
        val twoPow40 = twoPow30 * twoPow10
        val twoPow50 = twoPow40 * twoPow10

        ieee = IEEENumber( 41.0 / twoPow10 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "0.400390625e-1"
        assertEquals(expectedString, actualString )


        ieee = IEEENumber( 41.0 / twoPow20 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "0.391006469726562e-4"
        assertEquals(expectedString, actualString )


        ieee = IEEENumber( 41.0 / twoPow30 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "0.381842255592346e-7"
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( 41.0 / twoPow40 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "37.2892827726900e-12"
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( 41.0 / twoPow50 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "36.4153152077051e-15"
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( 41.0 / twoPow50 / twoPow10 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "35.5618312575245e-18"
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( 41.0 / twoPow50 / twoPow20 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "34.7283508374263e-21"
        assertEquals(expectedString, actualString )


        ieee = IEEENumber( 41.0 / twoPow50 / twoPow30 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "33.9144051146741e-24"
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( 41.0 / twoPow50 / twoPow40 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "33.1195362447989e-27"  //  Should round up
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( 41.0 / twoPow50 / twoPow50 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "32.3432971140614e-30"  // Should round up
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( twoPow40*256.0 - 1.0 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "281.474976710655e12"
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( twoPow40*512.0 - 1.0 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "562.949953421311e12"
        assertEquals(expectedString, actualString )

        ieee = IEEENumber( twoPow50 - 1.0 )
        actual = ieee.asFlexible(prefs)
        actualString = actual.render( prefs )
        expectedString = "1.12589990684262e15"
        assertEquals(expectedString, actualString )
    }
}