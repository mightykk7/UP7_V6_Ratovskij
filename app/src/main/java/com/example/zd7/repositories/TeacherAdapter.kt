package com.example.zd7.repositories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7.entities.Teacher

class TeacherAdapter(
    private val onEdit: (Teacher) -> Unit,
    private val onDelete: (Teacher) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    private var teachers: List<Teacher> = emptyList()

    fun updateList(newList: List<Teacher>) {
        teachers = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(teachers[position])
        holder.itemView.setOnClickListener {
            onEdit.invoke(teachers[position])
        }
        holder.itemView.setOnLongClickListener {
            onDelete.invoke(teachers[position])
            true
        }
    }

    override fun getItemCount(): Int = teachers.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(android.R.id.text1)
        private val email: TextView = itemView.findViewById(android.R.id.text2)

        fun bind(teacher: Teacher) {
            name.text = teacher.fullName
            email.text = teacher.email
        }
    }
}