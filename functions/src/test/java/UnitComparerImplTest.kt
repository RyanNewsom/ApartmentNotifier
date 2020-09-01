import model.ApartmentUnit
import org.junit.Before
import org.junit.Test

import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UnitComparerImplTest {
    private lateinit var comparer: UnitComparer

    @Before
    fun setUp() {
        comparer = UnitComparerImpl()
    }

    @Test
    fun `Given new unit added, Then is is returned`() {
        var currentId = 0

        val oldData = listOf(
                ApartmentUnit(
                        id = "${currentId++}",
                        unitNumber = "508",
                        area = 1000,
                        price = 777,
                        availableOn = "2020-08-29"
                )
        )

        val newUnit = ApartmentUnit(
                id = "${currentId++}",
                unitNumber = "900",
                area = 1200,
                price = 777,
                availableOn = "2020-09-19"
        )
        val newData = oldData.map { it.copy() }.plus(
                listOf(
                    newUnit
                )
        )

        val newUnits = comparer.getNewUnits(oldData, newData)
        assertTrue(newUnits.first() == newUnit)
        assertEquals(1, newUnits.size)
    }

    @Test
    fun `Given no new unit is added, Then new units is empty`() {
        var currentId = 0

        val oldData = listOf(
                ApartmentUnit(
                        id = "${currentId++}",
                        unitNumber = "508",
                        area = 1000,
                        price = 777,
                        availableOn = "2020-08-29"
                )
        )

        val newData = oldData.map { it.copy() }

        val newUnits = comparer.getNewUnits(oldData, newData)
        assertTrue(newUnits.isEmpty())
    }
}