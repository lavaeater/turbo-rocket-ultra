package map.grid

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class TextGridMapDefinitionTest {

    // 3-column x 2-row map (columns = def[y][x])
    //   col 0: s e
    //   col 1: g l
    //   col 2: b w
    private val def = TextGridMapDefinition(listOf("sgb", "elw"))

    @Test fun `hasStart detects s tile`() {
        assertTrue(def.hasStart(Coordinate(0, 0)))
    }

    @Test fun `hasStart returns false for non-start tile`() {
        assertFalse(def.hasStart(Coordinate(1, 0)))
    }

    @Test fun `hasGoal detects g tile`() {
        assertTrue(def.hasGoal(Coordinate(1, 0)))
    }

    @Test fun `hasBoss detects b tile`() {
        assertTrue(def.hasBoss(Coordinate(2, 0)))
    }

    @Test fun `hasLoot detects l tile`() {
        assertTrue(def.hasLoot(Coordinate(1, 1)))
    }

    @Test fun `hasSpawner detects w tile`() {
        assertTrue(def.hasSpawner(Coordinate(2, 1)))
    }

    @Test fun `booleanSections marks e as false`() {
        val sections = def.booleanSections
        assertFalse(sections[0][1]) // 'e' at col 0, row 1
    }

    @Test fun `booleanSections marks non-e tiles as true`() {
        val sections = def.booleanSections
        assertTrue(sections[0][0]) // 's'
        assertTrue(sections[1][0]) // 'g'
    }

    @Test fun `booleanSections dimensions match def`() {
        val sections = def.booleanSections
        assertEquals(3, sections.size)           // 3 columns
        assertEquals(2, sections[0].size)        // 2 rows
    }
}
