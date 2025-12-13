package com.example.zd7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var tvWelcome: TextView
    private lateinit var tvStudentCount: TextView
    private lateinit var tvTeacherCount: TextView

    private var statsJob: Job? = null

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

        tvWelcome.text = "Добро пожаловать!\nРежим: $role"
    }

    private fun loadStatistics() {
        statsJob?.cancel()
        statsJob = viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())

            // Используем combine для объединения двух Flow
            combine(
                database.studentDao().getAllStudents(),
                database.teacherDao().getAllTeachers()
            ) { students, teachers ->
                Pair(students, teachers)
            }.collect { (students, teachers) ->
                if (isAdded && view != null) {
                    tvStudentCount.text = "Студентов: ${students.size}"
                    tvTeacherCount.text = "Преподавателей: ${teachers.size}"
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        statsJob?.cancel()
    }
}