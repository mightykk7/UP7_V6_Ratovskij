package com.example.zd7.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "specialties")
data class Specialty(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "specialty_id")
    val specialtyId: Int = 0,

    @ColumnInfo(name = "specialty_name")
    val specialtyName: String,

    @ColumnInfo(name = "is_budget")
    val isBudgetAvailable: Boolean
)