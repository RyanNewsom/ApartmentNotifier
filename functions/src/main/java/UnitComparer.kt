import model.ApartmentUnit

interface UnitComparer {
    fun getNewUnits(oldData: Collection<ApartmentUnit>, newData: Collection<ApartmentUnit>) : Collection<ApartmentUnit>
}

class UnitComparerImpl: UnitComparer {
    override fun getNewUnits(oldData: Collection<ApartmentUnit>, newData: Collection<ApartmentUnit>): Collection<ApartmentUnit> {
        val newUnits = mutableListOf<ApartmentUnit>()

        newData.forEach { currentUnit ->
            val unitExists = oldData.find {
                it.id == currentUnit.id
            } != null
            if(!unitExists) {
                newUnits.add(currentUnit)
            }
        }

        return newUnits
    }
}