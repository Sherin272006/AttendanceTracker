package com.sherin.attendancetracker

import android.os.Bundle
import android.widget.CalendarView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class AttendanceCalendarActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val database = FirebaseDatabase.getInstance(
        "https://attendancetracker-b6978-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attendance_calendar)

        auth = FirebaseAuth.getInstance()

        val calendarView = findViewById<CalendarView>(R.id.calendarView)
        val tvSelectedDate = findViewById<TextView>(R.id.tvSelectedDate)
        val tvAttendanceStatus = findViewById<TextView>(R.id.tvAttendanceStatus)

        val firebaseKey = intent.getStringExtra("firebaseKey") ?: ""

        calendarView.setOnDateChangeListener { _, year, month, day ->

            val date =
                String.format(
                    "%04d-%02d-%02d",
                    year,
                    month + 1,
                    day
                )

            tvSelectedDate.text = "Selected Date : $date"

            val currentUser = auth.currentUser ?: return@setOnDateChangeListener

            database.reference
                .child("Users")
                .child(currentUser.uid)
                .child("Subjects")
                .child(firebaseKey)
                .child("attendanceHistory")
                .child(date)
                .get()
                .addOnSuccessListener {

                    if (it.exists()) {

                        val status = it.value.toString()

                        if (status == "Present") {

                            tvAttendanceStatus.text =
                                "🟢 Present"

                        } else {

                            tvAttendanceStatus.text =
                                "🔴 Absent"

                        }

                    } else {

                        tvAttendanceStatus.text =
                            "No Attendance Record"

                    }

                }

        }

    }

}