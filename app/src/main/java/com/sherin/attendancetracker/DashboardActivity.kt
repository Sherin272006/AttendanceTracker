package com.sherin.attendancetracker

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val btnAddSubject = findViewById<Button>(R.id.btnAddSubject)

        btnAddSubject.setOnClickListener {

            val intent = Intent(this, AddSubjectActivity::class.java)
            startActivity(intent)

        }
    }
}