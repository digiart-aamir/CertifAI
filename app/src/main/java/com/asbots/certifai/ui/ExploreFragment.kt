package com.asbots.certifai.ui

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asbots.certifai.R
import com.asbots.certifai.data.SampleData
import com.asbots.certifai.databinding.FragmentExploreBinding
import com.asbots.certifai.ui.carousel.CourseCarouselAdapter

class ExploreFragment : Fragment() {

    private var _binding: FragmentExploreBinding? = null
    private val binding get() = _binding!!
    private lateinit var carouselAdapter: CourseCarouselAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExploreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        carouselAdapter = CourseCarouselAdapter(SampleData.courses) { course ->
            val bundle = Bundle().apply { putString("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        binding.rvExploreCourses.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = carouselAdapter
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterCourses(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun filterCourses(query: String) {
        val filtered = if (query.isBlank()) {
            SampleData.courses
        } else {
            SampleData.courses.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.category.contains(query, ignoreCase = true) ||
                        it.description.contains(query, ignoreCase = true)
            }
        }
        carouselAdapter = CourseCarouselAdapter(filtered) { course ->
            val bundle = Bundle().apply { putString("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.rvExploreCourses.adapter = carouselAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
