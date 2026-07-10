package com.sherin.attendancetracker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sherin.attendancetracker.model.Subject

class SubjectAdapter(
    private val subjectList: ArrayList<Subject>
) : RecyclerView.Adapter<SubjectAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvSubjectName: TextView = itemView.findViewById(R.id.tvSubjectName)
        val tvAttendance: TextView = itemView.findViewById(R.id.tvAttendance)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {

        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_subject, parent, false)

        return SubjectViewHolder(view)

    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {

        val subject = subjectList[position]

        holder.tvSubjectName.text = subject.subjectName

        holder.tvAttendance.text =
            "${subject.attendedClasses}/${subject.conductedClasses}"

    }

    override fun getItemCount(): Int {

        return subjectList.size

    }
}