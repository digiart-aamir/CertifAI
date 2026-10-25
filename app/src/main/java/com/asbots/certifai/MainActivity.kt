package com.asbots.certifai

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.asbots.certifai.databinding.ActivityMainBinding
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    private var currentSelectedIndex by mutableIntStateOf(1)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                ContextCompat.getColor(this, R.color.white),
                ContextCompat.getColor(this, R.color.white)
            )
        )

        @Suppress("DEPRECATION")
        window.statusBarColor = ContextCompat.getColor(this, R.color.white)
        WindowCompat.getInsetsController(window, window.decorView).isAppearanceLightStatusBars = true

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupExyteComposeTopNav()
        setupNavigationDrawer()
    }

    private fun setupExyteComposeTopNav() {
        val composeTopNav = binding.includeTopNav.composeTopNav

        composeTopNav.setContent {
            ExyteTopBar(
                selectedIndex = currentSelectedIndex,
                onTabClick = { index ->
                    when (index) {
                        0 -> openDrawer()
                        1 -> navController.navigate(R.id.homeFragment)
                        2 -> navController.navigate(R.id.learnFragment)
                        3 -> navController.navigate(R.id.exploreFragment)
                        4 -> navController.navigate(R.id.certificatesFragment)
                        5 -> navController.navigate(R.id.profileFragment)
                    }
                }
            )
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    composeTopNav.visibility = View.VISIBLE
                    currentSelectedIndex = 1
                }
                R.id.learnFragment -> {
                    composeTopNav.visibility = View.VISIBLE
                    currentSelectedIndex = 2
                }
                R.id.exploreFragment -> {
                    composeTopNav.visibility = View.VISIBLE
                    currentSelectedIndex = 3
                }
                R.id.certificatesFragment -> {
                    composeTopNav.visibility = View.VISIBLE
                    currentSelectedIndex = 4
                }
                R.id.profileFragment -> {
                    composeTopNav.visibility = View.VISIBLE
                    // Profile screen opens without taking ball indicator state
                }
                else -> {
                    composeTopNav.visibility = View.GONE
                }
            }
        }
    }

    @Composable
    private fun ExyteTopBar(
        selectedIndex: Int,
        onTabClick: (Int) -> Unit
    ) {
        val drawableResList = listOf(
            R.drawable.ic_nav_menu,
            R.drawable.ic_nav_home,
            R.drawable.ic_nav_learn,
            R.drawable.ic_nav_explore,
            R.drawable.ic_nav_certs,
            R.drawable.ic_nav_profile
        )

        AnimatedNavigationBar(
            selectedIndex = selectedIndex,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp),
            cornerRadius = shapeCornerRadius(20.dp),
            ballAnimation = Parabolic(tween(350)),
            barColor = ComposeColor.White,
            ballColor = ComposeColor(0xFF2563EB)
        ) {
            drawableResList.forEachIndexed { index, drawableRes ->
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clickable { onTabClick(index) },
                    contentAlignment = Alignment.Center
                ) {
                    val isSelectedTab = index == selectedIndex
                    val iconTint = when {
                        index == 0 || index == 5 -> ComposeColor(0xFF64748B) // Slate Grey for Nav Drawer & Profile
                        isSelectedTab -> ComposeColor(0xFF2563EB)             // Electric Blue for selected middle tab
                        else -> ComposeColor(0xFFBCD2FA)                      // Light Blue for unselected middle tabs
                    }
                    Icon(
                        painter = painterResource(id = drawableRes),
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = iconTint
                    )
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

        // Bottom Monochrome Action Icons
        binding.ivDrawerActionLogout.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            Toast.makeText(this, "Logging out...", Toast.LENGTH_SHORT).show()
            navController.navigate(R.id.loginFragment)
        }

        binding.ivDrawerActionRefresh.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            Toast.makeText(this, "App Refreshed", Toast.LENGTH_SHORT).show()
            val currentId = navController.currentDestination?.id
            if (currentId != null) {
                navController.navigate(currentId)
            }
        }

        binding.ivDrawerActionTheme.setOnClickListener {
            Toast.makeText(this, "Theme Mode Toggled", Toast.LENGTH_SHORT).show()
        }

        binding.ivDrawerActionSettings.setOnClickListener {
            binding.drawerLayout.closeDrawer(androidx.core.view.GravityCompat.START)
            navController.navigate(R.id.profileFragment)
        }
    }

    fun openDrawer() {
        binding.drawerLayout.openDrawer(androidx.core.view.GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
