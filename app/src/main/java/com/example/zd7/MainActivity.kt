package com.example.zd7

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var bottomNavView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences("auth", MODE_PRIVATE)
        DatabaseHelper(this).seedDatabaseIfNeeded()

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        bottomNavView = findViewById(R.id.bottom_nav_view)

        setupNavigation()
        checkUserRole()
    }

    private fun setupNavigation() {
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.homeFragment, R.id.studentsFragment, R.id.teachersFragment),
            drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        bottomNavView.setupWithNavController(navController)

        setupMenuVisibility()
        setupNavigationListeners()
    }

    private fun setupMenuVisibility() {
        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""
        val navMenu = navView.menu
        val bottomMenu = bottomNavView.menu

        when (currentUserRole) {
            "Студент" -> {
                navMenu.findItem(R.id.studentsFragment).isVisible = false
                navMenu.findItem(R.id.teachersFragment).isVisible = false
                navMenu.findItem(R.id.groupsFragment).isVisible = false
                navMenu.findItem(R.id.specialtiesFragment).isVisible = false

                bottomMenu.findItem(R.id.studentsFragment).isVisible = false
                bottomMenu.findItem(R.id.teachersFragment).isVisible = false
                bottomMenu.findItem(R.id.specialtiesFragment).isVisible = false
            }
            "Преподаватель" -> {
                navMenu.findItem(R.id.groupsFragment).isVisible = false
                navMenu.findItem(R.id.specialtiesFragment).isVisible = false

                bottomMenu.findItem(R.id.specialtiesFragment).isVisible = false
            }
            "Приемная комиссия" -> {
            }
        }
    }

    private fun setupNavigationListeners() {
        navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_logout -> {
                    logout()
                    true
                }
                else -> {
                    NavigationUI.onNavDestinationSelected(menuItem, navController)
                    drawerLayout.closeDrawers()
                    true
                }
            }
        }

        bottomNavView.setOnItemSelectedListener { menuItem ->
            NavigationUI.onNavDestinationSelected(menuItem, navController)
            true
        }
    }

    private fun checkUserRole() {
        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""
        supportActionBar?.title = when (currentUserRole) {
            "Приемная комиссия" -> "Режим: Приемная комиссия"
            "Преподаватель" -> "Режим: Преподаватель"
            "Студент" -> "Режим: Студент"
            else -> "Колледж ZD7"
        }
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.nav_menu, menu)

        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""
        when (currentUserRole) {
            "Студент" -> {
                menu.findItem(R.id.studentsFragment).isVisible = false
                menu.findItem(R.id.teachersFragment).isVisible = false
                menu.findItem(R.id.groupsFragment).isVisible = false
                menu.findItem(R.id.specialtiesFragment).isVisible = false
            }
            "Преподаватель" -> {
                menu.findItem(R.id.groupsFragment).isVisible = false
                menu.findItem(R.id.specialtiesFragment).isVisible = false
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START)
                } else {
                    drawerLayout.openDrawer(GravityCompat.START)
                }
                true
            }
            R.id.action_logout -> {
                logout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp()
    }
}