package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.asbots.certifai.data.SampleData
import com.asbots.certifai.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private var _binding: FragmentQuizBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getString("courseId") ?: "course_1"
        val course = SampleData.courses.find { it.id == courseId } ?: SampleData.courses.first()
        val question = course.quizQuestions.firstOrNull()

        if (question != null) {
            binding.tvQuestion.text = question.questionText
            binding.rbOpt1.text = question.options.getOrNull(0) ?: "Option 1"
            binding.rbOpt2.text = question.options.getOrNull(1) ?: "Option 2"
            binding.rbOpt3.text = question.options.getOrNull(2) ?: "Option 3"
            binding.rbOpt4.text = question.options.getOrNull(3) ?: "Option 4"
        }

        binding.btnSubmitQuiz.setOnClickListener {
            Toast.makeText(requireContext(), "Quiz Submitted! Score: 100% 🎯", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
