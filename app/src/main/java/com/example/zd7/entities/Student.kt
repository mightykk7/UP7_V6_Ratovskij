package com.example.zd7.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "students")
data class Student(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "student_id")
    val studentId: Int = 0,

    @ColumnInfo(name = "full_name")
    val fullName: String,

    @ColumnInfo(name = "photo_url")
    val photoUrl: String? = null,

    @ColumnInfo(name = "birth_date")
    val birthDate: String,

    @ColumnInfo(name = "group_id")
    val groupId: Int,

    val course: Int,

    @ColumnInfo(name = "is_budget")
    val isBudget: Boolean,

    @ColumnInfo(name = "email")
    val email: String,

    @ColumnInfo(name = "password")
    val password: String
)