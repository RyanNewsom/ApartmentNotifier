package repository.source.retrofit

import retrofit2.http.GET
import com.google.firebase.database.core.Repo
import retrofit2.Call


interface GetUnitsService {
    @GET("sightmaps/1851")
    fun listRepos(): Call<LumaApartmentsResponse>
}