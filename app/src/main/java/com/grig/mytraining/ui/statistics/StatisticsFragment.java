package com.grig.mytraining.ui.statistics;

import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.grig.mytraining.MyHelper;
import com.grig.mytraining.R;
import com.grig.mytraining.ui.Record;
import com.grig.mytraining.ui.Training;

import java.util.ArrayList;

public class StatisticsFragment extends Fragment {
    Spinner spinnerTimeInterval;
    AutoCompleteTextView exercisesACTV;
    ArrayAdapter<String> arrayAdapterSpinnerExercisesStat,arrayAdapterSpinnerTimeIntervalStat;

    RecyclerView recyclerView;
    ImageButton imageButtonDel;
    long counter;
    ProgressBar progressBar;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(this::showResults, 0);
    }

        public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_statistics, container, false);
        progressBar = view.findViewById(R.id.progressBarStatistics);
        ++counter;
        System.out.println("create" + counter);

        arrayAdapterSpinnerExercisesStat = new ArrayAdapter<>(getContext(),
                R.layout.spinner_list_item_time_interval, MyHelper.MyDBHelper.getExercises());
        arrayAdapterSpinnerExercisesStat.setDropDownViewResource(R.layout.spinner_drop_down);

        recyclerView = view.findViewById(R.id.recyclerViewStat);

        exercisesACTV = view.findViewById(R.id.spinner_exercises_stat);
        exercisesACTV.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.auto_compleate, MyHelper.MyDBHelper.getExercises()));
        exercisesACTV.setText(MyHelper.MyDBHelper.getExercises().get(0));

        arrayAdapterSpinnerTimeIntervalStat = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item_time_interval,
                new String[] {"две недели", "месяц", "два месяца", "всё время"});
        arrayAdapterSpinnerTimeIntervalStat.setDropDownViewResource(R.layout.spinner_drop_down);

        spinnerTimeInterval = view.findViewById(R.id.spinner_time_interval_stat);
        spinnerTimeInterval.setAdapter(arrayAdapterSpinnerTimeIntervalStat);
        spinnerTimeInterval.setSelection(1);

        spinnerTimeInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                System.out.println("select");
                if (counter >= 2) {
                    System.out.println("req" + counter);
                    showResults();
                }
                System.out.println("+++");
                counter ++;
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {} });

        exercisesACTV.setOnItemClickListener((parent, view1, position, id) -> showResults());

        exercisesACTV.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus)
                exercisesACTV.setText("");
        });

        imageButtonDel = view.findViewById(R.id.imageButtonDel);
        imageButtonDel.setOnClickListener(v -> exercisesACTV.setText(""));
        return view;
    }

//    public void generateTrainingAdapter() {
//        LocalDate dateStart = LocalDate.now();
//        LocalDate dateEnd = LocalDate.now();
//        String selectedExercise = exercisesACTV.getText().toString();
//
//        switch (spinnerTimeInterval.getSelectedItem().toString()) {
//            case "неделю":
//                dateStart = dateStart.minusWeeks(1);
//                break;
//            case "две недели":
//                dateStart = dateStart.minusWeeks(2);
//                break;
//            case "месяц":
//                dateStart = dateStart.minusMonths(1);
//                break;
//            case "всё время":
//                dateStart = dateStart.minusYears(10);
//                break;
//        }
//
//        ArrayList<Object> trainings = new ArrayList<>();
//
//        trainings.add(new Training("Дата", "Упражнение", "Вес", "Результат"));
//        Cursor cursorT = MyHelper.MyDBHelper.getTrainingCursor(new String[] {"date", "weight", "additional_info", "is_record"},
//                "date BETWEEN ? AND ? AND exercise = ?",
//                new String[] {dateStart.toString(), dateEnd.toString(), selectedExercise}, "date");
//        if (cursorT.moveToNext()){
//            do {
//                if (cursorT.getString(3) == null)
//                    trainings.add(new Training(cursorT.getString(0).substring(2),
//                            null, cursorT.getString(1), cursorT.getString(2)));
//                else
//                    trainings.add(new Record(cursorT.getString(0).substring(2),
//                            null, cursorT.getString(1), cursorT.getString(2)));
//            } while (cursorT.moveToNext());
//        }
//        cursorT.close();
//        MyHelper.MyDBHelper.TrainingAdapters.trainingAdapterStatistics = new TrainingAdapterStatistics(trainings);
//    }

    public void showResults() {
        System.out.println("show");
        String[] dates = MyHelper.MyDBHelper.getDatesTimeInterval(spinnerTimeInterval.getSelectedItem().toString());
        String dateStart = dates[0];
        String dateEnd = dates[1];
        ArrayList<Object> trainings = new ArrayList<>();
        String selectedExercise = exercisesACTV.getText().toString();

        trainings.add(new Training("Дата", "Упражнение", "Вес", "Результат"));
        Cursor cursorT = MyHelper.MyDBHelper.getDatabase().rawQuery(
                "SELECT date, weight, additional_info, is_record FROM training " +
                        "WHERE date BETWEEN ? AND ? AND exercise = ? ORDER BY date", new String[] {dateStart, dateEnd, selectedExercise});
        if (cursorT.moveToNext()){
            do {
                if (cursorT.getString(3) == null)
                    trainings.add(new Training(cursorT.getString(0).substring(2),
                            null, cursorT.getString(1), cursorT.getString(2)));
                else
                    trainings.add(new Record(cursorT.getString(0).substring(2),
                            null, cursorT.getString(1), cursorT.getString(2)));
            } while (cursorT.moveToNext());
        }
        cursorT.close();

//        recyclerView.setAdapter(new TrainingAdapterStatistics(trainings));
        progressBar.setVisibility(View.INVISIBLE);
    }
}