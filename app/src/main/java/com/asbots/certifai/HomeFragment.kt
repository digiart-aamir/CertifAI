package com.asbots.certifai

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.asbots.certifai.data.repository.AuthRepository
import com.asbots.certifai.data.repository.CertifaiRepository
import com.asbots.certifai.databinding.FragmentHomeBinding
import com.asbots.certifai.ui.roadmap.RoadmapPathView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import com.asbots.certifai.data.models.NodeStatus

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
        setupRoadmap()
        setupDockListeners()
    }

    private fun setupHeader() {
        if (authRepository.isUserLoggedIn()) {
            lifecycleScope.launch(Dispatchers.IO) {
                try {
                    authRepository.fetchCurrentProfile()
                        .onSuccess { user ->
                            withContext(Dispatchers.Main) {
                                _binding?.tvHeaderAsCoins?.text = "🪙 ${user.coinsBalance} ASCoins"
                            }
                        }
                } catch (_: Exception) {
                }
            }
        }
    }

    private fun setupRoadmap() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                CertifaiRepository.fetchLiveRoadmap(authRepository.getAuthToken())
            } catch (_: Exception) {
            }
        }

        fun n(
            id: String,
            title: String,
            subtitle: String,
            badge: String = "",
            duration: String = "15m",
            free: Boolean = false,
            status: NodeStatus = if (free) NodeStatus.COMPLETED else NodeStatus.LOCKED
        ) = RoadmapPathView.PathNode(id, title, subtitle, badge, duration, free, status)

        val branches = listOf(
            RoadmapPathView.Branch(
                "Your learning path",
                listOf(
                    n("m1", "Python", "Completed", "FREE", "10m", free = true, status = NodeStatus.COMPLETED),
                    n("m2", "Data and stats", "Completed", "FREE", "15m", free = true, status = NodeStatus.COMPLETED),
                    n("m3", "Deep learning", "You are here", "You are here", "20m", free = true, status = NodeStatus.IN_PROGRESS),
                    n("m4", "LLMs and RAG", "Locked", "3 💎", "20m", free = false, status = NodeStatus.LOCKED),
                    n("m5", "Certification", "Locked", "10 💎", "25m", free = false, status = NodeStatus.LOCKED)
                )
            ),
            RoadmapPathView.Branch(
                "Advanced AI & Autonomous Agents",
                listOf(
                    n("m6", "Prompt Engineering", "Locked", "3 💎", "15m", status = NodeStatus.LOCKED),
                    n("m7", "Multi-Agent Systems", "Locked", "10 💎", "25m", status = NodeStatus.LOCKED),
                    n("m8", "Model Quantization", "Locked", "10 💎", "30m", status = NodeStatus.LOCKED)
                )
            )
        )

        binding.roadmapView.setBranches(branches)
        binding.roadmapView.onNodeClick = { node -> openNodeDetail(node) }
    }

    private fun openNodeDetail(node: RoadmapPathView.PathNode) {
        val bundle = Bundle().apply {
            putString("courseId", "course_1")
            putString("nodeId", node.id)
        }
        findNavController().navigate(R.id.courseDetailFragment, bundle)
    }

    private fun setupDockListeners() {
        binding.cardRootNode.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_1") }
            findNavController().navigate(R.id.action_homeFragment_to_courseDetailFragment, bundle)
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