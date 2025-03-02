package com.grig.mytraining;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.grig.mytraining.DateBase.DBHelper;
import com.grig.mytraining.ui.home.notes.Note;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

public final class MyHelper {

    public static class MyDBHelper {
        public static void updateNote(Note note, String oldTitle) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.NOTES_KEY_TITLE, note.getTitle());
            contentValues.put(DBHelper.NOTES_KEY_CONTENT, note.getContent());
            database.update(
                    DBHelper.TABLE_NOTES,
                    contentValues,
                    DBHelper.NOTES_KEY_TITLE + " = ?",
                    new String[]{oldTitle}
            );
        }
        private static final SQLiteDatabase database = new DBHelper(MyApplication.getAppContext()).getWritableDatabase();

        public static String[] getDatesTimeInterval(String timeInterval) {
            LocalDate dateStart = LocalDate.now();
            LocalDate dateEnd = LocalDate.now();
            switch (timeInterval) {
                case "Две недели":
                    dateStart = dateStart.minusWeeks(2);
                    break;
                case "Месяц":
                    dateStart = dateStart.minusMonths(1);
                    break;
                case "Два месяца":
                    dateStart = dateStart.minusMonths(2);
                    break;
                case "Всё время":
                    dateStart = LocalDate.MIN;
                    break;
            }
            return new String[] {dateStart.toString(), dateEnd.toString()};
        }
        public static class TrainingDaysFromDB {
            public static HashSet<CalendarDay> trainingDays = new HashSet<>();
            public static String dateForChronology = null;

            private static void getTrainingDaysFromDB () {
                CalendarDay trainingDay;
                int day;
                int month;
                int year;

                @SuppressLint("Recycle") Cursor cursor = MyHelper.MyDBHelper.getDatabase().rawQuery("SELECT date FROM training", null);
                while (cursor.moveToNext()) {
                    // берем день, месяц и год из даты
                    year = Integer.parseInt(cursor.getString(0).substring(0, 4));
                    month = Integer.parseInt(cursor.getString(0).substring(5, 7));
                    day = Integer.parseInt(cursor.getString(0).substring(8, 10));
                    trainingDay = CalendarDay.from(year, month, day);
                    trainingDays.add(trainingDay);
                }
            }

            public static void updateTrainingDays () {
                getTrainingDaysFromDB();
            }

        }

        public static SQLiteDatabase getDatabase() {
            return database;
        }

        public static ArrayList<Note> getNotes() {
            ArrayList<Note> notes = new ArrayList<>();
            Cursor cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                    "SELECT title, content FROM notes", null);
            while (cursor.moveToNext()) {
                notes.add(new Note(cursor.getString(0), cursor.getString(1)));
            }
            return notes;
        }

        public static String getFirstDate() {
            Cursor cursor = database.rawQuery("SELECT min(date) from training", null);
            cursor.moveToNext();
            String firstDate;
            firstDate = cursor.getString(0);
            cursor.close();
            return firstDate;
        }

        public static Cursor getTrainingCursor(String[] columns, String selection, String[] selectionArgs, String orderBy) {
            return database.query(DBHelper.TABLE_TRAINING,columns, selection, selectionArgs, null, null, orderBy);
        }
        public static Cursor getExerciseCursor() {
            return database.query(DBHelper.TABLE_EXERCISES, new String[] {"exercise"},
                    null, null, null, null, null);
        }

        public static void insertExercise(String value) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.EXERCISES_KEY_EXERCISE, value);
            database.insert(DBHelper.TABLE_EXERCISES, null, contentValues);
        }

        public static void insertTraining(String Date, String Exercise, String Weight, String AdditionalInfo) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.TRAINING_KEY_DATE, Date);
            contentValues.put(DBHelper.TRAINING_KEY_EXERCISE, Exercise);
            contentValues.put(DBHelper.TRAINING_KEY_WEIGHT, Weight);
            contentValues.put(DBHelper.TRAINING_KEY_ADDITIONAL_INFO, AdditionalInfo);
            database.insert(DBHelper.TABLE_TRAINING, null, contentValues);
        }

        public static void insertRecord(String Date, String Exercise, String Weight, String AdditionalInfo) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.TRAINING_KEY_DATE, Date);
            contentValues.put(DBHelper.TRAINING_KEY_EXERCISE, Exercise);
            contentValues.put(DBHelper.TRAINING_KEY_WEIGHT, Weight);
            contentValues.put(DBHelper.TRAINING_KEY_ADDITIONAL_INFO, AdditionalInfo);
            contentValues.put(DBHelper.TRAINING_KEY_IS_RECORD, 1);
            database.insert(DBHelper.TABLE_TRAINING, null, contentValues);
        }
        public static void insertNote(Note note) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBHelper.NOTES_KEY_TITLE, note.getTitle());
            contentValues.put(DBHelper.NOTES_KEY_CONTENT, note.getContent());
            database.insert(DBHelper.TABLE_NOTES, null, contentValues);
        }

        public static ArrayList<String> getExercises() {
            ArrayList<String> listExercises = new ArrayList<>();
            Cursor cursor = getExerciseCursor();
            if (cursor.moveToNext()){
                do {
                    listExercises.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            cursor.close();
            if (listExercises.isEmpty()) {
                listExercises.add("Упражнений нет!");
            }
            return listExercises;
        }
        public static String personalCTFun(String date) {
            Cursor cursor = getTrainingCursor(new String[] {"date", "weight"}, null, null, null);

            if (cursor.moveToNext()){
                do {
                    if (cursor.getString(0).substring(2).equals(date)) {
                        String weight = cursor.getString(1);
                        cursor.close();
                        return weight;
                    }

                } while (cursor.moveToNext());
            }
            cursor.close();
            return "Введите вес";
        }
    }
}