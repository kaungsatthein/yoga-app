package com.example.yogaapp

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class YogaCourse(
    val id: Int,
    val dayOfWeek: String,
    val timeOfCourse: String,
    val capacity: Int,
    val duration: Int,
    val price: Int,
    val typeOfClass: String,
    val description: String
) : Parcelable
