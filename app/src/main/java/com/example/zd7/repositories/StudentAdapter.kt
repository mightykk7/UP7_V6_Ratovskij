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

    class StudentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvName: TextView = itemView.findViewById(R.id.tvStudentName)
        private val tvGroup: TextView = itemView.findViewById(R.id.tvStudentGroup)
        private val tvCourse: TextView = itemView.findViewById(R.id.tvStudentCourse)
        private val ivPhoto: ImageView = itemView.findViewById(R.id.ivStudentPhoto)

        fun bind(student: Student) {
            tvName.text = student.fullName
            tvGroup.text = "Группа: ${student.groupId}"
            tvCourse.text = "Курс: ${student.course}"

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