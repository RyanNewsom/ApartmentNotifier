package repository.source

import model.ApartmentUnit

interface WritableDataSource {
    fun updateApartments(apartments: List<ApartmentUnit>)
}