package com.sherin.attendancetracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SubjectDetailsActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val database = FirebaseDatabase.getInstance(
        "https://attendancetracker-b6978-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject_details)

        auth = FirebaseAuth.getInstance()

        val tvSubjectTitle = findViewById<TextView>(R.id.tvSubjectTitle)
        val tvAttendancePercentage = findViewById<TextView>(R.id.tvAttendancePercentage)
        val tvAttendanceStatus = findViewById<TextView>(R.id.tvAttendanceStatus)
        val tvPresent = findViewById<TextView>(R.id.tvPresent)
        val tvConducted = findViewById<TextView>(R.id.tvConducted)
        val tvMinimumAttendance = findViewById<TextView>(R.id.tvMinimumAttendance)
        val tvSkippableClasses = findViewById<TextView>(R.id.tvSkippableClasses)
        val tvNeedToAttend = findViewById<TextView>(R.id.tvNeedToAttend)

        val btnPresent = findViewById<Button>(R.id.btnPresent)
        val btnAbsent = findViewById<Button>(R.id.btnAbsent)
        val btnCalendar = findViewById<Button>(R.id.btnCalendar)
        val btnDeleteSubject = findViewById<Button>(R.id.btnDeleteSubject)
        val btnResetAttendance = findViewById<Button>(R.id.btnResetAttendance)

        val firebaseKey = intent.getStringExtra("firebaseKey") ?: ""

        val subjectName = intent.getStringExtra("subjectName") ?: ""

        val minimumAttendance =
            intent.getIntExtra("minimumAttendance", 75)

        var attendedClasses =
            intent.getIntExtra("attendedClasses", 0)

        var conductedClasses =
            intent.getIntExtra("conductedClasses", 0)

        tvSubjectTitle.text = subjectName

        tvMinimumAttendance.text =
            "Minimum Attendance : $minimumAttendance%"

        fun updateUI() {

            val percentage =
                if (conductedClasses == 0)
                    0
                else
                    (attendedClasses * 100) / conductedClasses

            tvAttendancePercentage.text =
                "$percentage%"

            tvPresent.text =
                "Present Classes : $attendedClasses"

            tvConducted.text =
                "Conducted Classes : $conductedClasses"


            // Attendance Status
            if (conductedClasses == 0) {

                tvAttendanceStatus.text = "⚪ No Attendance Data"
                tvAttendanceStatus.setTextColor(
                    Color.parseColor("#BDBDBD")
                )

                tvSkippableClasses.text =
                    "📅 No classes have been conducted yet."

                tvNeedToAttend.text =
                    "Attendance will be calculated after your first class."

                return
            }

            when {

                percentage >= 90 -> {

                    tvAttendanceStatus.text =
                        "🟢 Excellent Attendance"

                    tvAttendanceStatus.setTextColor(
                        Color.parseColor("#4CAF50")
                    )

                }

                percentage >= minimumAttendance -> {

                    tvAttendanceStatus.text =
                        "🟡 Good Attendance"

                    tvAttendanceStatus.setTextColor(
                        Color.parseColor("#FFC107")
                    )

                }

                else -> {

                    tvAttendanceStatus.text =
                        "🔴 Low Attendance"

                    tvAttendanceStatus.setTextColor(
                        Color.parseColor("#F44336")
                    )

                }

            }
            // Classes You Can Skip
            var skip = 0

            while (true) {

                val futureAttendance =
                    (attendedClasses * 100.0) /
                            (conductedClasses + skip + 1)

                if (futureAttendance >= minimumAttendance) {

                    skip++

                } else {

                    break

                }

            }
            tvSkippableClasses.text =
                if (skip == 1)
                    "You can skip 1 class."
                else
                    "You can skip $skip classes."
            // Classes Needed
            if (percentage >= minimumAttendance) {

                tvNeedToAttend.text =
                    "🎉 Minimum attendance achieved."

            } else {

                var need = 0

                var p = attendedClasses
                var c = conductedClasses

                while ((p * 100.0) / c < minimumAttendance) {

                    p++
                    c++
                    need++

                }

                tvNeedToAttend.text =
                    "📚 Attend next $need classes to reach $minimumAttendance%."

            }

        }

        updateUI()
        // PRESENT BUTTON
        btnPresent.setOnClickListener {

            val currentUser =
                auth.currentUser ?: return@setOnClickListener

            val today =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date())

            val attendanceRef =
                database.reference
                    .child("Users")
                    .child(currentUser.uid)
                    .child("Subjects")
                    .child(firebaseKey)
                    .child("attendanceHistory")
                    .child(today)

            attendanceRef.get().addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {

                    Toast.makeText(
                        this,
                        "Attendance already marked for today!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    attendedClasses++
                    conductedClasses++

                    database.reference
                        .child("Users")
                        .child(currentUser.uid)
                        .child("Subjects")
                        .child(firebaseKey)
                        .child("attendedClasses")
                        .setValue(attendedClasses)

                    database.reference
                        .child("Users")
                        .child(currentUser.uid)
                        .child("Subjects")
                        .child(firebaseKey)
                        .child("conductedClasses")
                        .setValue(conductedClasses)

                    attendanceRef
                        .setValue("Present")
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Present marked successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            updateUI()

                        }

                }

            }

        }
        // ABSENT BUTTON
        btnAbsent.setOnClickListener {

            val currentUser =
                auth.currentUser ?: return@setOnClickListener

            val today =
                SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date())

            val attendanceRef =
                database.reference
                    .child("Users")
                    .child(currentUser.uid)
                    .child("Subjects")
                    .child(firebaseKey)
                    .child("attendanceHistory")
                    .child(today)

            attendanceRef.get().addOnSuccessListener { snapshot ->

                if (snapshot.exists()) {

                    Toast.makeText(
                        this,
                        "Attendance already marked for today!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    conductedClasses++

                    database.reference
                        .child("Users")
                        .child(currentUser.uid)
                        .child("Subjects")
                        .child(firebaseKey)
                        .child("conductedClasses")
                        .setValue(conductedClasses)

                    attendanceRef
                        .setValue("Absent")
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Absent marked successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            updateUI()

                        }

                }

            }

        }

