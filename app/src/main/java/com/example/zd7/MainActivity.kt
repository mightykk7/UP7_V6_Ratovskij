package com.example.zd7

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        DatabaseHelper(this).seedDatabaseIfNeeded()

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Устанавливаем постоянный заголовок
        supportActionBar?.title = "Колледж ZD7"

        setupNavigation()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        val navController = navHostFragment.navController

        val bottomNavView: BottomNavigationView = findViewById(R.id.bottom_nav_view)
        bottomNavView.setupWithNavController(navController)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.studentsFragment,
                R.id.teachersFragment,
                R.id.specialtiesFragment,
                R.id.profileFragment
            )
        )
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration)

        setupMenuVisibility(bottomNavView)

        bottomNavView.setOnItemSelectedListener { item ->
            NavigationUI.onNavDestinationSelected(item, navController)
            true
        }
    }

    private fun setupMenuVisibility(bottomNavView: BottomNavigationView) {
        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""
        val menu = bottomNavView.menu

        when (currentUserRole) {
            "Студент" -> {
                menu.findItem(R.id.studentsFragment).isVisible = false
                menu.findItem(R.id.teachersFragment).isVisible = false
                menu.findItem(R.id.specialtiesFragment).isVisible = false
            }
            "Преподаватель" -> {
                menu.findItem(R.id.specialtiesFragment).isVisible = false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}