package com.example.yogaapp

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.yogaapp.databinding.ActivityShowCoursesBinding
import java.util.Calendar


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

        val adapter = YogaCourseAdapter(
            courses,
            onEditClick = { yogaCourse ->
                editAction(yogaCourse)
            },
            onDeleteClick = { courseId ->
                deleteAction(courseId)
            }
        )
        adapter.notifyDataSetChanged()

        binding.recyclerViewCourses.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewCourses.adapter = adapter
    }

    private fun ShowCoursesActivity.editAction(yogaCourse: YogaCourse) {
        val alertDialog = AlertDialog.Builder(this)
        val dialogLayout = LayoutInflater.from(this).inflate(R.layout.edit_course_layout, null)
        alertDialog.setView(dialogLayout)

        // find views
        val spinnerDayOfWeek = dialogLayout.findViewById<Spinner>(R.id.editSpinnerDayOfWeek)
        val txtTimePicker = dialogLayout.findViewById<TextView>(R.id.editTxtTimePicker)
        val txtDatePicker = dialogLayout.findViewById<TextView>(R.id.editTxtDatePicker)
        val edtCapacity = dialogLayout.findViewById<EditText>(R.id.editCapacity)
        val edtDuration = dialogLayout.findViewById<EditText>(R.id.editDuration)
        val edtPrice = dialogLayout.findViewById<EditText>(R.id.editPrice)
        val rdoGroupType = dialogLayout.findViewById<RadioGroup>(R.id.rdoGroupType)
        val edtDescription = dialogLayout.findViewById<EditText>(R.id.editDescription)
        val btnUpdateCourse = dialogLayout.findViewById<Button>(R.id.btnUpdateCourse)

        // --- prefill values from yogaCourse ---
        // Assuming yogaCourse has properties: dayOfWeek, time, date, capacity, duration, price, type, description

        // Spinner (day of week)
        val days =
            listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDayOfWeek.adapter = adapter
        spinnerDayOfWeek.setSelection(days.indexOf(yogaCourse.dayOfWeek))

        txtTimePicker.text = yogaCourse.timeOfCourse
        txtDatePicker.text = yogaCourse.dayOfWeek
        edtCapacity.setText(yogaCourse.capacity.toString())
        edtDuration.setText(yogaCourse.duration.toString())
        edtPrice.setText(yogaCourse.price.toString())
        edtDescription.setText(yogaCourse.description)

        // Radio group (type)
        when (yogaCourse.typeOfClass) {
            "Flow Yoga" -> rdoGroupType.check(R.id.rdoFlowYoga)
            "Aerial Yoga" -> rdoGroupType.check(R.id.rdoAerialYoga)
            "Family Yoga" -> rdoGroupType.check(R.id.rdoFamilyYoga)
        }

        // Setup pickers
        txtTimePicker.setOnClickListener {
            val cal = Calendar.getInstance()
            val timePicker = TimePickerDialog(
                this,
                { _, hourOfDay, minute ->
                    txtTimePicker.text = String.format("%02d:%02d", hourOfDay, minute)
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true
            )
            timePicker.show()
        }

        txtDatePicker.setOnClickListener {
            val cal = Calendar.getInstance()
            val datePicker = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    txtDatePicker.text = "$dayOfMonth/${month + 1}/$year"
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)
            )
            datePicker.show()
        }

        val alert = alertDialog.create()
        alert.show()

        // --- update action ---
        btnUpdateCourse.setOnClickListener {
            val updatedCourse = yogaCourse.copy(
                dayOfWeek = spinnerDayOfWeek.selectedItem.toString(),
                timeOfCourse = txtTimePicker.text.toString(),

                capacity = edtCapacity.text.toString().toIntOrNull() ?: 0,
                duration = edtDuration.text.toString().toIntOrNull() ?: 0,
                price = edtPrice.text.toString().toIntOrNull() ?: 0,
                typeOfClass = when (rdoGroupType.checkedRadioButtonId) {
                    R.id.rdoFlowYoga -> "Flow Yoga"
                    R.id.rdoAerialYoga -> "Aerial Yoga"
                    R.id.rdoFamilyYoga -> "Family Yoga"
                    else -> ""
                },
                description = edtDescription.text.toString()
            )

            val result = dbHelper.editCourse(updatedCourse)
            if (result == 0) {
                Toast.makeText(this, "Updating Error", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Your course is updated", Toast.LENGTH_LONG).show()
            }
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            alert.dismiss()
        }
    }

    private fun ShowCoursesActivity.deleteAction(courseId: Int) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle("Delete Course")
            .setMessage("Are you sure want to delete?")
            .setCancelable(false)
            .setPositiveButton("Yes") { dialog, _ ->
                val result = dbHelper.deleteCourse(courseId)
                if (result == 0) {
                    Toast.makeText(this, "Deletion Error", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this, "Your course is deleted", Toast.LENGTH_LONG).show()
                }
                intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
            .create().show()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.course_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val result = when (item.itemId) {
            R.id.itemResetCourses -> {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("Delete Course")
                    .setMessage("Are you sure want to reset all courses?")
                    .setCancelable(false)
                    .setPositiveButton("Yes") { dialog, _ ->
                        val result = dbHelper.resetCourse()
                        if (result == 0) {
                            Toast.makeText(this, "Deletion Error", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "All course are deleted", Toast.LENGTH_LONG).show()
                        }
                        intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                    .setNegativeButton("No") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .create().show()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
        return result
    }
}



