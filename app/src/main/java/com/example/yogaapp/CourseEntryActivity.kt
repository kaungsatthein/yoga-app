package com.example.yogaapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.databinding.ActivityCourseEntryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CourseEntryActivity : AppCompatActivity() {

    lateinit var binding: ActivityCourseEntryBinding
    private val dayOfWeek = listOf(
        "Select Day of Week...",
        "Monday",
        "Tuesday",
        "Wednesday",
        "Thursday",
        "Friday",
        "Saturday",
        "Sunday"
    )
    private var selectedDay: String = ""
    private var selectedTime: String = ""
    private var selectedDate: String = ""
    private var description: String = ""
    private var typeOfClass: String = ""
    private var capacity: String = ""
    private var duration: String = ""
    private var price: String = ""
    private var flag: Boolean = false
    lateinit var dbHelper: YogaDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        dbHelper = YogaDBHelper(this)

        binding = ActivityCourseEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Day picker
        val dayArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayOfWeek)
        dayArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerDayOfWeek.adapter = dayArrayAdapter

        binding.spinnerDayOfWeek.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    selectedDay = dayOfWeek[position]
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    //do nothing
                }

            }

        //Time picker
        val calendar = Calendar.getInstance()
        binding.txtTimePicker.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                this, { _, hour, min ->
                    val cal = Calendar.getInstance()
                    cal.set(Calendar.HOUR_OF_DAY, hour)
                    cal.set(Calendar.MINUTE, min)
                    val timeFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())
                    selectedTime = timeFormat.format(cal.time)
                    binding.txtTimePicker.text = "Time of course: $selectedTime"
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), false
            )
            timePickerDialog.show()
        }

        //Date picker
        binding.txtDatePicker.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, y, m, d ->
                    val cal = Calendar.getInstance()
                    cal.set(y, m, d)
                    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    selectedDate = dateFormat.format(cal.time)
                    binding.txtDatePicker.text = "Date of course: $selectedDate"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }


        // submit button
        binding.btnSaveCourse.setOnClickListener {

            flag = false

            //Type of class
            val checkedId = binding.rdoGroupType.checkedRadioButtonId
            if (checkedId != -1) {
                // select one option
                val rdoBtn = findViewById<RadioButton>(checkedId)
                typeOfClass = rdoBtn.text.toString()
            } else {
                // select no option
                typeOfClass = "No selected type of class."
                flag = true
            }

            if (selectedDay == dayOfWeek[0]) {
                selectedDay = "No selected day of the week."
                flag = true
            }

            if (selectedTime == "") {
                selectedTime = "No selected time."
                flag = true
            }

            capacity = binding.editCapacity.text.toString()

            if (capacity.isEmpty()) {
                binding.editCapacity.error = "Please enter capacity."
                flag = true
            }

            duration = binding.editDuration.text.toString()

            if (duration.isEmpty()) {
                binding.editDuration.error = "Please enter duration."
                flag = true
            }

            price = binding.editPrice.text.toString()

            if (price.isEmpty()) {
                binding.editPrice.error = "Please enter price."
                flag = true
            }

            description = binding.editDescription.text.toString()

            Toast.makeText(
                this,
                "$selectedDay, $selectedTime, $typeOfClass, $description",
                Toast.LENGTH_LONG
            ).show()

            if (!flag) {
                val stringBuilder = StringBuilder();
                stringBuilder
                    .append("Day of week: $selectedDay\n")
                    .append("Day of time: $selectedTime\n")
                    .append("capacity: $capacity\n")
                    .append("duration: $duration\n")
                    .append("price: $price\n")
                    .append("Type of class: $typeOfClass\n")
                    .append("description: $description")

                val yogaCourseObj = YogaCourse(
                    0,
                    selectedDate,
                    selectedTime,
                    capacity.toInt(),
                    duration.toInt(),
                    price.toInt(),
                    typeOfClass,
                    description
                )

                val alertDialog = AlertDialog.Builder(this)

                alertDialog
                    .setTitle("Are you sure want to save?")
                    .setMessage(stringBuilder.toString())
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, _ ->
                        val result = dbHelper.saveCourse(yogaCourseObj)
                        if (result != (-1).toLong()) {
                            Toast.makeText(this, "Save course.", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "You cannot save course.", Toast.LENGTH_LONG)
                                .show()
                        }
                        dialog.dismiss()

                        val courses = dbHelper.getAllCourses();
                        courses.forEach { course ->
                            Log.i(
                                "yoga courses",
                                "***" + course.dayOfWeek + "," + course.timeOfCourse
                            )
                        }

                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        Toast.makeText(this, "No clicked!", Toast.LENGTH_LONG).show()
                        dialog.dismiss()
                    }

                val alert = alertDialog.create()
                alert.show()
            }
        }
    }
}