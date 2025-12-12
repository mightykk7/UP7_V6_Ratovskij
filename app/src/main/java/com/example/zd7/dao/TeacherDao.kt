package com.example.zd7.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.zd7.entities.Teacher
import kotlinx.coroutines.flow.Flow

@Dao
interface TeacherDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTeacher(teacher: Teacher): Long

    @Update
    suspend fun updateTeacher(teacher: Teacher)

    @Delete
    suspend fun deleteTeacher(teacher: Teacher)

    @Query("SELECT * FROM teachers ORDER BY full_name")
    fun getAllTeachers(): Flow<List<Teacher>>

    @Query("SELECT * FROM teachers WHERE teacher_id = :teacherId")
    suspend fun getTeacherById(teacherId: Int): Teacher?

    @Query("SELECT * FROM teachers WHERE email = :email")
    suspend fun getTeacherByEmail(email: String): Teacher?

    @Query("SELECT * FROM teachers WHERE hours_worked > max_hours")
    fun getOverworkedTeachers(): Flow<List<Teacher>>
}