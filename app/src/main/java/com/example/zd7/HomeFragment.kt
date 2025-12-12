package com.example.zd7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvStudentCount: TextView
    private lateinit var tvTeacherCount: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvWelcome = view.findViewById(R.id.tvWelcome)
        tvStudentCount = view.findViewById(R.id.tvStudentCount)
        tvTeacherCount = view.findViewById(R.id.tvTeacherCount)

        setupWelcomeMessage()
        loadStatistics()
    }

    private fun setupWelcomeMessage() {
        val sharedPreferences = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)
        val email = sharedPreferences.getString("user_email", "")
        val role = sharedPreferences.getString("user_role", "")

        tvWelcome.text = "Добро пожаловать, $email!\nРежим: $role"
    }

    private fun loadStatistics() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())

            database.studentDao().getAllStudents().collect { students ->
                tvStudentCount.text = "Студентов: ${students.size}"
            }

            database.teacherDao().getAllTeachers().collect { teachers ->
                tvTeacherCount.text = "Преподавателей: ${teachers.size}"
            }
        }
    }
}