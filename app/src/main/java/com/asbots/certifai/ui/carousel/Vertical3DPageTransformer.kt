package com.asbots.certifai.ui.carousel

import android.view.View
import androidx.viewpager2.widget.ViewPager2
import kotlin.math.abs

class Vertical3DPageTransformer : ViewPager2.PageTransformer {

    override fun transformPage(page: View, position: Float) {
        page.apply {
            when {
                position < -1 -> {
                    // Page is off-screen to the top
                    alpha = 0f
                }
                position <= 1 -> {
                    // Page is transitioning
                    alpha = 1f - (abs(position) * 0.35f)

                    // 3D Scale effect: Active card is 100%, adjacent cards scale down to 85%
                    val scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - abs(position))
                    scaleX = scaleFactor
                    scaleY = scaleFactor

                    // Vertical translation offset for card stack depth
                    translationY = -position * (height / 4f)
                }
                else -> {
                    // Page is off-screen to the bottom
                    alpha = 0f
                }
            }
        }
    }

    companion object {
        private const val MIN_SCALE = 0.85f
    }
}
