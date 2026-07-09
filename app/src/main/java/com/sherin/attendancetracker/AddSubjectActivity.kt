package com.sherin.attendancetracker

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.sherin.attendancetracker.model.Subject
class AddSubjectActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    private val database = FirebaseDatabase.getInstance(
        "https://attendancetracker-b6978-default-rtdb.asia-southeast1.firebasedatabase.app"
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_subject)
        auth = FirebaseAuth.getInstance()
        val etSubjectName = findViewById<EditText>(R.id.etSubjectName)
        val etMinimumAttendance = findViewById<EditText>(R.id.etMinimumAttendance)
        val btnSaveSubject = findViewById<Button>(R.id.btnSaveSubject)

        btnSaveSubject.setOnClickListener {

            Toast.makeText(this, "Button Clicked", Toast.LENGTH_SHORT).show()

            val subjectName = etSubjectName.text.toString().trim()
            val minimumAttendance = etMinimumAttendance.text.toString().trim()

            if (subjectName.isEmpty() || minimumAttendance.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please fill all fields",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {

                val currentUser = auth.currentUser
                Toast.makeText(
                    this,
                    "User: ${currentUser?.uid}",
                    Toast.LENGTH_LONG
                ).show()

                if (currentUser == null) {
                    Toast.makeText(this, "User is null", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                val subject = Subject(
                    subjectName = subjectName,
                    minimumAttendance = minimumAttendance.toInt(),
                    attendedClasses = 0,
                    conductedClasses = 0
                )

                Toast.makeText(this, "Trying to save...", Toast.LENGTH_SHORT).show()

                    database.reference
                        .child("Users")
                        .child(currentUser.uid)
                        .child("Subjects")
                        .push()
                        .setValue(subject)
                        .addOnSuccessListener {

                            Toast.makeText(
                                this,
                                "Subject Added Successfully",
                                Toast.LENGTH_SHORT
                            ).show()

                            finish()

                        }
                        .addOnFailureListener {exception->

                            Toast.makeText(
                                this,
                                exception.message,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                }
            }

        }
    }
