package com.asbots.certifai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asbots.certifai.data.models.MindmapNode
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.data.repository.CertifaiRepository
import com.asbots.certifai.databinding.FragmentHomeBinding
import com.asbots.certifai.ui.roadmap.MindmapNodeAdapter
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var authRepository: AuthRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        authRepository = AuthRepository(requireContext())

        setupHeader()
        setupMindmapBranches()
        setupDockListeners()
    }

    private fun setupHeader() {
        if (authRepository.isUserLoggedIn()) {
            lifecycleScope.launch {
                authRepository.fetchCurrentProfile()
                    .onSuccess { user ->
                        binding.tvHeaderAsCoins.text = "🪙 ${user.coinsBalance} ASCoins"
                    }
            }
        }
    }

    private fun setupMindmapBranches() {
        lifecycleScope.launch {
            CertifaiRepository.fetchLiveRoadmap()
        }

        // Branch 1: Core Syntax & Structures
        val branch1 = listOf(
            MindmapNode("m1", "Syntax & Setup", "Variables & I/O", "FREE", "100m", isFree = true),
            MindmapNode("m2", "Control Flow", "If/Else & Loops", "3 💎", "15m"),
            MindmapNode("m3", "Data Structures", "Lists, Dicts, Sets", "3 💎", "20m"),
            MindmapNode("m4", "Decorators", "Closures & Wrappers", "10 💎", "20m")
        )

        // Branch 2: Functions & File Engineering
        val branch2 = listOf(
            MindmapNode("m5", "Data Types", "Numbers & Str", "FREE", "10m", isFree = true),
            MindmapNode("m6", "Functions", "Args & Return", "3 💎", "15m"),
            MindmapNode("m7", "OOP & Classes", "Inheritance & MRO", "3 💎", "25m"),
            MindmapNode("m8", "Modules", "Imports & Pip", "3 💎", "15m"),
            MindmapNode("m9", "File Handling", "With Context, CSV", "3 💎", "15m"),
            MindmapNode("m10", "Error Handling", "Try/Except Blocks", "3 💎", "15m")
        )

        // Branch 3: Advanced Concurrency & APIs
        val branch3 = listOf(
            MindmapNode("m11", "Regex", "Pattern Matching", "3 💎", "20m"),
            MindmapNode("m12", "Threading", "GIL & Concurrency", "10 💎", "25m"),
            MindmapNode("m13", "Memory", "Ref Counts & GC", "10 💎", "20m"),
            MindmapNode("m14", "Virtual Envs", "Venv & Packages", "10 💎", "15m"),
            MindmapNode("m15", "APIs & Requests", "HTTP & REST JSON", "10 💎", "20m"),
            MindmapNode("m16", "CLI Tools", "Argparse & Sys", "10 💎", "20m"),
            MindmapNode("m17", "Type Hinting", "Typing & Pydantic", "10 💎", "15m"),
            MindmapNode("m18", "Databases", "SQLite & SQL", "10 💎", "25m")
        )

        val adapter1 = MindmapNodeAdapter(branch1) { node -> openNodeDetail(node) }
        val adapter2 = MindmapNodeAdapter(branch2) { node -> openNodeDetail(node) }
        val adapter3 = MindmapNodeAdapter(branch3) { node -> openNodeDetail(node) }

        binding.rvBranch1.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapter1
        }

        binding.rvBranch2.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapter2
        }

        binding.rvBranch3.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = adapter3
        }
    }

    private fun openNodeDetail(node: MindmapNode) {
        val bundle = Bundle().apply { putString("courseId", "course_1") }
        findNavController().navigate(R.id.courseDetailFragment, bundle)
    }

    private fun setupDockListeners() {
        binding.cardRootNode.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_1") }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        binding.btnDockStats.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.btnDockTest.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_1") }
            findNavController().navigate(R.id.quizFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
