package model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ApartmentUnit(
        val id: String,
        @Json(name = "unit_number")
        val unitNumber: String,
        val area: Int,
        val price: Int,
        @Json(name= "available_on")
        val availableOn: String
)