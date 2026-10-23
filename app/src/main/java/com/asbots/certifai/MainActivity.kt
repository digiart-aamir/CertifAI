package com.asbots.certifai

import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.asbots.certifai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        window.statusBarColor = Color.TRANSPARENT
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupHeaderActions()
        setupStaticTopNav()
        setupNavigationDrawer()
    }

    private fun setupHeaderActions() {
        binding.ivMenu.setOnClickListener {
            openDrawer()
        }
        binding.ivSearch.setOnClickListener {
            navController.navigate(R.id.exploreFragment)
        }
        binding.ivProfile.setOnClickListener {
            navController.navigate(R.id.profileFragment)
        }
    }

    private fun setupStaticTopNav() {
        val topNav = binding.includeTopNav
        val tabs = listOf(
            topNav.tabHome to R.id.homeFragment,
            topNav.tabLearn to R.id.learnFragment,
            topNav.tabExplore to R.id.exploreFragment,
            topNav.tabCertificates to R.id.certificatesFragment,
            topNav.tabProfile to R.id.profileFragment
        )

        tabs.forEach { (tabView, destId) ->
            tabView.setOnClickListener { _ ->
                try {
                    val currentDestId = navController.currentDestination?.id
                    if (currentDestId != destId) {
                        navController.navigate(destId)
                    }
                } catch (e: Exception) {
                    try {
                        navController.navigate(destId)
                    } catch (ex: Exception) {
                        // Fallback catch
                    }
                }
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.learnFragment, R.id.exploreFragment,
                R.id.certificatesFragment, R.id.profileFragment -> {
                    binding.llTopHeader.visibility = View.VISIBLE
                    topNav.llTopNavTabs.visibility = View.VISIBLE
                    // Highlight active tab
                    tabs.forEach { (t, destId) ->
                        val isActive = destId == destination.id
                        t.setBackgroundResource(if (isActive) R.drawable.bg_primary_button else android.R.color.transparent)
                        t.setTextColor(
                            resources.getColor(
                                if (isActive) android.R.color.white else R.color.text_secondary_light,
                                null
                            )
                        )
                    }
                }
                else -> {
                    binding.llTopHeader.visibility = View.GONE
                    topNav.llTopNavTabs.visibility = View.GONE
                }
            }
        }
    }

    private fun setupNavigationDrawer() {
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.homeFragment)
                R.id.nav_pathways -> navController.navigate(R.id.learnFragment)
                R.id.nav_certificates -> navController.navigate(R.id.certificatesFragment)
                R.id.nav_settings -> navController.navigate(R.id.profileFragment)
            }
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            true
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
