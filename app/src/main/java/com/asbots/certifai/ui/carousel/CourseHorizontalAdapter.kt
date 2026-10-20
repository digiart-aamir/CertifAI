package com.asbots.certifai.ui.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asbots.certifai.data.Course
import com.asbots.certifai.databinding.ItemCourseHorizontalBinding

class CourseHorizontalAdapter(
    private val courses: List<Course>,
    private val onCourseClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseHorizontalAdapter.HorizontalViewHolder>() {

    inner class HorizontalViewHolder(val binding: ItemCourseHorizontalBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HorizontalViewHolder {
        val binding = ItemCourseHorizontalBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return HorizontalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HorizontalViewHolder, position: Int) {
        val course = courses[position]
        with(holder.binding) {
            tvCategory.text = course.category
            tvRating.text = "★ ${course.rating}"
            tvTitle.text = course.title
            tvDuration.text = "${course.duration} • ${course.level}"

            root.setOnClickListener { onCourseClick(course) }
        }
    }

    override fun getItemCount(): Int = courses.size
}
