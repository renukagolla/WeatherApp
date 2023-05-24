package com.jpmc.weatherapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.gson.Gson
import com.jpmc.weatherapp.BuildConfig
import com.jpmc.weatherapp.model.WeatherResponse
import com.jpmc.weatherapp.repository.MainRepository
import com.jpmc.weatherapp.repository.MainRepositoryImpl
import com.jpmc.weatherapp.utils.MockResponseFileReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations

@ExperimentalCoroutinesApi
@RunWith(JUnit4::class)
class WeatherServiceTestTest {
    private val testDispatcher = TestCoroutineDispatcher()
    @Mock
    lateinit var apiService: MainRepository

    @get:Rule
    val instantTaskExecutionRule: InstantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var mainRepository: MainRepositoryImpl
    private lateinit var viewModel: WeatherViewModel


    @Before
    fun setup() {
        MockitoAnnotations.initMocks(this)
        Dispatchers.setMain(testDispatcher)
        mainRepository = MainRepositoryImpl(apiService)
        viewModel = WeatherViewModel(mainRepository)
    }

    @Test
    fun testGetWeatherByCitySuccess() {
        runBlocking {
            Mockito.`when`(mainRepository.getWeatherByCity("Hyderabad", BuildConfig.KEY)).thenReturn(testModel)
            viewModel.getWeatherByCity("Hyderabad")
            val result = viewModel.uiEvent.value
            assertEquals(GetWeatherResponse.ResponseSuccess(testModel), result)
        }
    }

    @Test
    fun testGetWeatherByCityFailure() {
        runBlocking {
            Mockito.`when`(mainRepository.getWeatherByCity("Hyderabad", BuildConfig.KEY)).thenReturn(failTestModel)
            viewModel.getWeatherByCity("ha")
            val result = viewModel.uiEvent.value
            assertEquals(GetWeatherResponse.ResponseFailure, result)
        }
    }

    @Test
    fun testGetWeatherByLatLonSuccess() {
        runBlocking {
            Mockito.`when`(mainRepository.getWeatherByLatLong(17.3753,78.4744, BuildConfig.KEY)).thenReturn(testModel)
            viewModel.getWeatherByLatLon(17.3753, 78.4744)
            val result = viewModel.uiEvent.value
            assertEquals(GetWeatherResponse.ResponseSuccess(testModel), result)
        }
    }

    @Test
    fun testGetWeatherByLatLonFailure() {
        runBlocking {
            Mockito.`when`(mainRepository.getWeatherByLatLong(17.3753,78.4744, BuildConfig.KEY)).thenReturn(failTestModel)
            viewModel.getWeatherByLatLon(0.0, 0.0)
            val result = viewModel.uiEvent.value
            assertEquals(GetWeatherResponse.ResponseFailure, result)
        }
    }

    companion object {
        private val gson = Gson()
        private val jsonString = MockResponseFileReader("successResponse/weather_api.json").content
        var testModel: WeatherResponse = gson.fromJson(jsonString, WeatherResponse::class.java)

        private val failJsonString = MockResponseFileReader("successResponse/weather_failureapi.json").content
        var failTestModel: WeatherResponse = gson.fromJson(failJsonString, WeatherResponse::class.java)
    }
}