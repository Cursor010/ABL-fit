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
    public static final int DATABASE_VERSION = 3;
    public static final String  DATABASE_NAME = "trainingDB";
    public static final String TABLE_EXERCISES = "exercises";
    public static final String EXERCISES_KEY_ID = "_id";
    public static final String EXERCISES_KEY_EXERCISE = "exercise";
    public static final String TABLE_TRAINING = "training";
    public static final String TRAINING_KEY_ID = "_id";
    public static final String TRAINING_KEY_DATE = "date";
    public static final String TRAINING_KEY_EXERCISE = "exercise";
    public static final String TRAINING_KEY_WEIGHT = "weight";
    public static final String TRAINING_KEY_ADDITIONAL_INFO = "additional_info";
    public static final String TRAINING_KEY_IS_RECORD = "is_record";

    public static final String TABLE_NOTES = "notes";
    public static final String NOTES_KEY_ID = "_id";
    public static final String NOTES_KEY_TITLE = "title";
    public static final String NOTES_KEY_CONTENT = "content";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table " + TABLE_EXERCISES + "(" + EXERCISES_KEY_EXERCISE + " text" + ")" );
        sqLiteDatabase.execSQL(
                "create table " + TABLE_TRAINING +
                "(" + TRAINING_KEY_DATE + " numeric,"
                + TRAINING_KEY_EXERCISE + " text,"
                + TRAINING_KEY_WEIGHT + " real,"
                + TRAINING_KEY_ADDITIONAL_INFO + " text,"
                + TRAINING_KEY_IS_RECORD + " numeric"
                        + ")"
        );
        sqLiteDatabase.execSQL(
                "create table " + TABLE_NOTES +
                        "(" + NOTES_KEY_TITLE + " text,"
                        + NOTES_KEY_CONTENT + " text" + ")"
        );
//        sqLiteDatabase.execSQL(
//                "create table " + TABLE_RECORD +
//                        "(" + RECORD_KEY_ID + " integer primary key,"
//                        + RECORD_KEY_DATE + " text,"
//                        + RECORD_KEY_EXERCISE + " text,"
//                        + RECORD_KEY_WEIGHT + " text,"
//                        + RECORD_KEY_ADDITIONAL_INFO + " text" + ")"
//        );
//
//        sqLiteDatabase.execSQL(
//                "create table " + TABLE_WEIGHT +
//                "(" + WEIGHT_KEY_ID + " integer primary key,"
//                + WEIGHT_KEY_DATE + " text,"
//                + WEIGHT_KEY_WEIGHT_VALUE + "text" + ")"
//        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i == 1) {
            ArrayList<Training> trainings = new ArrayList<>();
            ArrayList<String> exercises = new ArrayList<>();

            // Записываем в exercises все упражнения
            Cursor cursorExercise = sqLiteDatabase.query(DBHelper.TABLE_EXERCISES, null, null, null, null, null, null);
            if (cursorExercise.moveToNext()){
                do {
                    exercises.add(cursorExercise.getString(1));
                } while (cursorExercise.moveToNext());
            }
            cursorExercise.close();

            // Записываем в trainings все тренировки
            Cursor cursorTraining = sqLiteDatabase.query(DBHelper.TABLE_TRAINING, null, null, null, null, null, null);
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
                            cursorTraining.getString(4)));
                } while (cursorTraining.moveToNext());
                cursorTraining.close();
            }
    //        sqLiteDatabase.execSQL("drop table exercises");
            sqLiteDatabase.execSQL("drop table training");
            sqLiteDatabase.execSQL("drop table exercises");
            onCreate(sqLiteDatabase);
            for (String exercise : exercises) {
                ContentValues contentValuesExercise = new ContentValues();
                contentValuesExercise.put(DBHelper.EXERCISES_KEY_EXERCISE, exercise);
                sqLiteDatabase.insert(DBHelper.TABLE_EXERCISES, null, contentValuesExercise);
            }

            for (Training training : trainings) {
                ContentValues contentValuesTraining = new ContentValues();
                contentValuesTraining.put(DBHelper.TRAINING_KEY_DATE, training.getDate());
                contentValuesTraining.put(DBHelper.TRAINING_KEY_EXERCISE, training.getExercise());
                contentValuesTraining.put(DBHelper.TRAINING_KEY_WEIGHT, training.getWeight());
                contentValuesTraining.put(DBHelper.TRAINING_KEY_ADDITIONAL_INFO, training.getAdditionalInfo());
               sqLiteDatabase.insert(DBHelper.TABLE_TRAINING, null, contentValuesTraining);
            }
        }
        else if (i == 2) {
            ArrayList<Training> trainings = new ArrayList<>();
            ArrayList<String> exercises = new ArrayList<>();

            // Записываем в exercises все упражнения
            Cursor cursorExercise = sqLiteDatabase.query(DBHelper.TABLE_EXERCISES, null, null, null, null, null, null);
            if (cursorExercise.moveToNext()){
                do {
                    exercises.add(cursorExercise.getString(1));
                } while (cursorExercise.moveToNext());
            }
            cursorExercise.close();

            // Записываем в trainings все тренировки
            Cursor cursorTraining = sqLiteDatabase.query(DBHelper.TABLE_TRAINING, null, null, null, null, null, null);
            if (cursorTraining.moveToNext()) {
                do {
                    trainings.add(new Training(
                            cursorTraining.getString(1),
                            cursorTraining.getString(2),
                            cursorTraining.getString(3),
                            cursorTraining.getString(4)));
                } while (cursorTraining.moveToNext());
                cursorTraining.close();
            }
            //        sqLiteDatabase.execSQL("drop table exercises");
            sqLiteDatabase.execSQL("drop table training");
            sqLiteDatabase.execSQL("drop table exercises");
            onCreate(sqLiteDatabase);
            for (String exercise : exercises) {
                ContentValues contentValuesExercise = new ContentValues();
                contentValuesExercise.put(DBHelper.EXERCISES_KEY_EXERCISE, exercise);
                sqLiteDatabase.insert(DBHelper.TABLE_EXERCISES, null, contentValuesExercise);
            }

            for (Training training : trainings) {
                ContentValues contentValuesTraining = new ContentValues();
                contentValuesTraining.put(DBHelper.TRAINING_KEY_DATE, training.getDate());
                contentValuesTraining.put(DBHelper.TRAINING_KEY_EXERCISE, training.getExercise());
                contentValuesTraining.put(DBHelper.TRAINING_KEY_WEIGHT, training.getWeight());
                contentValuesTraining.put(DBHelper.TRAINING_KEY_ADDITIONAL_INFO, training.getAdditionalInfo());
                sqLiteDatabase.insert(DBHelper.TABLE_TRAINING, null, contentValuesTraining);
            }
        }
    }
}
