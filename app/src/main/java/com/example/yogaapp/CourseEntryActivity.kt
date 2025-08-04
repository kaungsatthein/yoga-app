package com.example.yogaapp

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.yogaapp.databinding.ActivityCourseEntryBinding
import java.text.SimpleDateFormat
import java.util.Locale

class CourseEntryActivity : AppCompatActivity() {

    lateinit var binding: ActivityCourseEntryBinding
    private val dayOfWeek = listOf("Select Day of Week...", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    private var selectedDay: String = ""
    private var selectedTime: String = ""
    private var selectedDate: String = ""
    private var description: String = ""
    private var typeOfClass: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCourseEntryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Day picker
        val dayArrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, dayOfWeek)
        dayArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.spinnerDayOfWeek.adapter = dayArrayAdapter

        binding.spinnerDayOfWeek.onItemSelectedListener = object: AdapterView.OnItemSelectedListener{
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
        binding.txtTimePicker.setOnClickListener{
            val timePickerDialog = TimePickerDialog(this, {
              _, hour, min ->
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
        binding.txtDatePicker.setOnClickListener{
            val datePickerDialog = DatePickerDialog(this, {
                    _, y, m, d ->
                val cal = Calendar.getInstance()
                cal.set(y,m,d)
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = dateFormat.format(cal.time)
                binding.txtDatePicker.text = "Date of course: $selectedDate"
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }


        // submit button
        binding.btnSaveCourse.setOnClickListener {

            //Type of class
            val checkedId = binding.rdoGroupType.checkedRadioButtonId
            if(checkedId != 1) {
                val rdoBtn = findViewById<RadioButton>(checkedId)
                typeOfClass = rdoBtn.text.toString()
            }

            description = binding.editDescription.text.toString()
            Toast.makeText(this, "$selectedDay, $selectedTime, $typeOfClass, $description", Toast.LENGTH_LONG).show()
        }
    }
}