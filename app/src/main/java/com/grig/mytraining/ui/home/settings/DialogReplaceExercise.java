package com.grig.mytraining.ui.home.settings;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grig.mytraining.DateBase.DBHelper;
import com.grig.mytraining.MyHelper;
import com.grig.mytraining.R;

public class DialogReplaceExercise extends AppCompatActivity {
    Spinner spinnerReplaceExercise;
    EditText replaceExerciseDialogNewName;
    Button buttonReplaceExercise;
    ArrayAdapter<String> arrayAdapter;
    DBHelper dbHelper;
    SQLiteDatabase database;
    String newName, oldName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_replace_exercise);

        // 1. Инициализация базы данных
        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        spinnerReplaceExercise = findViewById(R.id.spinnerReplaceExercise);
        replaceExerciseDialogNewName = findViewById(R.id.replaceExerciseDialogNewName);
        buttonReplaceExercise = findViewById(R.id.buttonReplaceExerciseDialog);

        arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_list_item,
                MyHelper.MyDBHelper.getExercises());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerReplaceExercise.setAdapter(arrayAdapter);

        buttonReplaceExercise.setOnClickListener(view -> {
            newName = replaceExerciseDialogNewName.getText().toString().trim();
            oldName = spinnerReplaceExercise.getSelectedItem().toString().trim();

            // 2. Проверка ввода
            if (newName.isEmpty()) {
                Toast.makeText(this, "Введите новое название", Toast.LENGTH_SHORT).show();
                return;
            }

            // 3. Проверка существования нового имени
            if (arrayAdapter.getPosition(newName) != -1) {
                Toast.makeText(this, "Упражнение уже существует", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                database.beginTransaction();

                // 4. Обновление упражнения
                ContentValues exValues = new ContentValues();
                exValues.put(DBHelper.EXERCISES_KEY_EXERCISE, newName);
                int exUpdated = database.update(
                        DBHelper.TABLE_EXERCISES,
                        exValues,
                        DBHelper.EXERCISES_KEY_EXERCISE + " = ?",
                        new String[]{oldName}
                );

                if (exUpdated > 0) {
                    // 5. Массовое обновление тренировок
                    ContentValues trValues = new ContentValues();
                    trValues.put(DBHelper.TRAINING_KEY_EXERCISE, newName);
                    database.update(
                            DBHelper.TABLE_TRAINING,
                            trValues,
                            DBHelper.TRAINING_KEY_EXERCISE + " = ?",
                            new String[]{oldName}
                    );

                    database.setTransactionSuccessful();
                    Toast.makeText(this, "Успешно заменено!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Упражнение не найдено", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, "Ошибка: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            } finally {
                database.endTransaction();
            }

            // 6. Обновление списка упражнений
            arrayAdapter.clear();
            arrayAdapter.addAll(MyHelper.MyDBHelper.getExercises());
            arrayAdapter.notifyDataSetChanged();

            finish();
        });
    }
}