// ATTENDANCE CALENDAR

        btnCalendar.setOnClickListener {

            val intent = Intent(
                this,
                AttendanceCalendarActivity::class.java
            )

            intent.putExtra("firebaseKey", firebaseKey)
            intent.putExtra("subjectName", subjectName)

            startActivity(intent)

        }
        // DELETE SUBJECT
        btnDeleteSubject.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Delete Subject")
                .setMessage("Are you sure you want to delete this subject?")
                .setPositiveButton("Delete") { _, _ ->

                    val currentUser =
                        auth.currentUser ?: return@setPositiveButton

                    database.reference
                        .child("Users")
                        .child(currentUser.uid)
                        .child("Subjects")
                        .child(firebaseKey)
                        .removeValue()
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Subject deleted successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        }

                }
                .setNegativeButton("Cancel", null)
                .show()

        }
        btnResetAttendance.setOnClickListener {

            androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Reset Attendance")
                .setMessage("This will reset all attendance data for this subject. Continue?")
                .setPositiveButton("Reset") { _, _ ->

                    val currentUser = auth.currentUser ?: return@setPositiveButton

                    val subjectRef = database.reference
                        .child("Users")
                        .child(currentUser.uid)
                        .child("Subjects")
                        .child(firebaseKey)

                    subjectRef.child("attendedClasses").setValue(0)
                    subjectRef.child("conductedClasses").setValue(0)
                    subjectRef.child("attendanceHistory").removeValue()
                        .addOnSuccessListener {

                            attendedClasses = 0
                            conductedClasses = 0

                            updateUI()

                            Toast.makeText(
                                this,
                                "Attendance reset successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                        }

                }
                .setNegativeButton("Cancel", null)
                .show()

        }

    }
}