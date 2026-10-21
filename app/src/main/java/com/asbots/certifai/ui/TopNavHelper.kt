package com.asbots.certifai.ui

import android.view.View
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import com.asbots.certifai.R

object TopNavHelper {

    fun setupTopNav(
        fragment: androidx.fragment.app.Fragment,
        tabHome: TextView,
        tabLearn: TextView,
        tabExplore: TextView,
        tabCertificates: TextView,
        tabProfile: TextView,
        activeDestinationId: Int
    ) {
        val tabs = listOf(
            tabHome to R.id.homeFragment,
            tabLearn to R.id.learnFragment,
            tabExplore to R.id.exploreFragment,
            tabCertificates to R.id.certificatesFragment,
            tabProfile to R.id.profileFragment
        )

        // Highlight active tab initially
        tabs.forEach { (tabView, destId) ->
            val isActive = destId == activeDestinationId
            tabView.setBackgroundResource(if (isActive) R.drawable.bg_primary_button else android.R.color.transparent)
            tabView.setTextColor(
                fragment.resources.getColor(
                    if (isActive) android.R.color.white else R.color.text_secondary_light,
                    null
                )
            )

            tabView.setOnClickListener { view ->
                if (destId != activeDestinationId) {
                    view.animate()
                        .alpha(0.6f)
                        .scaleX(0.92f)
                        .scaleY(0.92f)
                        .setDuration(120)
                        .withEndAction {
                            view.animate()
                                .alpha(1f)
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(120)
                                .withEndAction {
                                    fragment.findNavController().navigate(destId)
                                }
                                .start()
                        }
                        .start()
                }
            }
        }
    }
}
