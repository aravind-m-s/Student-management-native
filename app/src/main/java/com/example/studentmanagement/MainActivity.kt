package com.example.studentmanagement

import StudentModel
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private lateinit var studentAdapter: StudentAdapter;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        studentAdapter = StudentAdapter(mutableListOf(), this)
        val recyclerElement = findViewById<RecyclerView>(R.id.recyclerview_students_list)
        val addButton = findViewById<FloatingActionButton>(R.id.fab_add_student)
        val noStudentsTextView = findViewById<TextView>(R.id.textview_no_students)


        recyclerElement.adapter = studentAdapter
        recyclerElement.layoutManager = LinearLayoutManager(this)

        studentAdapter.getAllStudents()

        if (studentAdapter.itemCount == 0) {
            noStudentsTextView.visibility = View.VISIBLE
        } else {
            noStudentsTextView.visibility = View.GONE
        }


        addButton.setOnClickListener {
            val intent = Intent(this, AddEditActivity::class.java)
            startActivityForResult(intent, 19)
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 19 && resultCode == Activity.RESULT_OK) {
            studentAdapter.getAllStudents()
            studentAdapter.notifyDataSetChanged()

            val noStudentsTextView = findViewById<TextView>(R.id.textview_no_students)
            if (studentAdapter.itemCount == 0) {
                noStudentsTextView.visibility = View.VISIBLE
            } else {
                noStudentsTextView.visibility = View.GONE
            }
        }
    }

    override fun onResume() {
        super.onResume()
        studentAdapter.getAllStudents()
    }
}