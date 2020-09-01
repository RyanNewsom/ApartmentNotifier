package repository.source

import model.ApartmentUnit

interface ReadableDataSource {
    fun fetchData() : List<ApartmentUnit>
}