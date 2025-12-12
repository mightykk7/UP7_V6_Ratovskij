package com.example.zd7.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "teachers")
data class Teacher(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "teacher_id")
    val teacherId: Int = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "max_hours")
    val maxHours: Int = 1440,

    @ColumnInfo(name = "hours_worked")
    val hoursWorked: Int = 0,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password")
    val password: String
)