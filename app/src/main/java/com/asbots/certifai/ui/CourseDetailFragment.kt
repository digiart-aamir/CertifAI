package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.asbots.certifai.R
import com.asbots.certifai.data.SampleData
import com.asbots.certifai.databinding.FragmentCourseDetailBinding

class CourseDetailFragment : Fragment() {

    private var _binding: FragmentCourseDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCourseDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getString("courseId") ?: "course_1"
        val course = SampleData.courses.find { it.id == courseId } ?: SampleData.courses.first()

        binding.tvCourseTitle.text = course.title
        binding.tvDescription.text = course.description

        binding.btnStartPlayer.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", course.id) }
            findNavController().navigate(R.id.coursePlayerFragment, bundle)
        }

        binding.btnTakeQuiz.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", course.id) }
            findNavController().navigate(R.id.quizFragment, bundle)
        }

        binding.btnViewProject.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", course.id) }
            findNavController().navigate(R.id.projectFragment, bundle)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
