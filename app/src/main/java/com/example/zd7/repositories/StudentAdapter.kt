package com.example.zd7.repositories

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zd7.R
import com.example.zd7.entities.Student
import com.squareup.picasso.Picasso

class StudentAdapter(private val onItemClick: (Student) -> Unit) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    private var students: List<Student> = emptyList()
    // Список названий групп для отображения
    private val groupNames = listOf("ПР", "ПГС", "БД", "ДЗ")

    fun updateList(newList: List<Student>) {
        students = newList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_student, parent, false)
        return StudentViewHolder(view)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        holder.bind(students[position])
        holder.itemView.setOnClickListener {
            onItemClick.invoke(students[position])
        }
    }

    override fun getItemCount(): Int = students.size

    inner class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvGroup: TextView = itemView.findViewById(R.id.tvStudentGroup)
        private val tvCourse: TextView = itemView.findViewById(R.id.tvStudentCourse)
        private val tvBudget: TextView = itemView.findViewById(R.id.tvStudentBudget) // Добавляем TextView для статуса бюджета
        private val ivPhoto: ImageView = itemView.findViewById(R.id.ivStudentPhoto)

        fun bind(student: Student) {
            tvName.text = student.fullName

            // Отображаем название группы вместо номера
            val groupIndex = student.groupId - 1 // groupId начинается с 1
            val groupName = if (groupIndex in groupNames.indices) {
                groupNames[groupIndex]
            } else {
                "Группа ${student.groupId}"
            }
            tvGroup.text = "Группа: $groupName"

            tvCourse.text = "Курс: ${student.course}"

            // Отображаем бюджет/внебюджет
            if (student.isBudget) {
                tvBudget.text = "Бюджет"
                tvBudget.setBackgroundColor(itemView.context.getColor(android.R.color.holo_green_dark))
            } else {
                tvBudget.text = "Внебюджет"
                tvBudget.setBackgroundColor(itemView.context.getColor(android.R.color.holo_red_dark))
            }
            tvBudget.visibility = View.VISIBLE

            // Загрузка фото
            if (!student.photoUrl.isNullOrEmpty()) {
                Picasso.get()
                    .load(student.photoUrl)
                    .placeholder(R.drawable.ic_person)
                    .error(R.drawable.ic_error)
                    .into(ivPhoto)
            } else {
                ivPhoto.setImageResource(R.drawable.ic_person)
            }
        }
    }
}