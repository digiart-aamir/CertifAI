package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import coil.load
import coil.transform.CircleCropTransformation
import com.asbots.certifai.R
import com.asbots.certifai.data.models.UserProfile
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.databinding.FragmentProfileBinding
import kotlinx.coroutines.launch

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepository = AuthRepository(requireContext())

        setupListeners()
        loadProfileData()
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Dark Mode Enabled" else "Light Mode Enabled"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        binding.switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Push Notifications Enabled" else "Notifications Disabled"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnGoogleSignIn.setOnClickListener {
            performGoogleSignIn()
        }

        binding.btnSyncProfile.setOnClickListener {
            syncProfile()
        }

        binding.btnSignOut.setOnClickListener {
            performSignOut()
        }
    }

    private fun loadProfileData() {
        if (!authRepository.isUserLoggedIn()) {
            showLoggedOutView()
            return
        }

        setLoading(isLoading = true)
        lifecycleScope.launch {
            authRepository.fetchCurrentProfile()
                .onSuccess { user ->
                    setLoading(isLoading = false)
                    showLoggedInView(user)
                }
                .onFailure {
                    setLoading(isLoading = false)
                    showLoggedOutView()
                }
        }
    }

    private fun performGoogleSignIn() {
        setLoading(isLoading = true)
        lifecycleScope.launch {
            authRepository.performGoogleSignIn()
                .onSuccess { user ->
                    setLoading(isLoading = false)
                    Toast.makeText(requireContext(), "Signed in as ${user.name}", Toast.LENGTH_SHORT).show()
                    showLoggedInView(user)
                }
                .onFailure { error ->
                    setLoading(isLoading = false)
                    val errorMsg = error.message ?: ""
                    if (errorMsg == "NO_CREDENTIALS_ON_DEVICE") {
                        showNoCredentialsDialog()
                    } else {
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun showNoCredentialsDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("No Google Credentials Available")
            .setMessage("No Google account was found on this device, or the app's SHA-1 fingerprint needs to be added in Google Cloud Console.\n\nWould you like to sign in with a Demo Test Account to preview the profile and ASCoins features?")
            .setPositiveButton("Use Demo Account") { _, _ ->
                val user = authRepository.performDevSignIn()
                Toast.makeText(requireContext(), "Signed in with Demo Account: ${user.name}", Toast.LENGTH_SHORT).show()
                showLoggedInView(user)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun syncProfile() {
        setLoading(isLoading = true)
        lifecycleScope.launch {
            authRepository.fetchCurrentProfile()
                .onSuccess { user ->
                    setLoading(isLoading = false)
                    Toast.makeText(requireContext(), "Profile synced!", Toast.LENGTH_SHORT).show()
                    showLoggedInView(user)
                }
                .onFailure { error ->
                    setLoading(isLoading = false)
                    Toast.makeText(requireContext(), error.message ?: "Failed to sync profile", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun performSignOut() {
        setLoading(isLoading = true)
        lifecycleScope.launch {
            authRepository.signOut()
            setLoading(isLoading = false)
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()
            showLoggedOutView()
        }
    }

    private fun showLoggedInView(user: UserProfile) {
        binding.llLoggedOutContainer.visibility = View.GONE
        binding.llLoggedInContainer.visibility = View.VISIBLE

        binding.tvUserName.text = user.name
        binding.tvUserEmail.text = user.email
        binding.tvCoinsBalance.text = user.coinsBalance.toString()
        binding.tvNodesProgress.text = getString(R.string.nodes_progress_format, user.completedNodes, user.totalNodes)

        binding.tvVerifiedBadge.visibility = if (user.isVerified) View.VISIBLE else View.GONE

        if (!user.avatar.isNullOrBlank()) {
            binding.ivAvatar.load(user.avatar) {
                crossfade(true)
                placeholder(R.drawable.ic_certifai_shield)
                error(R.drawable.ic_certifai_shield)
                transformations(CircleCropTransformation())
            }
        } else {
            binding.ivAvatar.setImageResource(R.drawable.ic_certifai_shield)
        }
    }

    private fun showLoggedOutView() {
        binding.llLoggedInContainer.visibility = View.GONE
        binding.llLoggedOutContainer.visibility = View.VISIBLE
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnGoogleSignIn.isEnabled = !isLoading
        binding.btnSyncProfile.isEnabled = !isLoading
        binding.btnSignOut.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
