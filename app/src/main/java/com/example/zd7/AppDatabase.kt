package com.example.zd7

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.zd7.dao.SpecialtyDao
import com.example.zd7.dao.StudentDao
import com.example.zd7.dao.TeacherDao
import com.example.zd7.entities.Specialty
import com.example.zd7.entities.Student
import com.example.zd7.entities.Teacher

@Database(
    entities = [
        Student::class,
        Teacher::class,
        Specialty::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao
    abstract fun specialtyDao(): SpecialtyDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "college_db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}