package map.grid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class CoordinateTest {

    @Test fun `coordinates with same x and y are equal`() {
        assertEquals(Coordinate(3, 4), Coordinate(3, 4))
    }

    @Test fun `coordinates with different values are not equal`() {
        assertNotEquals(Coordinate(1, 2), Coordinate(1, 3))
    }

    @Test fun `set updates x and y and returns self`() {
        val c = Coordinate(0, 0)
        val result = c.set(5, 7)
        assertEquals(5, c.x)
        assertEquals(7, c.y)
        assertEquals(c, result)
    }

    @Test fun `toString formats as x comma y`() {
        assertEquals("3,4", Coordinate(3, 4).toString())
    }
}
