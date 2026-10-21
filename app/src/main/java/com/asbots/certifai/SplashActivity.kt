package com.asbots.certifai

import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat

class SplashActivity : AppCompatActivity() {

    private val splashHandler = Handler(Looper.getMainLooper())
    private var splashWebView: WebView? = null
    private val navigateRunnable = Runnable {
        launchMainActivity()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash_root)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isDarkTheme = (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES
        setupSystemBarAppearance(isDarkTheme)
        setupSplashWebView(isDarkTheme)

        splashHandler.postDelayed(navigateRunnable, 2200L)
    }

    private fun setupSystemBarAppearance(isDarkTheme: Boolean) {
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.isAppearanceLightStatusBars = !isDarkTheme
        windowInsetsController.isAppearanceLightNavigationBars = !isDarkTheme
    }

    @SuppressLint("ClickableViewAccessibility", "SetJavaScriptEnabled")
    private fun setupSplashWebView(isDarkTheme: Boolean) {
        val root = findViewById<View>(R.id.splash_root)
        val webView = findViewById<WebView>(R.id.splashWebView)
        splashWebView = webView

        val bgColor = if (isDarkTheme) "#0B1220".toColorInt() else Color.WHITE
        val assetUrl = if (isDarkTheme) "file:///android_asset/splash_dark.html" else "file:///android_asset/splash_light.html"

        root.setBackgroundColor(bgColor)
        webView.setBackgroundColor(bgColor)
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null)

        webView.setOnTouchListener { _, _ -> true }

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            useWideViewPort = true
            loadWithOverviewMode = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }

        webView.loadUrl(assetUrl)
    }

    private fun launchMainActivity() {
        if (!isFinishing && !isDestroyed) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            @Suppress("DEPRECATION")
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
            finish()
        }
    }

    override fun onDestroy() {
        splashHandler.removeCallbacksAndMessages(null)
        splashWebView?.apply {
            stopLoading()
            setOnTouchListener(null)
            destroy()
        }
        splashWebView = null
        super.onDestroy()
    }
}
