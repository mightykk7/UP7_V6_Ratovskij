package com.example.zd7.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.zd7.entities.Student
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStudent(student: Student): Long

    @Update
    suspend fun updateStudent(student: Student)

    @Delete
    suspend fun deleteStudent(student: Student)

    @Query("SELECT * FROM students ORDER BY full_name")
    fun getAllStudents(): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE student_id = :studentId")
    suspend fun getStudentById(studentId: Int): Student?

    @Query("SELECT * FROM students WHERE email = :email")
    suspend fun getStudentByEmail(email: String): Student?

    @Query("SELECT * FROM students WHERE group_id = :groupId ORDER BY full_name")
    fun getStudentsByGroup(groupId: Int): Flow<List<Student>>

    @Query("SELECT * FROM students WHERE full_name LIKE '%' || :query || '%' ORDER BY full_name")
    fun searchStudentsByName(query: String): Flow<List<Student>>

    @Query("SELECT COUNT(*) FROM students WHERE is_budget = 1")
    fun getBudgetStudentsCount(): Flow<Int>
}