package com.grig.mytraining.ui.home;

import android.content.ContentValues;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.grig.mytraining.MyHelper;
import com.grig.mytraining.R;

import java.time.LocalDate;

public class CreateTrainActivity extends AppCompatActivity {
    EditText editTextDate, editTextWeight , editTextAdditionalInfo;
    String spinnerText, editTextWeightText, editTextDateText, editTextAdditionalInfoText;
    AutoCompleteTextView autoCompleteTextView;
    ImageButton imageButtonMinusDate, imageButtonPlusDate;
    CheckBox checkBoxIsRecord;
    LocalDate date = null;
    String dateIntent, exerciseIntent, additionalInfoIntent, weightIntent, isRecord;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_train);



        findViewById(R.id.goBackButtonFromCreateTrain).setOnClickListener(view -> onBackPressed());

        autoCompleteTextView = findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(this,
                R.layout.auto_compleate, MyHelper.MyDBHelper.getExercises()));

        // Находим виджеты
        editTextWeight = findViewById(R.id.editTextNumberWeight);
        editTextAdditionalInfo = findViewById(R.id.editTextAdditionalInfo);

         imageButtonMinusDate = findViewById(R.id.imageButtonMinusDate);
         imageButtonPlusDate = findViewById(R.id.imageButtonPlusDate);

         checkBoxIsRecord = findViewById(R.id.checkBoxIsRecord);

        editTextDate = findViewById(R.id.editTextTrainDate);

        // Выставляем сегодняшнюю дату в editTextDate
        date = LocalDate.now();
        editTextDate.setHint(date.toString().substring(2));

        // При фокусе на поле даты textHint переводится в text
        editTextDate.setOnFocusChangeListener((view, b) -> {
            editTextWeight.setHint(MyHelper.MyDBHelper.personalCTFun(date.toString()));
            // Проверяем зашел ли пользователь в edit text или вышел из него
            if (b) {
                editTextDate.setText(editTextDate.getHint().toString());
            }
        });

        editTextWeight.setOnFocusChangeListener((view, b) -> {
            // Проверяем зашел ли пользователь в edit text или вышел из него
            if (b && !editTextWeight.getHint().toString().equals("Введите вес")) {
                editTextWeight.setText(editTextWeight.getHint().toString());
            }
        });

        editTextWeight.setHint(MyHelper.MyDBHelper.personalCTFun(editTextDate.getHint().toString()));

        if (getIntent().getStringExtra("date") != null) {
            dateIntent = getIntent().getStringExtra("date");
            exerciseIntent = getIntent().getStringExtra("exercise");
            additionalInfoIntent = getIntent().getStringExtra("additionalInfo");
            weightIntent = getIntent().getStringExtra("weight");
            System.out.println("isR" + getIntent().getStringExtra("isRecord"));
            if (getIntent().getStringExtra("isRecord").equals("0")) {
                System.out.println(true);
                isRecord = null;
            } else {
                isRecord = "1";
                checkBoxIsRecord.setChecked(true);
            }
            editTextDate.setHint(dateIntent.substring(2));
            autoCompleteTextView.setText(exerciseIntent);
            editTextAdditionalInfo.setText(additionalInfoIntent);
            System.out.println(weightIntent);
            editTextWeight.setText(weightIntent);
        }

        imageButtonMinusDate.setOnClickListener(view -> {

            date = date.minusDays(1);
            if (editTextWeight.getText().toString().isEmpty())
                editTextWeight.setHint(MyHelper.MyDBHelper.personalCTFun(date.toString().substring(2)));
            else
                editTextWeight.setText(MyHelper.MyDBHelper.personalCTFun(date.toString().substring(2)));
            if (editTextDate.getText().toString().isEmpty())
                editTextDate.setHint(date.toString().substring(2));
            else
                editTextDate.setText(date.toString().substring(2));
        });
        imageButtonPlusDate.setOnClickListener(view -> {
            date = date.minusDays(-1);
            if (editTextWeight.getText().toString().isEmpty())
                editTextWeight.setHint(MyHelper.MyDBHelper.personalCTFun(date.toString().substring(2)));
            else
                editTextWeight.setText(MyHelper.MyDBHelper.personalCTFun(date.toString().substring(2)));
            if (editTextDate.getText().toString().isEmpty())
                editTextDate.setHint(date.toString().substring(2));
            else
                editTextDate.setText(date.toString().substring(2));
        });

        Button BtnConfirmForm = findViewById(R.id.confirmFormButton);
        BtnConfirmForm.setOnClickListener(view -> {
            // Получаем текст всех форм
            spinnerText = autoCompleteTextView.getText().toString();
            if (!MyHelper.MyDBHelper.getExercises().contains(spinnerText)) {
                Toast.makeText(CreateTrainActivity.this, "Добавление " + spinnerText, Toast.LENGTH_SHORT).show();
                MyHelper.MyDBHelper.insertExercise(spinnerText);
            }
            editTextWeightText = editTextWeight.getText().toString();
            editTextDateText = editTextDate.getText().toString();
            editTextAdditionalInfoText = editTextAdditionalInfo.getText().toString();

            if (editTextDateText.isEmpty())
                editTextDateText = editTextDate.getHint().toString();
            if (editTextWeightText.isEmpty())
                editTextWeightText = editTextWeight.getHint().toString();
            if (editTextWeightText.equals("Введите вес")) {
                Toast.makeText(view.getContext(), "Введите вес", Toast.LENGTH_SHORT).show();
                return;
            }
            editTextDateText = "20" + editTextDateText;

            // Проверяем корректность даты
            if (!editTextDateText.matches("\\d{4}-\\d{2}-\\d{2}")){
                Toast.makeText(getApplicationContext(), "Не корректная дата!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Скрываем этот фрагмент если введен вес
            if (editTextWeightText.isEmpty())
                Toast.makeText(getApplicationContext(), "Введите вес", Toast.LENGTH_SHORT).show();
            else if (15>Float.parseFloat(editTextWeightText)||Float.parseFloat(editTextWeightText)>250)
                Toast.makeText(getApplicationContext(), "Вы правда столько весите?", Toast.LENGTH_SHORT).show();
            else {
                Toast.makeText(getApplicationContext(), "Тренировка добавлена!", Toast.LENGTH_SHORT).show();

                // Записываем в БД
                if (getIntent().getStringExtra("date") == null) {
                    if (!checkBoxIsRecord.isChecked())
                        MyHelper.MyDBHelper.insertTraining(editTextDateText, spinnerText, editTextWeightText, editTextAdditionalInfoText);
                    else
                        MyHelper.MyDBHelper.insertRecord(editTextDateText, spinnerText, editTextWeightText, editTextAdditionalInfoText);
                } else {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("date", editTextDateText);
                    contentValues.put("exercise", spinnerText);
                    contentValues.put("additional_info", editTextAdditionalInfoText);
                    contentValues.put("weight", editTextWeightText);
                    String newIsRecord = null;
                    if (checkBoxIsRecord.isChecked()) newIsRecord = "1";
                    contentValues.put("is_record", newIsRecord);
                    System.out.println("record " + newIsRecord + " " + isRecord);
//                    System.out.println(editTextDateText + " " + dateIntent);
//                    System.out.println(spinnerText + " " + exerciseIntent);
//                    System.out.println(editTextAdditionalInfoText + " " + additionalInfoIntent);
//                    System.out.println(editTextWeightText + " " + weightIntent);
//                    System.out.println(isRecord);
                    MyHelper.MyDBHelper.getDatabase().update("training", contentValues,
                            "date = ? and exercise = ? and additional_info = ? and weight = ?",
                            new String[] {dateIntent, exerciseIntent, additionalInfoIntent, weightIntent});
                }
                // Обновляем тренировочные дни для календаря
                MyHelper.MyDBHelper.TrainingDaysFromDB.updateTrainingDays();

                // Стираем все поля
                autoCompleteTextView.setText("");
                editTextDate.setHint(date.toString().substring(2));
                editTextWeight.setText("");
                editTextAdditionalInfo.setText("");
                checkBoxIsRecord.setChecked(false);
            }
        });

    }
}