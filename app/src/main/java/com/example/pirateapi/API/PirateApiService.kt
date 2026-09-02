package com.example.pirateapi.API



import com.example.pirateapi.Models.Treasure
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface PirateApiService {

    @GET("booty")
    suspend fun unearthTreasure(
        @Query("passcode") passcode: String
    ): retrofit2.Response<Treasure>
}

object RetrofitClient {
    val api: PirateApiService by lazy {
        Retrofit.Builder()
            // NETWORK CLUE 3: Point the spyglass to the correct harbor!
            // Fill in the base URL string pointing to your local API endpoint (remembering the trailing slash).
            .baseUrl("http://10.115.233.240:5132/api/TreasureMap/")

            // NETWORK CLUE 4: Retrofit doesn't understand raw JSON text by default.
            // What factory do we add here to automatically convert JSON into our Treasure data class?
            // TODO: Add the Gson converter factory.
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PirateApiService::class.java)
    }
}