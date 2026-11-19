package com.asbots.certifai.ui

import android.animation.Animator
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asbots.certifai.R
import com.asbots.certifai.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!
    private val splashHandler = Handler(Looper.getMainLooper())
    private var hasNavigated = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.lottieSplash.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                navigateToNext()
            }
            override fun onAnimationCancel(animation: Animator) {
                navigateToNext()
            }
            override fun onAnimationRepeat(animation: Animator) {}
        })

        splashHandler.postDelayed({
            navigateToNext()
        }, 3000L)
    }

    private fun navigateToNext() {
        if (!hasNavigated && isAdded) {
            hasNavigated = true
            splashHandler.removeCallbacksAndMessages(null)
            findNavController().navigate(R.id.onboardingFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        splashHandler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
