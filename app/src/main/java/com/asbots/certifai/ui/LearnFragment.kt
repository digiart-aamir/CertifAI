package com.asbots.certifai.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asbots.certifai.R
import com.asbots.certifai.data.SampleData
import com.asbots.certifai.data.repository.CertifaiRepository
import com.asbots.certifai.databinding.FragmentLearnBinding
import com.asbots.certifai.ui.carousel.CourseHorizontalAdapter

class LearnFragment : Fragment() {

    private var _binding: FragmentLearnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLearnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = CourseHorizontalAdapter(SampleData.courses) { course ->
            val bundle = Bundle().apply { putString("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        binding.rvLearnModules.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = adapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
