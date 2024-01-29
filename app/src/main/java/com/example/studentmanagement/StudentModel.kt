import java.io.Serializable

data class StudentModel(
    var id: Long?,
    var name: String,
    var grade: String,
    var rollNumber: String,
    var image: String
) : Serializable

object StudentContract {
    const val TABLE_NAME = "students"
    const val COLUMN_ID = "_id"
    const val COLUMN_NAME = "name"
    const val COLUMN_GRADE = "grade"
    const val COLUMN_ROLL_NUMBER = "roll_number"
    const val COLUMN_IMAGE = "image"
}
