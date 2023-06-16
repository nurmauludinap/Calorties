package com.una.calorties

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.una.calorties.databinding.ActivityHomeBinding
import com.una.calorties.fragments.FavoriteFragment
import com.una.calorties.fragments.HistoryFragment
import com.una.calorties.fragments.HomeFragment
import com.una.calorties.fragments.ProfileFragment


class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    val TAG = "HomeActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment?
        val navController = navHostFragment!!.navController

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
    }
}