package repository.source.retrofit

import com.squareup.moshi.JsonClass
import model.ApartmentUnit

@JsonClass(generateAdapter = true)
data class LumaApartmentsData(
    val units: List<ApartmentUnit>
)
