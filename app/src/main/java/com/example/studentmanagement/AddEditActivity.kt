package com.example.studentmanagement


import DbHelper
import StudentModel
import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddEditActivity : AppCompatActivity() {

    private var image: String = ""
    private lateinit var studentImage: ImageView;
    private val cameraRequestId = 1222


    private fun handleImageResult(data: Intent?) {
        val imageBitmap: Bitmap? = data?.extras?.getParcelable("data")

        imageBitmap?.let {
            image = saveImageToCache(it)

            studentImage.setImageBitmap(it)



        } ?: run {
            Log.w("MainActivity", "Failed to retrieve image data.")
        }
    }

    private fun saveImageToCache(bitmap: Bitmap): String {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "IMG_$timeStamp.jpg"
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val imageFile = File(storageDir, fileName)
        try {
            FileOutputStream(imageFile).use { out ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            }
        } catch (e: Exception) {
            Log.e("MainActivity", "Error saving image to cache: ${e.message}")
        }
        return imageFile.absolutePath
    }

    private val takePictureLauncher =  registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            handleImageResult(data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_edit_students)

        val studentName = findViewById<EditText>(R.id.editText_name)
        val studentClass = findViewById<EditText>(R.id.editText_class)
        val studentRollNo = findViewById<EditText>(R.id.editText_rollNo)
        val addButton = findViewById<Button>(R.id.button_add_student)
        studentImage = findViewById<ImageView>(R.id.imageview_add_edit_image)
        val toolbar = findViewById<Toolbar>(R.id.toolbar_add_edit)
        val backButton: AppCompatImageView = findViewById(R.id.backButton)
        val toolbarTitle = findViewById<TextView>(R.id.title_toolbar)

        setSupportActionBar(toolbar)

        backButton.setOnClickListener {
            finish()
        }


        val intent = intent
        val student: StudentModel? = intent.getSerializableExtra("student") as? StudentModel
        if(student != null){
            if(student.image.trim().isNotEmpty()){
                image = student.image
            studentImage.setImageURI(Uri.fromFile(File(student.image)))
            }
            studentName.setText(student.name)
            studentClass.setText(student.grade)
            studentRollNo.setText(student.rollNumber)
            addButton.text = "Update Student"
            toolbarTitle.text = "Update Student"
        }

        studentImage.setOnClickListener {
            if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED){
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    cameraRequestId,
                )
            }
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            takePictureLauncher.launch(cameraIntent)
        }

        addButton.setOnClickListener {
            val name = studentName.text.toString().trim()
            val grade = studentClass.text.toString().trim()
            val rollNo = studentRollNo.text.toString().trim()

            if (name.isEmpty() || grade.isEmpty() || rollNo.isEmpty()) {
                val snackbar = Snackbar.make(
                    addButton,
                    "Please fill in all fields",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.show()
                return@setOnClickListener
            }

            val newStudent: StudentModel = StudentModel(
                id = null,
                name = name,
                grade = grade,
                image = image,
                rollNumber = rollNo,
            )

            if (student != null) {
                newStudent.id = student.id
                DbHelper(this).updateStudent(newStudent)
            } else {
                val dbHelper = DbHelper(this)
                val newStudentId = dbHelper.addStudent(newStudent)
                Log.d("AddEditActivity", "Student added with ID: $newStudentId")
            }

            val homeIntent: Intent = Intent(this, MainActivity::class.java)
            homeIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(homeIntent)
            finish()
        }

    }
}
