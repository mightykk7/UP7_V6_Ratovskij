package com.example.zd7

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.Spinner
import androidx.fragment.app.Fragment
import com.example.zd7.entities.Student
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class AddStudentFragment : Fragment() {

    private lateinit var etFullName: EditText
    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var spGroup: Spinner
    private lateinit var spCourse: Spinner
    private lateinit var rbBudget: RadioButton
    private lateinit var rbNonBudget: RadioButton
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_student, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        etFullName = view.findViewById(R.id.etFullName)
        etEmail = view.findViewById(R.id.etEmail)
        etPassword = view.findViewById(R.id.etPassword)
        etBirthDate = view.findViewById(R.id.etBirthDate)
        spGroup = view.findViewById(R.id.spGroup)
        spCourse = view.findViewById(R.id.spCourse)
        rbBudget = view.findViewById(R.id.rbBudget)
        rbNonBudget = view.findViewById(R.id.rbNonBudget)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)

        setupDatePicker()
        setupSpinners()
        setupListeners()
    }

    private fun setupDatePicker() {
        etBirthDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, selectedYear, selectedMonth, selectedDay ->
                    val formattedDate = String.format(
                        "%02d.%02d.%04d",
                        selectedDay,
                        selectedMonth + 1,
                        selectedYear
                    )
                    etBirthDate.setText(formattedDate)
                },
                year,
                month,
                day
            )

            datePicker.datePicker.maxDate = System.currentTimeMillis()
            datePicker.show()
        }
    }

    private fun setupSpinners() {
        val groups = listOf("ИСП-101", "ИСП-102", "ПРОГ-201", "СЕТИ-301")
        val groupAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            groups
        )
        groupAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spGroup.adapter = groupAdapter

        val courses = listOf("1", "2", "3", "4")
        val courseAdapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            courses
        )
        courseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spCourse.adapter = courseAdapter
    }

    private fun setupListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveStudent()
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

        if (etBirthDate.text.toString().trim().isEmpty()) {
            etBirthDate.error = "Выберите дату рождения"
            isValid = false
        }

        return isValid
    }

    private fun saveStudent() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val database = AppDatabase.getDatabase(requireContext())

                val student = Student(
                    fullName = etFullName.text.toString().trim(),
                    email = etEmail.text.toString().trim(),
                    password = etPassword.text.toString().trim(),
                    birthDate = etBirthDate.text.toString().trim(),
                    groupId = spGroup.selectedItemPosition + 1,
                    course = spCourse.selectedItem.toString().toInt(),
                    isBudget = rbBudget.isChecked
                )

                val id = database.studentDao().insertStudent(student)

                withContext(Dispatchers.Main) {
                    showSnackbar("Студент добавлен с ID: $id")
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