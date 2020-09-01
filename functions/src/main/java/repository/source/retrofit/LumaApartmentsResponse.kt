package repository.source.retrofit

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LumaApartmentsResponse(
        val data: LumaApartmentsData
)