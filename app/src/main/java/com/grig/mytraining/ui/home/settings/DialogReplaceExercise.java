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

        spinnerReplaceExercise = findViewById(R.id.spinnerReplaceExercise);
        replaceExerciseDialogNewName = findViewById(R.id.replaceExerciseDialogNewName);
        buttonReplaceExercise = findViewById(R.id.buttonReplaceExerciseDialog);

        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(),
                R.layout.spinner_list_item, MyHelper.MyDBHelper.getExercises());
        arrayAdapter.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerReplaceExercise.setAdapter(arrayAdapter);

        buttonReplaceExercise.setOnClickListener(view -> {
            newName = replaceExerciseDialogNewName.getText().toString();
            oldName = spinnerReplaceExercise.getSelectedItem().toString();

            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.EXERCISES_KEY_EXERCISE, newName);

            database.update(DBHelper.TABLE_EXERCISES, contentValues, "exercise = ?",
                    new String[] {oldName});
            Cursor cursorT = database.query(DBHelper.TABLE_TRAINING, null, null, null, null, null, null);
            if (cursorT.moveToNext()){
                int dateIndex = cursorT.getColumnIndex(DBHelper.TRAINING_KEY_DATE);
                int exerciseIndex = cursorT.getColumnIndex(DBHelper.TRAINING_KEY_EXERCISE);
                int additionalInfoIndex = cursorT.getColumnIndex(DBHelper.TRAINING_KEY_ADDITIONAL_INFO);
                int weightIndex = cursorT.getColumnIndex(DBHelper.TRAINING_KEY_WEIGHT);

                do {
                    if ((cursorT.getString(exerciseIndex).equals(oldName))) {
                        ContentValues contentValues1 = new ContentValues();
                        contentValues1.put(DBHelper.TRAINING_KEY_DATE, cursorT.getString(dateIndex));
                        contentValues1.put(DBHelper.TRAINING_KEY_EXERCISE, newName);
                        contentValues1.put(DBHelper.TRAINING_KEY_ADDITIONAL_INFO, cursorT.getString(additionalInfoIndex));
                        contentValues1.put(DBHelper.TRAINING_KEY_WEIGHT, cursorT.getString(weightIndex));
                        database.update(DBHelper.TABLE_TRAINING, contentValues1, "exercise = ?",
                                new String[] {oldName});
                    }
                } while (cursorT.moveToNext());
            }
    cursorT.close();
            Toast.makeText(DialogReplaceExercise.this, "Успешно заменено!", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}