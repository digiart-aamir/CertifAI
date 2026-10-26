package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asbots.certifai.R
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepository: AuthRepository

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

        // If user is already logged in, navigate directly to home
        if (authRepository.isUserLoggedIn()) {
            findNavController().navigate(R.id.homeFragment)
            return
        }

        setupOptionListeners()

        binding.btnGoogleSignIn.setOnClickListener {
            findNavController().navigate(R.id.loginFragment)
        }
    }

    private fun setupOptionListeners() {
        binding.cardBeginner.setOnClickListener {
            Toast.makeText(requireContext(), "Beginner Pathway Selected", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.cardIntermediate.setOnClickListener {
            Toast.makeText(requireContext(), "Intermediate Pathway Selected", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }

        binding.cardAdvanced.setOnClickListener {
            Toast.makeText(requireContext(), "Advanced Agentic AI Selected", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
