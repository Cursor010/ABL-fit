package com.grig.mytraining.ui.home.settings

import androidx.appcompat.app.AppCompatActivity
import android.widget.Spinner
import android.widget.ArrayAdapter
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.grig.mytraining.R
import com.grig.mytraining.MyHelper
import com.grig.mytraining.DateBase.DBHelper
import android.widget.Toast

class DeleteExerciseDialog : AppCompatActivity() {
    var spinnerDeletingExercise: Spinner? = null
    var buttonDeletingExercise: Button? = null
    var arrayAdapter: ArrayAdapter<String>? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_exercise_dialog)
        arrayAdapter = ArrayAdapter(
            applicationContext,
            R.layout.spinner_list_item, MyHelper.MyDBHelper.getExercises()
        )
        arrayAdapter!!.setDropDownViewResource(R.layout.spinner_drop_down)
        spinnerDeletingExercise = findViewById(R.id.spinnerDeletingExercise)
        spinnerDeletingExercise?.adapter = arrayAdapter
        buttonDeletingExercise = findViewById(R.id.buttonDeletingExercise)
        buttonDeletingExercise?.setOnClickListener {
            MyHelper.MyDBHelper.getDatabase().delete( DBHelper.TABLE_EXERCISES, "exercise = ?",
                arrayOf(spinnerDeletingExercise?.selectedItem.toString())
            )
            Toast.makeText(this@DeleteExerciseDialog, "Успешно удалено!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}