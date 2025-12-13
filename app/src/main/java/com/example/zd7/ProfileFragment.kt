package com.example.zd7

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

class ProfileFragment : Fragment() {

    private lateinit var tvEmail: TextView
    private lateinit var tvRole: TextView
    private lateinit var btnLogout: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvEmail = view.findViewById(R.id.tvEmail)
        tvRole = view.findViewById(R.id.tvRole)
        btnLogout = view.findViewById(R.id.btnLogout)

        sharedPreferences = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)

        loadProfileData()
        setupListeners()
    }

    private fun loadProfileData() {
        val email = sharedPreferences.getString("user_email", "")
        val role = sharedPreferences.getString("user_role", "")
        val userId = sharedPreferences.getInt("user_id", 0)

        if (isAdded) { // Проверяем, что фрагмент прикреплен
            tvEmail.text = "Email: $email"
            tvRole.text = "Роль: $role\nID пользователя: $userId"
        }
    }

    private fun setupListeners() {
        btnLogout.setOnClickListener {
            logout()
        }
    }

    private fun logout() {
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()

        if (isAdded) {
            showSnackbar("Вы вышли из системы")

            // Запускаем LoginActivity и закрываем текущую
            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        // Не вызываем requireActivity().finish() здесь, так как это может вызвать проблемы
        // с жизненным циклом фрагмента
    }

    private fun showSnackbar(message: String) {
        if (isAdded && view != null) {
            Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG).show()
        }
    }
}