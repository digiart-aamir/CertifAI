package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.asbots.certifai.data.SampleData
import com.asbots.certifai.databinding.FragmentCoursePlayerBinding

class CoursePlayerFragment : Fragment() {

    private var _binding: FragmentCoursePlayerBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCoursePlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getString("courseId") ?: "course_1"
        val course = SampleData.courses.find { it.id == courseId } ?: SampleData.courses.first()
        val lesson = course.modules.firstOrNull()?.lessons?.firstOrNull()

        if (lesson != null) {
            binding.tvLessonTitle.text = lesson.title
            binding.tvContentText.text = lesson.contentText
        }

        binding.btnCompleteLesson.setOnClickListener {
            Toast.makeText(requireContext(), "Lesson Completed! 🎉", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
