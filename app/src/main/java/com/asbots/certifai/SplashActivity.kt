package com.asbots.certifai

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airbnb.lottie.RenderMode
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashHandler = Handler(Looper.getMainLooper())
    private var hasNavigated = false
    private lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        authRepository = AuthRepository(this)

        // Force GPU Hardware Acceleration for 60fps smooth playback
        binding.lottieSplash.setRenderMode(RenderMode.HARDWARE)

        binding.lottieSplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                navigateToNextScreen()
            }
            override fun onAnimationCancel(animation: Animator) {
                navigateToNextScreen()
            }
            override fun onAnimationRepeat(animation: Animator) {}
        })

        // Safety fallback timer matching animation duration
        splashHandler.postDelayed({
            navigateToNextScreen()
        }, 3500L)
    }

    private fun navigateToNextScreen() {
        if (!hasNavigated && !isFinishing && !isDestroyed) {
            hasNavigated = true
            splashHandler.removeCallbacksAndMessages(null)

            val showOnboarding = !authRepository.isUserLoggedIn()
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("SHOW_ONBOARDING", showOnboarding)
            }
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        splashHandler.removeCallbacksAndMessages(null)
    }
}
