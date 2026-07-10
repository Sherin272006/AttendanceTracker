package com.sherin.attendancetracker

import android.widget.Button
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

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
        val tvPresent = findViewById<TextView>(R.id.tvPresent)
        val tvConducted = findViewById<TextView>(R.id.tvConducted)
        val tvMinimumAttendance = findViewById<TextView>(R.id.tvMinimumAttendance)
        val tvSkippableClasses = findViewById<TextView>(R.id.tvSkippableClasses)
        val btnPresent = findViewById<Button>(R.id.btnPresent)
        val btnAbsent = findViewById<Button>(R.id.btnAbsent)
        val firebaseKey = intent.getStringExtra("firebaseKey") ?: ""
        val minimumAttendance = intent.getIntExtra("minimumAttendance", 75)
        val subjectName = intent.getStringExtra("subjectName") ?: ""
        val attendedClasses = intent.getIntExtra("attendedClasses", 0)
        val conductedClasses = intent.getIntExtra("conductedClasses", 0)

        tvSubjectTitle.text = subjectName
        tvPresent.text = "Present Classes : $attendedClasses"
        tvConducted.text = "Conducted Classes : $conductedClasses"
        tvMinimumAttendance.text = "Minimum Attendance : $minimumAttendance%"

        val percentage =
            if (conductedClasses == 0) {
                0
            } else {
                (attendedClasses * 100) / conductedClasses
            }

        tvAttendancePercentage.text = "$percentage%"

        var skippableClasses = 0

        while (true) {

            val futureAttendance =
                (attendedClasses * 100.0) / (conductedClasses + skippableClasses + 1)

            if (futureAttendance >= minimumAttendance) {
                skippableClasses++
            } else {
                break
            }

        }

        tvSkippableClasses.text =
            if (skippableClasses == 1) {
                "You can skip 1 class."
            } else {
                "You can skip $skippableClasses classes."
            }

        btnPresent.setOnClickListener {

            val currentUser = auth.currentUser ?: return@setOnClickListener

            val newPresent = attendedClasses + 1
            val newConducted = conductedClasses + 1

            database.reference
                .child("Users")
                .child(currentUser.uid)
                .child("Subjects")
                .child(firebaseKey)
                .child("attendedClasses")
                .setValue(newPresent)

            database.reference
                .child("Users")
                .child(currentUser.uid)
                .child("Subjects")
                .child(firebaseKey)
                .child("conductedClasses")
                .setValue(newConducted)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Attendance Updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                }

        }
        btnAbsent.setOnClickListener {

            val currentUser = auth.currentUser ?: return@setOnClickListener

            val newConducted = conductedClasses + 1

            database.reference
                .child("Users")
                .child(currentUser.uid)
                .child("Subjects")
                .child(firebaseKey)
                .child("conductedClasses")
                .setValue(newConducted)
                .addOnSuccessListener {

                    Toast.makeText(
                        this,
                        "Attendance Updated",
                        Toast.LENGTH_SHORT
                    ).show()

                    finish()

                }

        }

    }
}