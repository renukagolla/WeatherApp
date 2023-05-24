package com.jpmc.weatherapp.repository

import com.jpmc.weatherapp.model.WeatherResponse
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

//We are doing Network request on a server
//suspend needs to be called in coroutines scope
interface MainRepository {
    @GET("weather?")
    suspend fun getWeatherByCity(
        @Query("q") city: String,
        @Query("appid") apiKey: String
    ): WeatherResponse

    @GET("weather?")
    suspend fun getWeatherByLatLong(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String
    ): WeatherResponse
}

class MainRepositoryImpl @Inject constructor(val apiService: MainRepository) {
    suspend fun getWeatherByCity(city: String, apiKey: String) = apiService.getWeatherByCity(city, apiKey)

    suspend fun getWeatherByLatLong(lat: Double, lon: Double, apiKey: String) = apiService.getWeatherByLatLong(lat, lon, apiKey)
}

//now after this, we have not reached to the server yet, so for this we need to do the network rewuest, with the help of
//Retrofit Builder --> we need to provide the dependency in module with the help of Dagger Hilt
