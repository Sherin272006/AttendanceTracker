package com.sherin.attendancetracker.model

data class Subject(

    var subjectName: String = "",

    var minimumAttendance: Int = 0,

    var attendedClasses: Int = 0,

    var conductedClasses: Int = 0

)