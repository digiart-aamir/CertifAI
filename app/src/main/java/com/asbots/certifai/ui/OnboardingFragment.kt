package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.asbots.certifai.R
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepository: AuthRepository

    private val steps = listOf(
        OnboardingStep(
            "STEP 01 OF 04 • FOUNDATIONS",
            "Master Real AI from Scratch",
            "Go beyond basic tutorials. Build intuition in Python, machine learning, neural architectures, and transformer math with structured pathways.",
            listOf("Interactive Labs", "Bilingual", "Milestones"),
            R.drawable.onboarding_step_1_ai_neural_core
        ),
        OnboardingStep(
            "STEP 02 OF 04 • PRACTICAL LABS",
            "Build Production Systems",
            "Deploy real multi-agent pipelines, LangGraph workflows, vector retrieval (RAG), and fine-tuned models directly in browser cloud sandboxes.",
            listOf("Cloud Sandboxes", "Multi-Agent RAG", "Production APIs"),
            R.drawable.onboarding_step_2_cloud_sandbox
        ),
        OnboardingStep(
            "STEP 03 OF 04 • CODE DEFENSE",
            "Automated Capstone Defense",
            "Submissions are verified against live unit tests, RAGAS benchmark hallucination metrics, and automated code review before graduation.",
            listOf("Auto-Grading", "Rubric Benchmarks", "Plagiarism Free"),
            R.drawable.onboarding_step_3_defense_radar
        ),
        OnboardingStep(
            "STEP 04 OF 04 • GLOBAL CREDENTIALS",
            "Get Certified & Stand Out",
            "Earn verifiable digital credentials recognized by global tech employers. Share one-click verifiable badges directly to LinkedIn, GitHub, and portfolios.",
            listOf("On-Chain Verified", "LinkedIn Share", "Portfolio Proof"),
            R.drawable.onboarding_step_4_certificate,
            "Get Started Now"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepository = AuthRepository(requireContext())

        if (authRepository.isUserLoggedIn()) {
            findNavController().navigate(R.id.homeFragment)
            return
        }

        val adapter = OnboardingAdapter(steps)
        binding.viewPager.adapter = adapter

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUIForPage(position)
            }
        })

        binding.btnContinue.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < steps.size - 1) {
                binding.viewPager.currentItem = current + 1
            } else {
                findNavController().navigate(R.id.loginFragment)
            }
        }

        binding.btnBackAction.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) {
                binding.viewPager.currentItem = current - 1
            }
        }

        binding.btnBack.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) {
                binding.viewPager.currentItem = current - 1
            } else {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        binding.btnSkip.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun updateUIForPage(position: Int) {
        if (position == steps.size - 1) {
            binding.btnContinue.text = "Get Started Now"
        } else {
            binding.btnContinue.text = "Continue"
        }

        if (position > 0) {
            binding.btnBackAction.visibility = View.VISIBLE
        } else {
            binding.btnBackAction.visibility = View.GONE
        }

        val activeBg = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(android.graphics.Color.parseColor("#2563EB"))
        }
        val inactiveBg = android.graphics.drawable.GradientDrawable().apply {
            shape = android.graphics.drawable.GradientDrawable.RECTANGLE
            cornerRadius = 16f
            setColor(android.graphics.Color.parseColor("#E2E8F0"))
        }

        binding.dot1.layoutParams.width = if (position == 0) 28 * resources.displayMetrics.density.toInt() else 8 * resources.displayMetrics.density.toInt()
        binding.dot2.layoutParams.width = if (position == 1) 28 * resources.displayMetrics.density.toInt() else 8 * resources.displayMetrics.density.toInt()
        binding.dot3.layoutParams.width = if (position == 2) 28 * resources.displayMetrics.density.toInt() else 8 * resources.displayMetrics.density.toInt()
        binding.dot4.layoutParams.width = if (position == 3) 28 * resources.displayMetrics.density.toInt() else 8 * resources.displayMetrics.density.toInt()

        binding.dot1.background = if (position == 0) activeBg else inactiveBg
        binding.dot2.background = if (position == 1) activeBg else inactiveBg
        binding.dot3.background = if (position == 2) activeBg else inactiveBg
        binding.dot4.background = if (position == 3) activeBg else inactiveBg

        binding.dot1.requestLayout()
        binding.dot2.requestLayout()
        binding.dot3.requestLayout()
        binding.dot4.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
