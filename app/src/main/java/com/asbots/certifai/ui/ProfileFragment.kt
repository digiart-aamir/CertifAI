package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
            binding.tabSecurity.background = unselectedBg
            binding.tabNotifications.background = unselectedBg

            binding.tabPersonalInfo.setTextColor(mutedColor)
            binding.tabAppearance.setTextColor(mutedColor)
            binding.tabSecurity.setTextColor(mutedColor)
            binding.tabNotifications.setTextColor(mutedColor)

            selectedTab.background = selectedBg
            (selectedTab as? android.widget.TextView)?.setTextColor(whiteColor)

            binding.panelPersonalInfo.visibility = View.GONE
            binding.panelAppearance.visibility = View.GONE
            binding.panelSecurity.visibility = View.GONE
            binding.panelNotifications.visibility = View.GONE

            targetPanel.visibility = View.VISIBLE
        }

        binding.tabPersonalInfo.setOnClickListener {
            selectTab(binding.tabPersonalInfo, binding.panelPersonalInfo)
        }

        binding.tabAppearance.setOnClickListener {
            selectTab(binding.tabAppearance, binding.panelAppearance)
        }

        binding.tabSecurity.setOnClickListener {
            selectTab(binding.tabSecurity, binding.panelSecurity)
        }

        binding.tabNotifications.setOnClickListener {
            selectTab(binding.tabNotifications, binding.panelNotifications)
        }
    }

    private fun setupListeners() {
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Dark Cyberpunk Theme Enabled" else "Light Theme Enabled"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        binding.switchStreakAlerts.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Streak Alerts Enabled" else "Streak Alerts Disabled"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        binding.switchWeeklySummaries.setOnCheckedChangeListener { _, isChecked ->
            val msg = if (isChecked) "Weekly Summaries Enabled" else "Weekly Summaries Disabled"
            Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
        }

        binding.btnSignOut.setOnClickListener {
            performSignOut()
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
        binding.tvDetailEmail.text = "Email: ${user.email}"
        binding.tvDetailUsername.text = "Username: ${displayUsername.removePrefix("@")}"
        binding.tvDetailRole.text = "Role: ${user.role?.uppercase() ?: "USER"}"

        binding.tvRoleBadge.text = user.role?.uppercase() ?: "USER"
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
