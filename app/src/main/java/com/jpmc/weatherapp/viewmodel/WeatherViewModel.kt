package com.jpmc.weatherapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jpmc.weatherapp.BuildConfig
import com.jpmc.weatherapp.model.WeatherResponse
import com.jpmc.weatherapp.repository.MainRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class GetWeatherResponse {
    object ShowLoading : GetWeatherResponse()
    object ResponseFailure : GetWeatherResponse()
    data class ResponseSuccess(val response: WeatherResponse) :
        GetWeatherResponse()
}

@HiltViewModel
class WeatherViewModel @Inject constructor(
    private val mainRepository: MainRepositoryImpl,
) :
    ViewModel() {

    private val _uiEvent = MutableLiveData<GetWeatherResponse>()
    val uiEvent: LiveData<GetWeatherResponse> = _uiEvent

    fun getWeatherByCity(cityName: String) {
        viewModelScope.launch {
            _uiEvent.postValue(GetWeatherResponse.ShowLoading)
            try {
                val forecastApiResponse =
                    mainRepository.getWeatherByCity(
                        cityName, BuildConfig.KEY
                    )
                _uiEvent.postValue(GetWeatherResponse.ResponseSuccess(forecastApiResponse))
            } catch (e: java.lang.Exception) {
                _uiEvent.postValue(GetWeatherResponse.ResponseFailure)
            }
        }
    }

    fun getWeatherByLatLon(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiEvent.postValue(GetWeatherResponse.ShowLoading)
            try {
                val forecastApiResponse =
                    mainRepository.getWeatherByLatLong(
                        lat, lon, BuildConfig.KEY
                    )
                _uiEvent.postValue(GetWeatherResponse.ResponseSuccess(forecastApiResponse))
            } catch (e: java.lang.Exception) {
                _uiEvent.postValue(GetWeatherResponse.ResponseFailure)
            }
        }
    }
}