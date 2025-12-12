package com.example.zd7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7.repositories.StudentAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class StudentsFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddStudent: FloatingActionButton
    private lateinit var etSearch: EditText
    private lateinit var btnSearch: Button
    private lateinit var adapter: StudentAdapter

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

        setupRecyclerView()
        setupListeners()
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
            showSnackbar("Добавление студента")
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

    private fun loadStudents() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.studentDao().getAllStudents().collect { students ->
                adapter.updateList(students)
            }
        }
    }

    private fun searchStudents(query: String) {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.studentDao().searchStudentsByName(query).collect { students ->
                adapter.updateList(students)
                showSnackbar("Найдено студентов: ${students.size}")
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}