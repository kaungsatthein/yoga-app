package com.example.yogaapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yogaapp.databinding.ActivityShowCoursesBinding

class ShowCoursesActivity : AppCompatActivity() {

    lateinit var binding: ActivityShowCoursesBinding
    lateinit var dbHelper: YogaDBHelper

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShowCoursesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = YogaDBHelper(this)

        val courses = dbHelper.getAllCourses()

        val adapter = YogaCourseAdapter(courses)
        adapter.notifyDataSetChanged()
        binding.recyclerViewCourses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCourses.adapter = adapter
    }
}