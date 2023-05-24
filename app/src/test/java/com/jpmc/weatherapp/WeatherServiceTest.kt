package com.jpmc.weatherapp

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.jpmc.weatherapp.repository.MainRepository
import com.jpmc.weatherapp.repository.MainRepositoryImpl
import com.jpmc.weatherapp.utils.MockResponseFileReader
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class WeatherServiceTestTest {
    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()
    private val server = MockWebServer()
    private lateinit var repository: MainRepositoryImpl
    private lateinit var mockedResponse: String
    private val gson = GsonBuilder()
        .setLenient()
        .create()

    @Before
    fun init() {
        server.start(8000)
        val BASE_URL = server.url("/").toString()
        val okHttpClient = OkHttpClient
            .Builder()
            .build()
        val service = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build().create(MainRepository::class.java)
        repository = MainRepositoryImpl(service)
    }

    @Test
    fun testApiSuccess() {
        mockedResponse = MockResponseFileReader("successResponse/weather_api.json").content
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )
        val response = runBlocking {
            repository.apiService.getWeatherByCity(
                "Hyderabad",
                "65d00499677e59496ca2f318eb68c049"
            )
        }
        val json = gson.toJson(response)
        val resultResponse = JsonParser.parseString(json)
        val expectedresponse = JsonParser.parseString(mockedResponse)
        Assert.assertNotNull(response)
        Assert.assertTrue(resultResponse.equals(expectedresponse))
    }

    @Test
    fun testApiFailure() {
        mockedResponse = MockResponseFileReader("successResponse/weather_failureapi.json").content
        server.enqueue(
            MockResponse()
                .setResponseCode(200)
                .setBody(mockedResponse)
        )
        val response = runBlocking {
            repository.apiService.getWeatherByCity(
                "",
                ""
            )
        }
        val json = gson.toJson(response)
        val resultResponse = JsonParser.parseString(json)
        val expectedResponse = JsonParser.parseString(mockedResponse)
        Assert.assertTrue(resultResponse.equals(expectedResponse))
    }

    @After
    fun tearDown() {
        server.shutdown()
    }
}