package com.sherin.attendancetracker
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.sherin.attendancetracker.model.Subject
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var subjectList: ArrayList<Subject>
    private lateinit var adapter: SubjectAdapter

    private lateinit var auth: FirebaseAuth

    private val database = FirebaseDatabase.getInstance(
        "https://attendancetracker-b6978-default-rtdb.asia-southeast1.firebasedatabase.app"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        auth = FirebaseAuth.getInstance()

        recyclerView = findViewById(R.id.rvSubjects)

        subjectList = ArrayList()

        adapter = SubjectAdapter(subjectList)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        loadSubjects()

        val btnAddSubject = findViewById<Button>(R.id.btnAddSubject)
        btnAddSubject.setOnClickListener {

            val intent = Intent(this, AddSubjectActivity::class.java)
            startActivity(intent)

        }
    }
        override fun onResume() {
            super.onResume()
            loadSubjects()
        }

        private fun loadSubjects() {

            val currentUser = auth.currentUser

            if (currentUser != null) {

                database.reference
                    .child("Users")
                    .child(currentUser.uid)
                    .child("Subjects")
                    .addValueEventListener(object : ValueEventListener {

                        override fun onDataChange(snapshot: DataSnapshot) {

                            subjectList.clear()

                            for (subjectSnapshot in snapshot.children) {

                                val subject = subjectSnapshot.getValue(Subject::class.java)

                                if (subject != null) {
                                    subjectList.add(subject)
                                }

                            }

                            adapter.notifyDataSetChanged()

                        }

                        override fun onCancelled(error: DatabaseError) {

                        }

                    })

            }
        }

}