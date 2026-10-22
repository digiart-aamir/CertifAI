package com.asbots.certifai

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.asbots.certifai.data.SampleData
import com.asbots.certifai.databinding.FragmentHomeBinding
import com.asbots.certifai.ui.carousel.CourseHorizontalAdapter

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val scrollHandler = Handler(Looper.getMainLooper())
    private var scrollRunnable: Runnable? = null
    private var currentScrollPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Header actions
        binding.ivMenu.setOnClickListener {
            (requireActivity() as? MainActivity)?.openDrawer()
        }

        binding.ivProfile.setOnClickListener {
            findNavController().navigate(R.id.profileFragment)
        }

        binding.ivSearch.setOnClickListener {
            findNavController().navigate(R.id.exploreFragment)
        }

        // View All Courses click -> open Explore screen with vertical list
        binding.tvViewAllCourses.setOnClickListener {
            findNavController().navigate(R.id.exploreFragment)
        }

        // Enrolled course card click
        binding.cardEnrolled.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_1") }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }
        binding.btnContinueEnrolled.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_1") }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        // Recommended course card click
        binding.cardRecommended.setOnClickListener {
            val bundle = Bundle().apply { putString("courseId", "course_3") }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        // Horizontal Course Carousel with 2.5s auto-scroll
        val horizontalAdapter = CourseHorizontalAdapter(SampleData.courses) { course ->
            val bundle = Bundle().apply { putString("courseId", course.id) }
            findNavController().navigate(R.id.courseDetailFragment, bundle)
        }

        val layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvHorizontalCarousel.apply {
            this.layoutManager = layoutManager
            adapter = horizontalAdapter
        }

        setupAutoScroll(horizontalAdapter)
    }

    private fun setupAutoScroll(adapter: CourseHorizontalAdapter) {
        scrollRunnable = object : Runnable {
            override fun run() {
                if (_binding != null && adapter.itemCount > 0) {
                    currentScrollPosition = (currentScrollPosition + 1) % adapter.itemCount
                    binding.rvHorizontalCarousel.smoothScrollToPosition(currentScrollPosition)
                    scrollHandler.postDelayed(this, 2500)
                }
            }
        }
        scrollHandler.postDelayed(scrollRunnable!!, 2500)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        scrollRunnable?.let { scrollHandler.removeCallbacks(it) }
        _binding = null
    }
}
