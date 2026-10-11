package `in`.emergencyWbApp.essentials.data.remote



import `in`.emergencyWbApp.essentials.data.remote.dto.ConfigDto
import `in`.emergencyWbApp.essentials.data.remote.dto.PagesResponseDto
import retrofit2.http.GET

interface EmergencyApi {

    @GET("config")
    suspend fun getConfig(): ConfigDto

    @GET("pages/main")
    suspend fun getPages(): PagesResponseDto
}