package com.example.zd7

import android.content.Context
import com.example.zd7.entities.Specialty
import com.example.zd7.entities.Student
import com.example.zd7.entities.Teacher
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.runBlocking

class DatabaseHelper(private val context: Context) {

    companion object {
        private var isDatabaseSeeded = false
    }

    fun seedDatabaseIfNeeded() {
        if (!isDatabaseSeeded) {
            runBlocking {
                seedDatabase()
                isDatabaseSeeded = true
            }
        }
    }

    private suspend fun seedDatabase() {
        val database = AppDatabase.getDatabase(context)

        try {
            // Проверяем, есть ли уже данные
            val specialtiesCount = database.specialtyDao().getAllSpecialties().firstOrNull()?.size ?: 0
            if (specialtiesCount > 0) {
                return // База уже заполнена
            }

            // Добавляем специальности
            val specialties = listOf(
                Specialty(specialtyName = "Информационные системы", isBudgetAvailable = true),
                Specialty(specialtyName = "Программирование", isBudgetAvailable = true),
                Specialty(specialtyName = "Сетевое администрирование", isBudgetAvailable = false)
            )

            specialties.forEach { specialty ->
                database.specialtyDao().insertSpecialty(specialty)
            }

            // Проверяем, есть ли уже администратор
            val existingAdmin = database.teacherDao().getTeacherByEmail("admin@college.ru")
            if (existingAdmin == null) {
                // Добавляем администратора (как преподавателя)
                val admin = Teacher(
                    fullName = "Администратор",
                    email = "admin@college.ru",
                    password = "admin123"
                )
                database.teacherDao().insertTeacher(admin)
            }

            // Проверяем, есть ли уже преподаватель
            val existingTeacher = database.teacherDao().getTeacherByEmail("teacher@college.ru")
            if (existingTeacher == null) {
                // Добавляем преподавателя
                val teacher = Teacher(
                    fullName = "Иванов Иван Иванович",
                    email = "teacher@college.ru",
                    password = "teacher123"
                )
                database.teacherDao().insertTeacher(teacher)
            }

            // Проверяем, есть ли уже студенты
            val existingStudent1 = database.studentDao().getStudentByEmail("student@college.ru")
            if (existingStudent1 == null) {
                // Добавляем студентов
                val student1 = Student(
                    fullName = "Петров Петр Петрович",
                    email = "student@college.ru",
                    password = "student123",
                    birthDate = "15.05.2003",
                    groupId = 1,
                    course = 1,
                    isBudget = true
                )
                database.studentDao().insertStudent(student1)
            }

            val existingStudent2 = database.studentDao().getStudentByEmail("anna@college.ru")
            if (existingStudent2 == null) {
                val student2 = Student(
                    fullName = "Сидорова Анна Сергеевна",
                    email = "anna@college.ru",
                    password = "anna123",
                    birthDate = "20.10.2002",
                    groupId = 2,
                    course = 2,
                    isBudget = false
                )
                database.studentDao().insertStudent(student2)
            }

        } catch (e: Exception) {
            e.printStackTrace()
            // Перебрасываем исключение, чтобы увидеть его в логах
            throw e
        }
    }
}