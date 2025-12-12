package com.example.zd7

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etEmail: EditText
    private lateinit var etPassword: EditText
    private lateinit var spinnerRole: Spinner
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: TextView

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var database: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Проверка авторизации
        sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("is_logged_in", false)) {
            startMainActivity()
            return
        }

        // Инициализация views
        etEmail = findViewById(R.id.etEmail)
        etPassword = findViewById(R.id.etPassword)
        spinnerRole = findViewById(R.id.spinnerRole)
        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        // Инициализация базы данных
        database = AppDatabase.getDatabase(this)
        DatabaseHelper(this).seedDatabaseIfNeeded()

        setupUI()
    }

    private fun setupUI() {
        // Настройка выбора роли
        val roles = arrayOf("Приемная комиссия", "Преподаватель", "Студент")
        spinnerRole.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            roles
        )

        // Обработчик кнопки входа
        btnLogin.setOnClickListener {
            val email = etEmail.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val role = spinnerRole.selectedItem.toString()

            if (validateInput(email, password)) {
                loginUser(email, password, role)
            }
        }

        // Кнопка регистрации (только информационная)
        btnRegister.setOnClickListener {
            showSnackbar("Регистрация новых пользователей доступна только администратору")
        }

        // Заполняем тестовые данные для удобства
        fillTestData()
    }

    private fun fillTestData() {
        // Для тестирования заполняем данные
        etEmail.setText("admin@college.ru")
        etPassword.setText("admin123")
        spinnerRole.setSelection(0)
    }

    private fun validateInput(email: String, password: String): Boolean {
        var isValid = true

        if (email.isEmpty()) {
            etEmail.error = "Введите email"
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.error = "Неверный формат email"
            isValid = false
        }

        if (password.isEmpty()) {
            etPassword.error = "Введите пароль"
            isValid = false
        } else if (password.length < 6) {
            etPassword.error = "Минимум 6 символов"
            isValid = false
        }

        return isValid
    }

    private fun loginUser(email: String, password: String, role: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                when (role) {
                    "Приемная комиссия" -> {
                        // Администратор - это специальный преподаватель
                        val teacher = database.teacherDao().getTeacherByEmail(email)
                        if (teacher != null && teacher.password == password) {
                            if (email == "admin@college.ru") {
                                saveLoginData(email, role, teacher.teacherId)
                                runOnUiThread {
                                    startMainActivity()
                                }
                            } else {
                                runOnUiThread {
                                    showSnackbar("Недостаточно прав для входа как администратор")
                                }
                            }
                        } else {
                            runOnUiThread {
                                showSnackbar("Неверные данные")
                            }
                        }
                    }
                    "Преподаватель" -> {
                        val teacher = database.teacherDao().getTeacherByEmail(email)
                        if (teacher != null && teacher.password == password) {
                            // Проверяем, не пытается ли администратор войти как обычный преподаватель
                            if (email != "admin@college.ru") {
                                saveLoginData(email, role, teacher.teacherId)
                                runOnUiThread {
                                    startMainActivity()
                                }
                            } else {
                                runOnUiThread {
                                    showSnackbar("Администратор должен войти как 'Приемная комиссия'")
                                }
                            }
                        } else {
                            runOnUiThread {
                                showSnackbar("Неверные данные")
                            }
                        }
                    }
                    "Студент" -> {
                        val student = database.studentDao().getStudentByEmail(email)
                        if (student != null && student.password == password) {
                            saveLoginData(email, role, student.studentId)
                            runOnUiThread {
                                startMainActivity()
                            }
                        } else {
                            runOnUiThread {
                                showSnackbar("Неверные данные")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    showSnackbar("Ошибка: ${e.message}")
                }
            }
        }
    }

    private fun saveLoginData(email: String, role: String, userId: Int) {
        val editor = sharedPreferences.edit()
        editor.putString("user_email", email)
        editor.putString("user_role", role)
        editor.putInt("user_id", userId)
        editor.putBoolean("is_logged_in", true)
        editor.apply()
    }

    private fun startMainActivity() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(btnLogin, message, Snackbar.LENGTH_LONG)
            .setAction("OK") { }
            .show()
    }
}