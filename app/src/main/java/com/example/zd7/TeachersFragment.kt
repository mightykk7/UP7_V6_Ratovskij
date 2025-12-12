package com.example.zd7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7.entities.Teacher
import com.example.zd7.repositories.TeacherAdapter
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch

class TeachersFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var fabAddTeacher: FloatingActionButton
    private lateinit var adapter: TeacherAdapter

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

        setupRecyclerView()
        setupListeners()
        loadTeachers()
    }

    private fun setupRecyclerView() {
        adapter = TeacherAdapter(
            onEdit = { teacher ->
                showSnackbar("Редактировать преподавателя: ${teacher.fullName}")
            },
            onDelete = { teacher ->
                showSnackbar("Удалить преподавателя: ${teacher.fullName}")
                deleteTeacher(teacher)
            }
        )
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupListeners() {
        fabAddTeacher.setOnClickListener {
            showSnackbar("Добавление преподавателя")
        }
    }

    private fun loadTeachers() {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.teacherDao().getAllTeachers().collect { teachers ->
                adapter.submitList(teachers)
            }
        }
    }

    private fun deleteTeacher(teacher: Teacher) {
        lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.teacherDao().deleteTeacher(teacher)
            showSnackbar("Преподаватель удален")
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}