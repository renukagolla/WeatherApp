package com.jpmc.weatherapp.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.jpmc.weatherapp.R
import com.jpmc.weatherapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navController = supportFragmentManager.getNavController()
    }

    private fun FragmentManager.getNavController(): NavController {
        return (findFragmentById(R.id.fragmentContainerView) as NavHostFragment).navController
    }

    override fun onNavigateUp(): Boolean {
        return navController.navigateUp()
    }
}