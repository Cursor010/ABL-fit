package com.grig.mytraining.DateBase;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.grig.mytraining.ui.Training;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4; // Увеличена версия базы данных
    public static final String DATABASE_NAME = "trainingDB";

    // Таблица упражнений
    public static final String TABLE_EXERCISES = "exercises";
    public static final String EXERCISES_KEY_ID = "_id";
    public static final String EXERCISES_KEY_EXERCISE = "exercise";

    // Таблица тренировок
    public static final String TABLE_TRAINING = "training";
    public static final String TRAINING_KEY_ID = "_id";
    public static final String TRAINING_KEY_DATE = "date";
    public static final String TRAINING_KEY_EXERCISE = "exercise";
    public static final String TRAINING_KEY_WEIGHT = "weight";
    public static final String TRAINING_KEY_ADDITIONAL_INFO = "additional_info";
    public static final String TRAINING_KEY_IS_RECORD = "is_record";

    // Таблица заметок
    public static final String TABLE_NOTES = "notes";
    public static final String NOTES_KEY_ID = "_id";
    public static final String NOTES_KEY_TITLE = "title";
    public static final String NOTES_KEY_CONTENT = "content";
    public static final String NOTES_KEY_DATE = "date"; // Добавлена колонка для даты

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Создание таблицы упражнений
        db.execSQL(
                "CREATE TABLE " + TABLE_EXERCISES + "(" +
                        EXERCISES_KEY_EXERCISE + " TEXT" +
                        ")"
        );

        // Создание таблицы тренировок
        db.execSQL(
                "CREATE TABLE " + TABLE_TRAINING + "(" +
                        TRAINING_KEY_DATE + " TEXT," +
                        TRAINING_KEY_EXERCISE + " TEXT," +
                        TRAINING_KEY_WEIGHT + " REAL," +
                        TRAINING_KEY_ADDITIONAL_INFO + " TEXT," +
                        TRAINING_KEY_IS_RECORD + " INTEGER" +
                        ")"
        );

        // Создание таблицы заметок
        db.execSQL(
                "CREATE TABLE " + TABLE_NOTES + "(" +
                        NOTES_KEY_TITLE + " TEXT," +
                        NOTES_KEY_CONTENT + " TEXT," +
                        NOTES_KEY_DATE + " TEXT" + // Добавлена колонка для даты
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 4) {
            // Миграция для добавления колонки date в таблицу notes
            db.execSQL("ALTER TABLE " + TABLE_NOTES + " ADD COLUMN " + NOTES_KEY_DATE + " TEXT;");
        }

        // Миграция для версии 1
        if (oldVersion == 1) {
            migrateFromVersion1(db);
        }

        // Миграция для версии 2
        if (oldVersion == 2) {
            migrateFromVersion2(db);
        }
    }

    private void migrateFromVersion1(SQLiteDatabase db) {
        ArrayList<Training> trainings = new ArrayList<>();
        ArrayList<String> exercises = new ArrayList<>();

        // Сохраняем все упражнения
        Cursor cursorExercise = db.query(TABLE_EXERCISES, null, null, null, null, null, null);
        if (cursorExercise.moveToNext()) {
            do {
                exercises.add(cursorExercise.getString(1));
            } while (cursorExercise.moveToNext());
        }
        cursorExercise.close();

        // Сохраняем все тренировки
        Cursor cursorTraining = db.query(TABLE_TRAINING, null, null, null, null, null, null);
        if (cursorTraining.moveToNext()) {
            DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.ENGLISH);
            DateFormat trueDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);

            Date DBDate = null;
            do {
                try {
                    DBDate = dateFormat.parse(cursorTraining.getString(1));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                trainings.add(new Training(
                        trueDateFormat.format(DBDate),
                        cursorTraining.getString(2),
                        cursorTraining.getString(3),
                        cursorTraining.getString(4)
                ));
            } while (cursorTraining.moveToNext());
            cursorTraining.close();
        }

        // Удаляем старые таблицы
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);

        // Создаем новые таблицы
        onCreate(db);

        // Восстанавливаем данные
        for (String exercise : exercises) {
            ContentValues values = new ContentValues();
            values.put(EXERCISES_KEY_EXERCISE, exercise);
            db.insert(TABLE_EXERCISES, null, values);
        }

        for (Training training : trainings) {
            ContentValues values = new ContentValues();
            values.put(TRAINING_KEY_DATE, training.getDate());
            values.put(TRAINING_KEY_EXERCISE, training.getExercise());
            values.put(TRAINING_KEY_WEIGHT, training.getWeight());
            values.put(TRAINING_KEY_ADDITIONAL_INFO, training.getAdditionalInfo());
            db.insert(TABLE_TRAINING, null, values);
        }
    }

    private void migrateFromVersion2(SQLiteDatabase db) {
        ArrayList<Training> trainings = new ArrayList<>();
        ArrayList<String> exercises = new ArrayList<>();

        // Сохраняем все упражнения
        Cursor cursorExercise = db.query(TABLE_EXERCISES, null, null, null, null, null, null);
        if (cursorExercise.moveToNext()) {
            do {
                exercises.add(cursorExercise.getString(1));
            } while (cursorExercise.moveToNext());
        }
        cursorExercise.close();

        // Сохраняем все тренировки
        Cursor cursorTraining = db.query(TABLE_TRAINING, null, null, null, null, null, null);
        if (cursorTraining.moveToNext()) {
            do {
                trainings.add(new Training(
                        cursorTraining.getString(1),
                        cursorTraining.getString(2),
                        cursorTraining.getString(3),
                        cursorTraining.getString(4)
                ));
            } while (cursorTraining.moveToNext());
            cursorTraining.close();
        }

        // Удаляем старые таблицы
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAINING);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISES);

        // Создаем новые таблицы
        onCreate(db);

        // Восстанавливаем данные
        for (String exercise : exercises) {
            ContentValues values = new ContentValues();
            values.put(EXERCISES_KEY_EXERCISE, exercise);
            db.insert(TABLE_EXERCISES, null, values);
        }

        for (Training training : trainings) {
            ContentValues values = new ContentValues();
            values.put(TRAINING_KEY_DATE, training.getDate());
            values.put(TRAINING_KEY_EXERCISE, training.getExercise());
            values.put(TRAINING_KEY_WEIGHT, training.getWeight());
            values.put(TRAINING_KEY_ADDITIONAL_INFO, training.getAdditionalInfo());
            db.insert(TABLE_TRAINING, null, values);
        }
    }
}