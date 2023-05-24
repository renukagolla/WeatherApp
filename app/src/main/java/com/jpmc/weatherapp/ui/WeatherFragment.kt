package com.jpmc.weatherapp.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.jpmc.weatherapp.R
import com.jpmc.weatherapp.databinding.FragmentWeatherBinding
import com.jpmc.weatherapp.model.WeatherResponse
import com.jpmc.weatherapp.util.*
import com.jpmc.weatherapp.viewmodel.GetWeatherResponse
import com.jpmc.weatherapp.viewmodel.WeatherViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class WeatherFragment : Fragment(), LocationListener {
    private lateinit var binding: FragmentWeatherBinding
    private val viewModel: WeatherViewModel by activityViewModels()
    private lateinit var locationManager: LocationManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_weather, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationManager =
            requireActivity().getSystemService(Context.LOCATION_SERVICE) as LocationManager
        listenForEvents()

        binding.searchCity.onQuerySubmit { text ->
            text?.let {
                if (it.length >= 2) {
                    binding.searchCity.onActionViewCollapsed()
                    putCity(requireActivity(), it)
                    viewModel.getWeatherByCity(it)
                } else {
                    Toast.makeText(context, getString(R.string.invalid_city), Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getLocation()
    }

    private fun listenForEvents() {
        with(binding) {
            viewModel.uiEvent.observe(viewLifecycleOwner) { result ->
                when (result) {
                    is GetWeatherResponse.ShowLoading -> {
                        locationLoading.visibility = View.VISIBLE
                    }
                    is GetWeatherResponse.ResponseSuccess ->
                        result.response.let {
                            locationLoading.visibility = View.GONE
                            lytLocation.visibility = View.VISIBLE
                            setData(result.response)
                        }
                    is GetWeatherResponse.ResponseFailure -> {
                        locationLoading.visibility = View.GONE
                        Toast.makeText(
                            context,
                            getString(R.string.no_data_message),
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun setData(data: WeatherResponse) {
        with(binding) {
            lytLocation.visibility = View.VISIBLE
            locationGPS = data
            tvTemperature.text = data.main.temp.kelvinToCelsius().toString()
            tvDate.text = dateConverter()
            tvSunrise.text = timeConverter((data.sys.sunrise).toLong())
            tvSunset.text = timeConverter((data.sys.sunset).toLong())
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLocation() {
        if (checkPermissions(requireActivity())) {
            if (isLocationEnabled(requireActivity())) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5f, this)
            } else {
                loadLastSearchCity()
                showLocationDialog()
            }
        } else {
            loadLastSearchCity()
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    override fun onLocationChanged(location: Location) {
        viewModel.getWeatherByLatLon(location.latitude, location.longitude)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // PERMISSION GRANTED
            Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show()
            getLocation()
        } else {
            // PERMISSION NOT GRANTED
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showLocationDialog() {
        AlertDialog.Builder(context).apply {
            setMessage(getString(R.string.enable_location))

            setPositiveButton(R.string.enable) { dialog, _ ->
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
                dialog.dismiss()
            }

            setNegativeButton(R.string.dismiss) { dialog, _ ->
                dialog.dismiss()
            }
            show()
        }
    }

    private fun loadLastSearchCity() {
        getCity(requireActivity())?.let {
            viewModel.getWeatherByCity(it)
        }
    }
}
