package repository.source.retrofit

import model.ApartmentUnit
import repository.source.ReadableDataSource
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

class HttpsSourceReadable : ReadableDataSource {
    override fun fetchData(): List<ApartmentUnit> {
        val retrofit = Retrofit.Builder()
                .baseUrl("https://sightmap.com/app/api/v1/4jlw03yrw2y/")
                .addConverterFactory(MoshiConverterFactory.create())
                .build()

        val service = retrofit.create(GetUnitsService::class.java)
        val response = service.listRepos().execute()
        return response.body()?.data?.units ?: listOf()
    }
}