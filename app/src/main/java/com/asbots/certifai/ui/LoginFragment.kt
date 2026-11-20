package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.asbots.certifai.R
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepository = AuthRepository(requireActivity())

        if (authRepository.isUserLoggedIn()) {
            findNavController().navigate(R.id.profileFragment)
            return
        }

        binding.btnGoogleSignIn.setOnClickListener {
            performGoogleSignIn()
        }
    }

    private fun performGoogleSignIn() {
        setLoading(isLoading = true)
        lifecycleScope.launch {
            authRepository.performGoogleSignIn(requireActivity())
                .onSuccess { user ->
                    setLoading(isLoading = false)
                    Toast.makeText(requireContext(), "Welcome back, ${user.name}!", Toast.LENGTH_SHORT).show()
                    findNavController().navigate(R.id.profileFragment)
                }
                .onFailure { error ->
                    setLoading(isLoading = false)
                    val errorMsg = error.message ?: ""
                    showSignInNoticeDialog(errorMsg)
                }
        }
    }

    private fun showSignInNoticeDialog(errorMsg: String) {
        val message = if (errorMsg == "NO_CREDENTIALS_ON_DEVICE") {
            "No Google account was found on this device.\n\nWould you like to sign in with a Demo Test Account to preview your profile and ASCoins?"
        } else {
            "Google Sign-In notice: $errorMsg\n\nIf running on a test device or emulator, Google OAuth may require adding the app's SHA-1 certificate fingerprint in Google Cloud Console.\n\nWould you like to sign in with a Demo Test Account to preview your profile and ASCoins?"
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Google Sign-In")
            .setMessage(message)
            .setPositiveButton("Use Demo Account") { _, _ ->
                val user = authRepository.performDevSignIn()
                Toast.makeText(requireContext(), "Signed in as ${user.name}", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.profileFragment)
            }
            .setNegativeButton("Try Again") { _, _ ->
                performGoogleSignIn()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    private fun setLoading(isLoading: Boolean) {
        binding.btnGoogleSignIn.isEnabled = !isLoading
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
