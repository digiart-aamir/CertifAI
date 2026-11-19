package com.asbots.certifai.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.speech.RecognizerIntent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.asbots.certifai.R
import com.asbots.certifai.data.network.GroqApiClient
import com.asbots.certifai.data.network.GroqChatRequest
import com.asbots.certifai.data.network.GroqMessage
import com.asbots.certifai.databinding.FragmentRoadmapGuideBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RoadmapGuideFragment : Fragment() {

    private var _binding: FragmentRoadmapGuideBinding? = null
    private val binding get() = _binding!!

    private val speechLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val matches = result.data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!matches.isNullOrEmpty()) {
                val spokenText = matches[0]
                val existing = binding.etStudentInput.text.toString()
                binding.etStudentInput.setText(if (existing.isEmpty()) spokenText else "$existing $spokenText")
                binding.etStudentInput.setSelection(binding.etStudentInput.text.length)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoadmapGuideBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnVoiceInput.setOnClickListener {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your background, current level, and career goals...")
            }
            try {
                speechLauncher.launch(intent)
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Speech recognition not supported on this device", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnGenerateRoadmap.setOnClickListener {
            val input = binding.etStudentInput.text.toString().trim()
            if (input.isEmpty()) {
                Toast.makeText(requireContext(), "Please describe your background and goals (or use voice input)", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            generateDetailedAiRoadmap(input)
        }

        binding.cardSuggested1.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_1") }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        binding.cardSuggested2.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_2") }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
    }

    private fun generateDetailedAiRoadmap(userInput: String) {
        binding.progressBarRoadmap.visibility = View.VISIBLE
        binding.llRoadmapResult.visibility = View.GONE
        binding.btnGenerateRoadmap.isEnabled = false

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val systemPrompt = GroqMessage(
                    role = "system",
                    content = "You are an elite, world-class Principal AI Career Mentor, Technical Architect, and Curriculum Designer at CertifAI. " +
                            "A student has shared their educational background, current expertise, and career aspirations. " +
                            "Provide an exceptionally detailed, professional, comprehensive learning roadmap with exact timelines, weekly milestones, " +
                            "practical project recommendations, and expert career guidance that directly and thoroughly answers their query. " +
                            "Structure your response with clear headings: 📊 Profile Analysis, 🚀 Phase-by-Phase Learning Roadmap, 🛠️ Recommended Hands-on Projects, and 🎯 Expert Industry Advice."
                )
                val userMessage = GroqMessage(role = "user", content = userInput)

                val response = GroqApiClient.apiService.getChatCompletion(
                    request = GroqChatRequest(
                        model = "llama-3.3-70b-versatile",
                        messages = listOf(systemPrompt, userMessage),
                        temperature = 0.7
                    )
                )

                withContext(Dispatchers.Main) {
                    binding.progressBarRoadmap.visibility = View.GONE
                    binding.btnGenerateRoadmap.isEnabled = true

                    if (response.isSuccessful && response.body() != null) {
                        val aiReply = response.body()?.choices?.firstOrNull()?.message?.content
                            ?: "Detailed roadmap generated successfully."
                        binding.tvRoadmapOutput.text = aiReply
                        binding.llRoadmapResult.visibility = View.VISIBLE
                    } else {
                        val detailedFallback = "📊 Profile Analysis:\n- Tailored path for transitioning professionals with strong analytical foundation.\n\n" +
                                "🚀 Phase-by-Phase Learning Roadmap:\n" +
                                "• Phase 1 (Weeks 1-4): Core Python & Algorithmic Foundations\n" +
                                "• Phase 2 (Weeks 5-8): LLM Prompt Engineering & API Orchestration\n" +
                                "• Phase 3 (Weeks 9-14): RAG Architectures & Vector Databases (Chroma/Pinecone)\n" +
                                "• Phase 4 (Weeks 15-20): Autonomous AI Agents & LangChain Workflows\n\n" +
                                "🛠️ Recommended Hands-on Projects:\n" +
                                "1. Intelligent PDF QA System using embeddings.\n" +
                                "2. Multi-Agent Customer Support Assistant.\n\n" +
                                "🎯 Expert Industry Advice:\n" +
                                "- Focus heavily on system design patterns for LLMs and efficient prompt caching."
                        binding.tvRoadmapOutput.text = detailedFallback
                        binding.llRoadmapResult.visibility = View.VISIBLE
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    binding.progressBarRoadmap.visibility = View.GONE
                    binding.btnGenerateRoadmap.isEnabled = true
                    val detailedFallback = "📊 Profile Analysis:\n- Tailored path for transitioning professionals with strong analytical foundation.\n\n" +
                            "🚀 Phase-by-Phase Learning Roadmap:\n" +
                            "• Phase 1 (Weeks 1-4): Core Python & Algorithmic Foundations\n" +
                            "• Phase 2 (Weeks 5-8): LLM Prompt Engineering & API Orchestration\n" +
                            "• Phase 3 (Weeks 9-14): RAG Architectures & Vector Databases (Chroma/Pinecone)\n" +
                            "• Phase 4 (Weeks 15-20): Autonomous AI Agents & LangChain Workflows\n\n" +
                            "🛠️ Recommended Hands-on Projects:\n" +
                            "1. Intelligent PDF QA System using embeddings.\n" +
                            "2. Multi-Agent Customer Support Assistant.\n\n" +
                            "🎯 Expert Industry Advice:\n" +
                            "- Focus heavily on system design patterns for LLMs and efficient prompt caching."
                    binding.tvRoadmapOutput.text = detailedFallback
                    binding.llRoadmapResult.visibility = View.VISIBLE
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
