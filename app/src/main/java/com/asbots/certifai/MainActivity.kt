package com.asbots.certifai

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import coil.load
import coil.transform.CircleCropTransformation
import com.asbots.certifai.data.models.UserProfile
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.databinding.ActivityMainBinding
import com.exyte.animatednavbar.AnimatedNavigationBar
import com.exyte.animatednavbar.animation.balltrajectory.Parabolic
import com.exyte.animatednavbar.animation.indendshape.shapeCornerRadius
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var authRepository: AuthRepository

    private var currentSelectedIndex by mutableIntStateOf(1)

    // Drawer Header Views
    private var ivDrawerAvatar: ImageView? = null
    private var tvDrawerName: TextView? = null
    private var tvDrawerEmail: TextView? = null
    private var tvDrawerCoins: TextView? = null
    private var tvDrawerNodes: TextView? = null
    private var llDrawerProfileCard: View? = null

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

        authRepository = AuthRepository(this)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        setupExyteComposeTopNav()
        setupNavigationDrawer()
        checkInitialSession()
    }

    private fun checkInitialSession() {
        if (!authRepository.isUserLoggedIn()) {
            navController.navigate(R.id.onboardingFragment)
        }
    }

    private fun setupExyteComposeTopNav() {
        val composeTopNav = binding.includeTopNav.composeTopNav

        composeTopNav.setContent {
            ExyteTopBar(
                selectedIndex = currentSelectedIndex,
                onTabClick = { index ->
                    when (index) {
                        0 -> openDrawer()
                        1 -> navigateTab(R.id.homeFragment)
                        2 -> navigateTab(R.id.learnFragment)
                        3 -> navigateTab(R.id.exploreFragment)
                        4 -> navigateTab(R.id.certificatesFragment)
                        5 -> navigateTab(R.id.profileFragment)
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
                    currentSelectedIndex = 5
                }
                else -> {
                    composeTopNav.visibility = View.GONE
                }
            }
        }
    }

    private fun navigateTab(destinationId: Int) {
        if (navController.currentDestination?.id != destinationId) {
            try {
                navController.navigate(destinationId) {
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            } catch (_: Exception) {
                try {
                    navController.navigate(destinationId)
                } catch (_: Exception) {
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
                .height(56.dp)
                .padding(horizontal = 4.dp),
            cornerRadius = shapeCornerRadius(24.dp),
            ballAnimation = Parabolic(tween(350)),
            barColor = ComposeColor(0xFF0F172A),
            ballColor = ComposeColor(0xFF38BDF8)
        ) {
            drawableResList.forEachIndexed { index, drawableRes ->
                val isSelectedTab = index == selectedIndex
                val scale by animateFloatAsState(
                    targetValue = if (isSelectedTab) 1.22f else 1.0f,
                    animationSpec = tween(durationMillis = 250),
                    label = "iconScale"
                )

                val iconTint = if (isSelectedTab) {
                    ComposeColor(0xFF38BDF8)
                } else {
                    ComposeColor(0xFF94A3B8)
                }

                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .clickable { onTabClick(index) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelectedTab) {
                        Box(
                            modifier = Modifier
                                .size(width = 44.dp, height = 36.dp)
                                .background(
                                    color = ComposeColor(0x2638BDF8),
                                    shape = RoundedCornerShape(12.dp)
                                )
                        )
                    }

                    Icon(
                        painter = painterResource(id = drawableRes),
                        contentDescription = null,
                        modifier = Modifier
                            .size(24.dp)
                            .scale(scale),
                        tint = iconTint
                    )
                }
            }
        }
    }

    private fun setupNavigationDrawer() {
        binding.drawerLayout.setScrimColor(Color.TRANSPARENT)

        val drawerPanel = binding.llDrawerPanel
        ivDrawerAvatar = drawerPanel.findViewById(R.id.ivDrawerAvatar)
        tvDrawerName = drawerPanel.findViewById(R.id.tvDrawerName)
        tvDrawerEmail = drawerPanel.findViewById(R.id.tvDrawerEmail)
        tvDrawerCoins = drawerPanel.findViewById(R.id.tvDrawerCoins)
        tvDrawerNodes = drawerPanel.findViewById(R.id.tvDrawerNodes)
        llDrawerProfileCard = drawerPanel.findViewById(R.id.llDrawerProfileCard)

        llDrawerProfileCard?.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            if (authRepository.isUserLoggedIn()) {
                navController.navigate(R.id.profileFragment)
            } else {
                navController.navigate(R.id.loginFragment)
            }
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> navController.navigate(R.id.homeFragment)
                R.id.nav_pathways -> navController.navigate(R.id.learnFragment)
                R.id.nav_certificates -> navController.navigate(R.id.certificatesFragment)
                R.id.nav_settings -> navController.navigate(R.id.profileFragment)
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            true
        }

        // Bottom Action Bar
        binding.ivDrawerActionLogout.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            lifecycleScope.launch {
                authRepository.signOut()
                Toast.makeText(this@MainActivity, "Signed out successfully", Toast.LENGTH_SHORT).show()
                updateDrawerProfileUI()
                navController.navigate(R.id.loginFragment)
            }
        }

        binding.ivDrawerActionRefresh.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            updateDrawerProfileUI()
            Toast.makeText(this, "Profile Synced", Toast.LENGTH_SHORT).show()
            val currentId = navController.currentDestination?.id
            if (currentId != null) {
                navController.navigate(currentId)
            }
        }

        binding.ivDrawerActionTheme.setOnClickListener {
            Toast.makeText(this, "Theme Mode Toggled", Toast.LENGTH_SHORT).show()
        }

        binding.ivDrawerActionSettings.setOnClickListener {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            navController.navigate(R.id.profileFragment)
        }
    }

    private fun updateDrawerProfileUI() {
        if (!authRepository.isUserLoggedIn()) {
            tvDrawerName?.text = "Guest Learner"
            tvDrawerEmail?.text = "Tap to Sign in with Google"
            tvDrawerCoins?.text = "🪙 0 Coins"
            tvDrawerNodes?.text = "🎯 0/36 Nodes"
            ivDrawerAvatar?.setImageResource(R.drawable.ic_certifai_shield)
            return
        }

        lifecycleScope.launch {
            authRepository.fetchCurrentProfile()
                .onSuccess { user ->
                    bindDrawerUser(user)
                }
                .onFailure {
                    tvDrawerName?.text = "Learner"
                    tvDrawerEmail?.text = "Signed in"
                }
        }
    }

    private fun bindDrawerUser(user: UserProfile) {
        tvDrawerName?.text = user.name
        tvDrawerEmail?.text = user.email
        tvDrawerCoins?.text = "🪙 ${user.coinsBalance} Coins"
        tvDrawerNodes?.text = "🎯 ${user.completedNodes}/${user.totalNodes} Nodes"

        if (!user.avatar.isNullOrBlank()) {
            ivDrawerAvatar?.load(user.avatar) {
                crossfade(true)
                placeholder(R.drawable.ic_certifai_shield)
                error(R.drawable.ic_certifai_shield)
                transformations(CircleCropTransformation())
            }
        } else {
            ivDrawerAvatar?.setImageResource(R.drawable.ic_certifai_shield)
        }
    }

    fun openDrawer() {
        updateDrawerProfileUI()
        binding.drawerLayout.openDrawer(GravityCompat.START)
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
