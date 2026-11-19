package com.asbots.certifai

import android.animation.Animator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.airbnb.lottie.RenderMode
import com.asbots.certifai.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val splashHandler = Handler(Looper.getMainLooper())
    private var hasNavigated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Force GPU Hardware Acceleration for 60fps smooth playback
        binding.lottieSplash.setRenderMode(RenderMode.HARDWARE)

        binding.lottieSplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                navigateToMain()
            }
            override fun onAnimationCancel(animation: Animator) {
                navigateToMain()
            }
            override fun onAnimationRepeat(animation: Animator) {}
        })

        // Safety fallback timer matching animation duration
        splashHandler.postDelayed({
            navigateToMain()
        }, 4000L)
    }

    private fun navigateToMain() {
        if (!hasNavigated && !isFinishing && !isDestroyed) {
            hasNavigated = true
            splashHandler.removeCallbacksAndMessages(null)
            startActivity(Intent(this, MainActivity::class.java))
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
