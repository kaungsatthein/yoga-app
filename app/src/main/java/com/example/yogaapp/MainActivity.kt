package com.example.yogaapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCourseEntry.setOnClickListener {
            intent = Intent(this, CourseEntryActivity::class.java)
            startActivity(intent)
        }

        binding.btnShowCourses.setOnClickListener {
            intent = Intent(this, ShowCoursesActivity::class.java)
            startActivity(intent)
        }
    }
}