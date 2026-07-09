package com.sherin.attendancetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class AddSubjectActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject)

        val etSubjectName = findViewById<EditText>(R.id.etSubjectName)
        val etMinimumAttendance = findViewById<EditText>(R.id.etMinimumAttendance)
        val btnSaveSubject = findViewById<Button>(R.id.btnSaveSubject)

        btnSaveSubject.setOnClickListener {

            val subjectName = etSubjectName.text.toString().trim()
            val minimumAttendance = etMinimumAttendance.text.toString().trim()

            if (subjectName.isEmpty() || minimumAttendance.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
            } else {

                Toast.makeText(
                    this,
                    "Subject Added Successfully",
                    Toast.LENGTH_SHORT
                ).show()

            }
        }
    }
}