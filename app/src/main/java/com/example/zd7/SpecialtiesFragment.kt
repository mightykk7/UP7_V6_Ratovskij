package com.example.zd7

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class SpecialtiesFragment : Fragment() {

    // Если у вас есть RecyclerView для специальностей
    private lateinit var recyclerView: RecyclerView
    private var loadJob: Job? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Замените R.layout.fragment_specialties на ваш реальный макет
        return inflater.inflate(R.layout.fragment_specialties, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Инициализируйте RecyclerView если он есть
        // recyclerView = view.findViewById(R.id.recyclerView)
        // recyclerView.layoutManager = LinearLayoutManager(requireContext())

        loadSpecialties()
    }

    private fun loadSpecialties() {
        loadJob?.cancel()
        loadJob = viewLifecycleOwner.lifecycleScope.launch {
            val database = AppDatabase.getDatabase(requireContext())
            database.specialtyDao().getAllSpecialties().collect { specialties ->
                // Обновите адаптер RecyclerView данными из списка 'specialties'
                // adapter.updateList(specialties)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        loadJob?.cancel()
    }
}