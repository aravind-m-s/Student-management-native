package com.example.studentmanagement

import DbHelper
import StudentModel
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.RecyclerView
import java.io.File

class StudentAdapter (private val students: MutableList<StudentModel>, private val context: Context) : RecyclerView.Adapter<StudentAdapter.StudentViewModel>() {

    class StudentViewModel(itemView: View) :RecyclerView.ViewHolder(itemView)

    private val databaseHelper: DbHelper = DbHelper(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewModel {
        return StudentViewModel(
            LayoutInflater.from(parent.context).inflate(
                R.layout.student_card,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return students.size
    }

    fun getAllStudents() {
        students.clear()
        val dbStudents =  databaseHelper.getAllStudents()
        students.addAll(dbStudents)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: StudentViewModel, position: Int) {
        val student = students[position]
        holder.itemView.apply {
            val userImage =findViewById<ImageView>(R.id.imageview_studentImage)
            val userName = findViewById<TextView>(R.id.textview_studentName)
            val userDetails = findViewById<TextView>(R.id.textview_studentDetails)
            val deleteButton = findViewById<ImageView>(R.id.imageview_delete)
            if(student.image.isNotEmpty()){
                userImage.setImageURI(Uri.fromFile(File(student.image)))

            }
            userName.text = student.name
            userDetails.text = "Class: ${student.grade}, Roll No: ${student.rollNumber}"

            deleteButton.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(context)
                alertDialogBuilder.setTitle("Delete Student")
                alertDialogBuilder.setMessage("Are you sure you want to delete this student?")
                alertDialogBuilder.setPositiveButton("OK") { _, _ ->
                    // User clicked OK button
                    val deleted: Boolean = databaseHelper.deleteStudent(student)
                    if (deleted) {
                        students.removeAt(position)
                        notifyItemRemoved(position)
                    }
                }
                alertDialogBuilder.setNegativeButton("Cancel") { dialog, _ ->
                    // User clicked Cancel button
                    dialog.dismiss()
                }

                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }


            setOnClickListener{
                val intent = Intent(context, AddEditActivity::class.java)
                intent.putExtra("student", student)
                context.startActivity(intent)
            }

        }
    };
}