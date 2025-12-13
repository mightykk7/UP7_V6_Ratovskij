package com.example.zd7

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7.repositories.StudentAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class StudentsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddStudent: FloatingActionButton
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var adapter: StudentAdapter
    private lateinit var sharedPreferences: SharedPreferences

    private var loadJob: Job? = null
    private var searchJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_students, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerView)
        fabAddStudent = view.findViewById(R.id.fabAddStudent)
        etSearch = view.findViewById(R.id.etSearch)
        btnSearch = view.findViewById(R.id.btnSearch)

        sharedPreferences = requireContext().getSharedPreferences("auth", android.content.Context.MODE_PRIVATE)

        setupRecyclerView()
        setupListeners()
        checkUserRole() // Проверяем роль пользователя
        loadStudents()
    }

    private fun setupRecyclerView() {
        adapter = StudentAdapter { student ->
            showSnackbar("Выбран студент: ${student.fullName}")
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAddStudent.setOnClickListener {
            if (canManageStudents()) {
                try {
                    findNavController().navigate(R.id.action_studentsFragment_to_addStudentFragment)
                } catch (e: Exception) {
                    showSnackbar("Ошибка навигации: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                showSnackbar("Только приемная комиссия может добавлять студентов")
            }
        }

        btnSearch.setOnClickListener {
            val query = etSearch.text.toString().trim()
            if (query.isNotEmpty()) {
                searchStudents(query)
            } else {
                loadStudents()
            }
        }
    }

    private fun checkUserRole() {
        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""

        // Скрываем FAB если пользователь не Приемная комиссия
        if (currentUserRole != "Приемная комиссия") {
            fabAddStudent.visibility = View.GONE
        }

        // Для студентов скрываем поиск если они студенты
        if (currentUserRole == "Студент") {
            etSearch.visibility = View.GONE
            btnSearch.visibility = View.GONE
        }
    }

    private fun canManageStudents(): Boolean {
        val currentUserRole = sharedPreferences.getString("user_role", "") ?: ""
        return currentUserRole == "Приемная комиссия"
    }

    private fun loadStudents() {
        loadJob?.cancel()
        loadJob = viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.studentDao().getAllStudents().collect { students ->
                adapter.updateList(students)
            }
        }
    }

    private fun searchStudents(query: String) {
        searchJob?.cancel()
        searchJob = viewLifecycleOwner.lifecycleScope.launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())
                database.studentDao().searchStudentsByName(query).collect { students ->
                    adapter.updateList(students)
                    showSnackbar("Найдено студентов: ${students.size}")
                }
            } catch (e: Exception) {
                showSnackbar("Ошибка поиска: ${e.message}")
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
        searchJob?.cancel()
    }
}