package com.asbots.certifai.ui.carousel

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.asbots.certifai.data.Course
import com.asbots.certifai.databinding.ItemCourseCarouselBinding

class CourseCarouselAdapter(
    private val courses: List<Course>,
    private val onCourseClick: (Course) -> Unit
) : RecyclerView.Adapter<CourseCarouselAdapter.CarouselViewHolder>() {

    inner class CarouselViewHolder(val binding: ItemCourseCarouselBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarouselViewHolder {
        val binding = ItemCourseCarouselBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CarouselViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CarouselViewHolder, position: Int) {
        val course = courses[position]
        with(holder.binding) {
            tvStageBadge.text = course.stageText
            tvRating.text = "★ ${course.rating}"
            tvCourseTitle.text = course.title
            tvCategory.text = course.category
            tvLevel.text = course.level
            tvDuration.text = course.duration
            tvDescription.text = course.description
            tvProgressPercent.text = "${course.progressPercent}%"
            pbProgress.progress = course.progressPercent

            btnStartCourse.text = if (course.progressPercent > 0) "▶  Resume Learning" else "▶  Start Course"

            btnStartCourse.setOnClickListener {
                onCourseClick(course)
            }
            root.setOnClickListener {
                onCourseClick(course)
            }
        }
    }

    override fun getItemCount(): Int = courses.size
}
