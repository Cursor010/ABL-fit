package com.grig.mytraining.ui.home.settings

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.grig.mytraining.DateBase.DBHelper
import com.grig.mytraining.MyHelper
import com.grig.mytraining.R

class DeleteExerciseDialog : AppCompatActivity() {
    private lateinit var db: SQLiteDatabase
    private lateinit var spinnerDeletingExercise: Spinner
    private lateinit var buttonDeletingExercise: Button
    private lateinit var arrayAdapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_exercise_dialog)

        initDatabase()
        initViews()
        setupSpinner()
        setupDeleteButton()
    }

    private fun initDatabase() {
        db = MyHelper.MyDBHelper.getDatabase()
    }

    private fun initViews() {
        spinnerDeletingExercise = findViewById(R.id.spinnerDeletingExercise)
        buttonDeletingExercise = findViewById(R.id.buttonDeletingExercise)
    }

    private fun setupSpinner() {
        arrayAdapter = ArrayAdapter(
            this,
            R.layout.spinner_list_item_with_ellipsize, // Новый layout с обработкой длинного текста
            MyHelper.MyDBHelper.getExercises()
        )
        arrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down)
        spinnerDeletingExercise.adapter = arrayAdapter
    }

    private fun setupDeleteButton() {
        buttonDeletingExercise.setOnClickListener {
            val exercise = spinnerDeletingExercise.selectedItem.toString()

            if (hasRelatedTrainings(exercise)) {
                showDeleteWarningDialog(exercise)
            } else {
                deleteExercise(exercise)
            }
        }
    }

    private fun hasRelatedTrainings(exercise: String): Boolean {
        val query = "SELECT COUNT(*) FROM ${DBHelper.TABLE_TRAINING} " +
                "WHERE ${DBHelper.TRAINING_KEY_EXERCISE} = ?"
        val cursor = db.rawQuery(query, arrayOf(exercise))
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count > 0
    }

    private fun showDeleteWarningDialog(exercise: String) {
        AlertDialog.Builder(this)
            .setTitle("Внимание!")
            .setMessage("Это упражнение используется в ${getTrainingCount(exercise)} тренировках.\n" +
                    "Все связанные тренировки будут удалены!")
            .setPositiveButton("Удалить всё") { _, _ -> deleteExerciseWithTrainings(exercise) }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun getTrainingCount(exercise: String): Int {
        val cursor = db.query(
            DBHelper.TABLE_TRAINING,
            arrayOf("COUNT(*)"),
            "${DBHelper.TRAINING_KEY_EXERCISE} = ?",
            arrayOf(exercise),
            null, null, null
        )
        cursor.moveToFirst()
        val count = cursor.getInt(0)
        cursor.close()
        return count
    }

    private fun deleteExerciseWithTrainings(exercise: String) {
        try {
            db.beginTransaction()

            // Удаляем тренировки
            db.delete(
                DBHelper.TABLE_TRAINING,
                "${DBHelper.TRAINING_KEY_EXERCISE} = ?",
                arrayOf(exercise)
            )

            // Удаляем упражнение
            deleteExercise(exercise)

            db.setTransactionSuccessful()
        } catch (e: Exception) {
            Toast.makeText(this, "Ошибка удаления: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            db.endTransaction()
            updateExercisesList()
        }
    }

    private fun deleteExercise(exercise: String) {
        val deletedRows = db.delete(
            DBHelper.TABLE_EXERCISES,
            "${DBHelper.EXERCISES_KEY_EXERCISE} = ?",
            arrayOf(exercise)
        )

        if (deletedRows > 0) {
            Toast.makeText(this, "Успешно удалено!", Toast.LENGTH_SHORT).show()
            updateExercisesList()
        } else {
            Toast.makeText(this, "Упражнение не найдено!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateExercisesList() {
        arrayAdapter.clear()
        arrayAdapter.addAll(MyHelper.MyDBHelper.getExercises())
        arrayAdapter.notifyDataSetChanged()
    }
}