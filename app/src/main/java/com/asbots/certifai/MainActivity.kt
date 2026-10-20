package com.asbots.certifai

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.asbots.certifai.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

        setupFloatingDock()
        setupNavigationDrawer()
    }

    private fun setupNavigationDrawer() {
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

    private fun setupFloatingDock() {
        val dock = binding.floatingDock

        dock.btnNavHome.setOnClickListener {
            navController.navigate(R.id.homeFragment)
            updateDockActiveState(dock.btnNavHome)
        }
        dock.btnNavLearn.setOnClickListener {
            navController.navigate(R.id.learnFragment)
            updateDockActiveState(dock.btnNavLearn)
        }
        dock.btnNavExplore.setOnClickListener {
            navController.navigate(R.id.exploreFragment)
            updateDockActiveState(dock.btnNavExplore)
        }
        dock.btnNavCertificates.setOnClickListener {
            navController.navigate(R.id.certificatesFragment)
            updateDockActiveState(dock.btnNavCertificates)
        }
        dock.btnNavProfile.setOnClickListener {
            navController.navigate(R.id.profileFragment)
            updateDockActiveState(dock.btnNavProfile)
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment, R.id.learnFragment, R.id.exploreFragment,
                R.id.certificatesFragment, R.id.profileFragment -> {
                    dock.llFloatingDock.visibility = View.VISIBLE
                }
                else -> {
                    dock.llFloatingDock.visibility = View.GONE
                }
            }
        }
    }

    private fun updateDockActiveState(activeButton: View) {
        val activeColor = ContextCompat.getColor(this, R.color.cyan_accent_bright)
        val inactiveColor = ContextCompat.getColor(this, R.color.text_secondary_dark)

        val dock = binding.floatingDock
        val buttons = listOf(
            dock.btnNavHome, dock.btnNavLearn, dock.btnNavExplore,
            dock.btnNavCertificates, dock.btnNavProfile
        )

        buttons.forEach { btn ->
            val tint = if (btn == activeButton) activeColor else inactiveColor
            btn.setColorFilter(tint)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
