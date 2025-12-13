package com.example.zd7.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.zd7.entities.Specialty
import kotlinx.coroutines.flow.Flow

@Dao
interface SpecialtyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSpecialty(specialty: Specialty): Long

    @Update
    suspend fun updateSpecialty(specialty: Specialty)

    @Delete
    suspend fun deleteSpecialty(specialty: Specialty)

    @Query("SELECT * FROM specialties ORDER BY specialty_name")
    fun getAllSpecialties(): Flow<List<Specialty>>
}