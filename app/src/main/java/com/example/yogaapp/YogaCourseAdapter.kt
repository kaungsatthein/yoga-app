package com.example.yogaapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.yogaapp.databinding.CourseItemViewBinding

class YogaCourseAdapter(val courses: List<YogaCourse>) :
    RecyclerView.Adapter<YogaCourseAdapter.YogaCourseViewHolder>() {

    inner class YogaCourseViewHolder(val binding: CourseItemViewBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): YogaCourseViewHolder {
        val binding =
            CourseItemViewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return YogaCourseViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: YogaCourseViewHolder,
        position: Int
    ) {
        val course = courses[position]
        holder.binding.txtViewDayOfWeek.text = course.dayOfWeek
    }

    override fun getItemCount(): Int {
        return courses.size
    }

}