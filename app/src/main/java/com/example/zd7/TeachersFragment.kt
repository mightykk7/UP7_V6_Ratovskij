package com.example.zd7

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7.entities.Teacher
import com.example.zd7.repositories.TeacherAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class TeachersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTeacher: FloatingActionButton
    private lateinit var adapter: TeacherAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private var loadJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_teachers, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        fabAddTeacher = view.findViewById(R.id.fabAddTeacher)

        sharedPreferences = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)

        setupRecyclerView()
        setupListeners()
        checkUserRole() // Проверяем роль пользователя
        loadTeachers()
    }

    private fun setupRecyclerView() {
        adapter = TeacherAdapter(
            onEdit = { teacher ->
                showSnackbar("Редактировать преподавателя: ${teacher.fullName}")
            },
            onDelete = { teacher ->
                // Проверяем права на удаление
                if (canManageTeachers()) {
                    deleteTeacher(teacher)
                } else {
                    showSnackbar("У вас нет прав для удаления преподавателей")
                }
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAddTeacher.setOnClickListener {
            if (canManageTeachers()) {
                try {
                    val actionId = R.id.action_teachersFragment_to_addTeacherFragment
                    findNavController().navigate(actionId)
                } catch (e: IllegalArgumentException) {
                    showSnackbar("Функция добавления преподавателя не настроена")
                    e.printStackTrace()
                } catch (e: Exception) {
                    showSnackbar("Ошибка: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                showSnackbar("Только приемная комиссия может добавлять преподавателей")
            }
        }
    }

    private fun checkUserRole() {
        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""

        // Скрываем FAB если пользователь не Приемная комиссия
        if (currentUserRole != "Приемная комиссия") {
            fabAddTeacher.visibility = View.GONE

            // Также меняем поведение адаптера
            adapter = TeacherAdapter(
                onEdit = { teacher ->
                    // Для обычных учителей показываем только информацию
                    showSnackbar("Преподаватель: ${teacher.fullName}")
                },
                onDelete = { teacher ->
                    // Запрещаем удаление
                    showSnackbar("У вас нет прав для удаления преподавателей")
                }
            )
            recyclerView.adapter = adapter
        }
    }

    private fun canManageTeachers(): Boolean {
        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""
        return currentUserRole == "Приемная комиссия"
    }

    private fun loadTeachers() {
        loadJob?.cancel()
        loadJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())
                database.teacherDao().getAllTeachers().collect { teachers ->
                    adapter.updateList(teachers)
                }
            } catch (e: Exception) {
                showSnackbar("Ошибка загрузки преподавателей: ${e.message}")
            }
        }
    }

    private fun deleteTeacher(teacher: Teacher) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())
                database.teacherDao().deleteTeacher(teacher)
                showSnackbar("Преподаватель удален")
                loadTeachers() // Перезагружаем список
            } catch (e: Exception) {
                showSnackbar("Ошибка удаления: ${e.message}")
            }
        }
    }

    private fun showSnackbar(message: String) {
        if (isAdded && view != null) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadJob?.cancel()
    }
}