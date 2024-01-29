import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DbHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "students.db"
        const val DATABASE_VERSION = 1
    }

    private val initializingQuery = """
        CREATE TABLE ${StudentContract.TABLE_NAME} (
            ${StudentContract.COLUMN_ID} INTEGER PRIMARY KEY,
            ${StudentContract.COLUMN_NAME} TEXT,
            ${StudentContract.COLUMN_GRADE} TEXT,
            ${StudentContract.COLUMN_ROLL_NUMBER} TEXT,
            ${StudentContract.COLUMN_IMAGE} TEXT
        )
    """

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(initializingQuery)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {}


    fun getAllStudents(): List<StudentModel> {
        val students = mutableListOf<StudentModel>()

        val db = readableDatabase

        val cursor = db.query(
            StudentContract.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        while (cursor.moveToNext()) {
            val idIndex = cursor.getColumnIndex(StudentContract.COLUMN_ID)
            val nameIndex = cursor.getColumnIndex(StudentContract.COLUMN_NAME)
            val gradeIndex = cursor.getColumnIndex(StudentContract.COLUMN_GRADE)
            val rollNumberIndex = cursor.getColumnIndex(StudentContract.COLUMN_ROLL_NUMBER)
            val imageIndex = cursor.getColumnIndex(StudentContract.COLUMN_IMAGE)

            if (idIndex != -1 && nameIndex != -1 && gradeIndex != -1 &&
                rollNumberIndex != -1 && imageIndex != -1) {

                val id = cursor.getLong(idIndex)
                val name = cursor.getString(nameIndex)
                val grade = cursor.getString(gradeIndex)
                val rollNumber = cursor.getString(rollNumberIndex)
                val image = cursor.getString(imageIndex)

                val student = StudentModel(id, name, grade, rollNumber, image)
                students.add(student)
            }
        }

        cursor.close()
        db.close()

        return students
    }

    fun addStudent(student: StudentModel): Long {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(StudentContract.COLUMN_NAME, student.name)
            put(StudentContract.COLUMN_GRADE, student.grade)
            put(StudentContract.COLUMN_ROLL_NUMBER, student.rollNumber)
            put(StudentContract.COLUMN_IMAGE, student.image)
        }

        val newId = db.insert(StudentContract.TABLE_NAME, null, values)

        student.id = newId

        db.close()

        Log.d("DbHelper", "Student added with ID: $newId") // Add this line for debugging

        return newId
    }



    fun deleteStudent(student: StudentModel) : Boolean {
        val db = writableDatabase

        val whereClause = "${StudentContract.COLUMN_ID} = ?"
        val whereArgs = arrayOf(student.id.toString())

        db.delete(StudentContract.TABLE_NAME, whereClause, whereArgs)

        db.close()

        return true
    }

    fun updateStudent(student: StudentModel): Boolean {
        val db = writableDatabase

        val values = ContentValues().apply {
            put(StudentContract.COLUMN_NAME, student.name)
            put(StudentContract.COLUMN_GRADE, student.grade)
            put(StudentContract.COLUMN_ROLL_NUMBER, student.rollNumber)
            put(StudentContract.COLUMN_IMAGE, student.image)
        }

        val whereClause = "${StudentContract.COLUMN_ID} = ?"
        val whereArgs = arrayOf(student.id.toString())

        val rowsAffected = db.update(StudentContract.TABLE_NAME, values, whereClause, whereArgs)

        db.close()

        return rowsAffected > 0
    }



}
