package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
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

        setupSubTabs()
        setupListeners()
        loadProfileData()
    }

    private fun setupSubTabs() {
        val selectedBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_selected)
        val unselectedBg = ContextCompat.getDrawable(requireContext(), R.drawable.bg_chip_unselected)

        val whiteColor = ContextCompat.getColor(requireContext(), R.color.white)
        val mutedColor = ContextCompat.getColor(requireContext(), R.color.text_secondary_light)

        fun selectTab(selectedTab: View, targetPanel: View) {
            binding.tabPersonalInfo.background = unselectedBg
            binding.tabAppearance.background = unselectedBg
            binding.tabNotifications.background = unselectedBg
            binding.tabSecurity.background = unselectedBg

            binding.tabPersonalInfo.setTextColor(mutedColor)
            binding.tabAppearance.setTextColor(mutedColor)
            binding.tabNotifications.setTextColor(mutedColor)
            binding.tabSecurity.setTextColor(mutedColor)

            selectedTab.background = selectedBg
            (selectedTab as? android.widget.TextView)?.setTextColor(whiteColor)

            binding.panelPersonalInfo.visibility = View.GONE
            binding.panelAppearance.visibility = View.GONE
            binding.panelNotifications.visibility = View.GONE
            binding.panelSecurity.visibility = View.GONE

            targetPanel.visibility = View.VISIBLE
        }

        binding.tabPersonalInfo.setOnClickListener {
            selectTab(binding.tabPersonalInfo, binding.panelPersonalInfo)
        }

        binding.tabAppearance.setOnClickListener {
            selectTab(binding.tabAppearance, binding.panelAppearance)
        }

        binding.tabNotifications.setOnClickListener {
            selectTab(binding.tabNotifications, binding.panelNotifications)
        }

        binding.tabSecurity.setOnClickListener {
            selectTab(binding.tabSecurity, binding.panelSecurity)
        }
    }

    private fun setupListeners() {
        binding.btnSaveProfile.setOnClickListener {
            saveProfileChanges()
        }

        binding.btnApplyTheme.setOnClickListener {
            saveAppearanceSettings()
        }

        binding.btnSaveNotifications.setOnClickListener {
            saveNotificationSettings()
        }

        binding.btnSignOut.setOnClickListener {
            showSignOutConfirmation()
        }
    }

    private fun loadProfileData() {
        if (!authRepository.isUserLoggedIn()) {
            authRepository.performDevSignIn().let { bindUserProfile(it) }
            return
        }

        lifecycleScope.launch {
            authRepository.fetchCurrentProfile()
                .onSuccess { user ->
                    bindUserProfile(user)
                }
                .onFailure {
                    authRepository.performDevSignIn().let { user ->
                        bindUserProfile(user)
                    }
                }
        }
    }

    private fun bindUserProfile(user: UserProfile) {
        val displayName = if (user.name.isNotBlank()) user.name else "Aamir Aamir"
        val displayUsername = if (!user.username.isNullOrBlank()) "@${user.username}" else "@digiartaamir"

        binding.tvUserName.text = displayName
        binding.tvUserUsername.text = displayUsername

        binding.tvRoleBadge.text = user.role?.uppercase() ?: "USER"
        binding.tvVerifiedBadge.visibility = if (user.isVerified) View.VISIBLE else View.GONE
        binding.tvJoinedDateBadge.text = "Joined Mar 2026"

        binding.tvCoinsStat.text = "💎 ${user.coinsBalance} ASCoins"
        binding.tvNodesStat.text = "🎯 ${user.completedNodes}/${user.totalNodes} Topics"
        binding.tvBadgesStat.text = "🏆 ${user.badgesCount} Badges"

        // Populate Tab 1 Form Fields
        binding.etFullName.setText(user.name)
        binding.etUsername.setText(displayUsername.removePrefix("@"))
        binding.etEmail.setText(user.email)
        binding.etPhone.setText(user.phone ?: "")
        binding.etBio.setText(user.bio ?: "")
        binding.etLocation.setText(user.location ?: "")

        // Populate Tab 3 Switches
        val settings = user.settings
        binding.switchStreakAlerts.isChecked = (settings?.receiveStreakAlerts ?: 1) == 1
        binding.switchWeeklySummaries.isChecked = (settings?.receiveWeeklyReminders ?: 1) == 1

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

    private fun saveProfileChanges() {
        val name = binding.etFullName.text.toString().trim()
        val username = binding.etUsername.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()

        if (name.isBlank()) {
            Toast.makeText(requireContext(), "Full Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            authRepository.updateProfile(
                name = name,
                username = username,
                bio = bio,
                phone = phone,
                location = location,
                website = null
            ).onSuccess { updatedUser ->
                bindUserProfile(updatedUser)
                Toast.makeText(requireContext(), "Profile updated successfully! 🎉", Toast.LENGTH_SHORT).show()
            }.onFailure { error ->
                Toast.makeText(requireContext(), error.message ?: "Failed to update profile", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveAppearanceSettings() {
        val theme = if (binding.switchDarkMode.isChecked) "dark" else "light"
        lifecycleScope.launch {
            authRepository.updateSettings(
                theme = theme,
                streakAlerts = if (binding.switchStreakAlerts.isChecked) 1 else 0,
                weeklyReminders = if (binding.switchWeeklySummaries.isChecked) 1 else 0,
                marketing = 0
            ).onSuccess {
                Toast.makeText(requireContext(), "Theme applied successfully! 🎨", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveNotificationSettings() {
        lifecycleScope.launch {
            authRepository.updateSettings(
                theme = "system",
                streakAlerts = if (binding.switchStreakAlerts.isChecked) 1 else 0,
                weeklyReminders = if (binding.switchWeeklySummaries.isChecked) 1 else 0,
                marketing = 0
            ).onSuccess {
                Toast.makeText(requireContext(), "Notification settings saved! 🔔", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showSignOutConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Sign Out")
            .setMessage("Are you sure you want to sign out of your ASBots account?")
            .setPositiveButton("Sign Out") { _, _ ->
                performSignOut()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun performSignOut() {
        lifecycleScope.launch {
            authRepository.signOut()
            Toast.makeText(requireContext(), "Signed out successfully", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.loginFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
