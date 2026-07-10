package com.sherin.attendancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ProfileActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    private val database = FirebaseDatabase.getInstance(
        "https://attendancetracker-b6978-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        auth = FirebaseAuth.getInstance()

        val tvUserEmail = findViewById<TextView>(R.id.tvUserEmail)
        val tvTotalSubjects = findViewById<TextView>(R.id.tvTotalSubjects)
        val tvOverallPercentage = findViewById<TextView>(R.id.tvOverallPercentage)
        val btnLogout = findViewById<Button>(R.id.btnLogout)

        val currentUser = auth.currentUser

        if (currentUser != null) {

            tvUserEmail.text = "Email : ${currentUser.email}"

            database.reference
                .child("Users")
                .child(currentUser.uid)
                .child("Subjects")
                .addListenerForSingleValueEvent(object : ValueEventListener {

                    override fun onDataChange(snapshot: DataSnapshot) {

                        var totalPresent = 0
                        var totalConducted = 0

                        val totalSubjects = snapshot.childrenCount

                        for (subjectSnapshot in snapshot.children) {

                            val subject = subjectSnapshot.getValue(
                                com.sherin.attendancetracker.model.Subject::class.java
                            )

                            if (subject != null) {
                                totalPresent += subject.attendedClasses
                                totalConducted += subject.conductedClasses
                            }

                        }

                        val overall =
                            if (totalConducted == 0) {
                                0
                            } else {
                                (totalPresent * 100) / totalConducted
                            }

                        tvTotalSubjects.text = "Total Subjects : $totalSubjects"
                        tvOverallPercentage.text = "Overall Attendance : $overall%"

                    }

                    override fun onCancelled(error: DatabaseError) {
                    }

                })

        }

        btnLogout.setOnClickListener {

            auth.signOut()

            startActivity(Intent(this, LoginActivity::class.java))

            finishAffinity()

        }

    }

}