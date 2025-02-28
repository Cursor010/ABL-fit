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
//            public static void generateTrainingAdapter(String timeInterval, byte parent, String selectedExercise) {
//
//                ArrayList<Object> trainings = new ArrayList<>();
//
//                trainings.add(new Training("Дата", "Упражнение", "Вес", "Результат"));
//                Cursor cursor;
//                if (parent == STATISTICS) {
////                    cursor = getDatabase().rawQuery("SELECT date, weight, additional_info FROM training" +
////                            " WHERE date BETWEEN ? AND ? AND exercise = ? order By date",
////                            new String[] {dateStart.toString(), dateEnd.toString(), selectedExercise});
//                    cursor = MyHelper.MyDBHelper.getTrainingCursor(new String[] {"date", "weight", "additional_info", "is_record"},
//                "date BETWEEN ? AND ? AND exercise = ?",
//                new String[] {ge, dateEnd.toString(), selectedExercise}, "date");
//
//                    if (cursor.moveToNext()) {
//                        do {
//                            if (cursor.getString(3) == null)
//                                trainings.add(new Training(cursor.getString(0).substring(2), null,
//                                        cursor.getString(1), cursor.getString(2)));
//                            else
//                                trainings.add(new Record(cursor.getString(0).substring(2),
//                                        cursor.getString(1), cursor.getString(2), cursor.getString(3)));
//                        } while (cursor.moveToNext());
//                    }
//                    return new TrainingAdapterStatistics(trainings);
//
//                } else {
//                    cursor = MyHelper.MyDBHelper.getTrainingCursor(
//                            new String[] {"date", "exercise", "weight", "additional_info", "is_record"},
//                            "date BETWEEN ? AND ?",
//                            new String[] {dateStart.toString(), dateEnd.toString()}, "date");
//
//                if (cursor.moveToNext()) {
//                    do {
//                        if (cursor.getString(4) == null)
//                            trainings.add(new Training(cursor.getString(0).substring(2),
//                                    cursor.getString(1), cursor.getString(2), cursor.getString(3)));
//                        else
//                            trainings.add(new Record(cursor.getString(0).substring(2),
//                                    cursor.getString(1), cursor.getString(2), cursor.getString(3)));
//                    } while (cursor.moveToNext());
//                }
//                    trainingAdapterChronology = new TrainingAdapterChronology(trainings);
//                }
//                cursor.close();
//            }


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
//                    if (year == LocalDate.now().getYear())
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

//        public static Cursor getTrainingCursorSortByDate() {
//            return database.query(DBHelper.TABLE_TRAINING, new String[] {"date", "exercise", "weight", "additional_info"},
//                    null, null, null, null, DBHelper.TRAINING_KEY_DATE);
//        }
//
//        public static Cursor getTrainingCursorSortByDateExercise() {
//            return database.query(DBHelper.TABLE_TRAINING, new String[] {"date", "exercise", "weight", "additional_info"},
//                    "exercise = ?", null, null, null, DBHelper.TRAINING_KEY_DATE);
//        }

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
//        public static ArrayList<String> readTableTraining(String key) {
//            ArrayList<String> listValues = new ArrayList<>();
//            Cursor cursor = getTrainingCursor();
//
//            if (cursor.moveToNext()){
//                int valueIndex = cursor.getColumnIndex(key);
//                do {
//                    listValues.add(cursor.getString(valueIndex));
//                } while (cursor.moveToNext());
//            }
//            cursor.close();
//            return listValues;
//        }

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