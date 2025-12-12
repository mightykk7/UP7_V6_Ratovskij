package com.example.zd7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.zd7.entities.Teacher
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddTeacherFragment : Fragment() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_teacher, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFullName = view.findViewById(R.id.etFullName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        setupListeners()
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveTeacher()
            }
        }

        btnCancel.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private fun validateInput(): Boolean {
        var isValid = true

        if (etFullName.text.toString().trim().isEmpty()) {
            etFullName.error = "Введите ФИО"
            isValid = false
        }

        if (etEmail.text.toString().trim().isEmpty()) {
            etEmail.error = "Введите email"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.text.toString()).matches()) {
            etEmail.error = "Неверный формат email"
            isValid = false
        }

        if (etPassword.text.toString().trim().isEmpty()) {
            etPassword.error = "Введите пароль"
            isValid = false
        } else if (etPassword.text.toString().length < 6) {
            etPassword.error = "Минимум 6 символов"
            isValid = false
        }

        return isValid
    }

    private fun saveTeacher() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())

                val teacher = Teacher(
                    fullName = etFullName.text.toString().trim(),
                    email = etEmail.text.toString().trim(),
                    password = etPassword.text.toString().trim()
                )

                val id = database.teacherDao().insertTeacher(teacher)

                withContext(Dispatchers.Main) {
                    showSnackbar("Преподаватель добавлен с ID: $id")
                    requireActivity().supportFragmentManager.popBackStack()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showSnackbar("Ошибка: ${e.message}")
                }
            }
        }
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
    }
}