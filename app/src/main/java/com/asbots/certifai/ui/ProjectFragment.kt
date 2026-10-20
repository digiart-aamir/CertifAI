package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.asbots.certifai.data.SampleData
import com.asbots.certifai.databinding.FragmentProjectBinding

class ProjectFragment : Fragment() {

    private var _binding: FragmentProjectBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProjectBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val courseId = arguments?.getString("courseId") ?: "course_1"
        val course = SampleData.courses.find { it.id == courseId } ?: SampleData.courses.first()

        if (course.projectStatement.isNotEmpty()) {
            binding.tvProjectStatement.text = course.projectStatement
        }

        binding.btnDownloadStarter.setOnClickListener {
            Toast.makeText(requireContext(), "Downloading starter files...", Toast.LENGTH_SHORT).show()
        }

        binding.btnSubmitProject.setOnClickListener {
            Toast.makeText(requireContext(), "Project Submitted Successfully! 🚀", Toast.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
