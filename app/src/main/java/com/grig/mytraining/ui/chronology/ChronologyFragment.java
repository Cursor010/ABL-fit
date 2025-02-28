package com.grig.mytraining.ui.chronology;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.grig.mytraining.MyHelper;
import com.grig.mytraining.R;
import com.grig.mytraining.ui.Record;
import com.grig.mytraining.ui.Training;
import com.grig.mytraining.ui.home.CreateTrainActivity;

import java.util.ArrayList;

public class ChronologyFragment extends Fragment {
    RecyclerView recyclerView;
    ProgressBar progressBar;
    TextView tvHeader;

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(() -> showResults("Месяц", "", true, true), 0);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chronology, container, false);
        progressBar = view.findViewById(R.id.progressBarChronology);
        tvHeader = view.findViewById(R.id.tvHeaderChron);
        System.out.println(MyHelper.MyDBHelper.TrainingDaysFromDB.dateForChronology);
//        FragmentFilterChron fragmentFilterChron = new FragmentFilterChron();
//        fragmentFilterChron.show(requireActivity().getSupportFragmentManager(), "bottom");
        view.findViewById(R.id.filterChron).setOnClickListener(v -> {
            createBottomSheetDialog(view);
        });

        recyclerView = view.findViewById(R.id.recyclerViewCron);

        return view;
    }

    @SuppressLint("InflateParams")
    private void createBottomSheetDialog(View view) {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View bottomView = inflater.inflate(R.layout.filter_chron_bottom_sheet, null);
        AutoCompleteTextView autoCompleteTextView = bottomView.findViewById(R.id.autoCompleteTextView2);
        autoCompleteTextView.setAdapter(new ArrayAdapter<>(view.getContext(), R.layout.auto_compleate, MyHelper.MyDBHelper.getExercises()));
        autoCompleteTextView.setOnItemClickListener((parent, view1, position, id) -> {
            InputMethodManager imm = (InputMethodManager) view1.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(autoCompleteTextView.getWindowToken(), 0);
        });

        bottomView.findViewById(R.id.filterChronApplyButton).setOnClickListener(v -> {
            RadioGroup radioGroup = bottomView.findViewById(R.id.radioGroup);
            RadioButton radioButton = bottomView.findViewById(radioGroup.getCheckedRadioButtonId());
            String timeInterval = radioButton.getText().toString();

            SwitchCompat switchShowTraining = bottomView.findViewById(R.id.switchShowTraining);
            SwitchCompat switchShowRecord = bottomView.findViewById(R.id.switchShowRecord);

            showResults(timeInterval, autoCompleteTextView.getText().toString(),
                    switchShowTraining.isChecked(), switchShowRecord.isChecked());
            bottomSheetDialog.hide();
        });

        bottomSheetDialog.setContentView(bottomView);
        bottomSheetDialog.show();

        //        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if (hasFocus) {
//                    bottomView.findViewById(R.id.button2).setVisibility(View.VISIBLE);
//                    autoCompleteTextView.startAnimation(
//                            AnimationUtils.loadAnimation(getContext(), R.anim.slide_auto_compleate_tv));
//                    autoCompleteTextView.setTranslationX(0);
//                }
//                else
//                    bottomView.findViewById(R.id.button2).setVisibility(View.INVISIBLE);
//
//            }
//        });
    }

    private void showResults(String timeInterval, String exercise, boolean isSwitchTraining, boolean isSwitchRecord) {
        String[] datesInterval = MyHelper.MyDBHelper.getDatesTimeInterval(timeInterval);
        String dateStart = datesInterval[0];
        String dateEnd = datesInterval[1];
        ArrayList<Object> trainings = new ArrayList<>();
        trainings.add(new Training("Дата", "Упражнение", "Вес", "Результат"));
        Cursor cursor;
        if (MyHelper.MyDBHelper.TrainingDaysFromDB.dateForChronology != null) {
            System.out.println(MyHelper.MyDBHelper.TrainingDaysFromDB.dateForChronology);
            cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                    "SELECT date, exercise, weight, additional_info, is_record " +
                            "FROM training WHERE date BETWEEN ? AND ? ORDER BY date",
                    new String[]{MyHelper.MyDBHelper.TrainingDaysFromDB.dateForChronology,
                            MyHelper.MyDBHelper.TrainingDaysFromDB.dateForChronology});
        } else {
            if (exercise.equals("")) {
                cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                        "SELECT date, exercise, weight, additional_info, is_record " +
                                "FROM training WHERE date BETWEEN ? AND ? ORDER BY date",
                        new String[]{dateStart, dateEnd});
                //            if (isSwitchTraining && isSwitchRecord)
                //                cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                //                        "SELECT date, exercise, weight, additional_info, is_record " +
                //                                "FROM training WHERE date BETWEEN ? AND ? ORDER BY date",
                //                        new String[]{dateStart, dateEnd});
                //            else if (isSwitchTraining && !isSwitchRecord) {
                //                cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                //                        "SELECT date, exercise, weight, additional_info, is_record " +
                //                                "FROM training WHERE date BETWEEN ? AND ? ORDER BY date",
                //                        new String[]{dateStart, dateEnd});
                //            }
                //            else if (!isSwitchTraining && isSwitchRecord)
                //                cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                //                        "SELECT date, exercise, weight, additional_info, is_record " +
                //                                "FROM training WHERE date BETWEEN ? AND ? AND is_record = ? ORDER BY date",
                //                        new String[]{dateStart, dateEnd, "1"});
                //            else {
                //                cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                //                        "SELECT date, exercise, weight, additional_info, is_record " +
                //                                "FROM training WHERE date BETWEEN ? AND ? AND is_record = ? ORDER BY date",
                //                        new String[]{dateStart, dateEnd, "8"});
                //                Toast.makeText(getContext(), "Ну как хочешь, зырь теперь на пустой лист\uD83D\uDE0E", Toast.LENGTH_LONG).show();
                //            }
            } else
                cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                        "SELECT date, exercise, weight, additional_info, is_record FROM training " +
                                "WHERE date BETWEEN ? AND ? AND exercise = ? ORDER BY date", new String[]{dateStart, dateEnd, exercise});
        }

        if (cursor.moveToNext()) {
            do {
                if (cursor.getString(4) == null) {
                    if (isSwitchTraining)
                        trainings.add(new Training(cursor.getString(0),
                                cursor.getString(1), cursor.getString(2), cursor.getString(3)));
                } else if (isSwitchRecord)
                    trainings.add(new Record(cursor.getString(0),
                            cursor.getString(1), cursor.getString(2), cursor.getString(3)));
            } while (cursor.moveToNext());
        }
        if (!isSwitchTraining && !isSwitchRecord)
            Toast.makeText(getContext(), "Ну как хочешь, зырь теперь на пустой лист\uD83D\uDE0E", Toast.LENGTH_LONG).show();

        if (exercise.equals("")) {
//            tvHeader.setText("Результаты за " + timeInterval.toLowerCase(Locale.ROOT));
            recyclerView.setAdapter(new TrainingAdapterChronology(trainings, new TrainingAdapterChronology.OnItemClickListener() {
                @Override
                public void onTrainingClick(Training training) {
                    Intent intent = new Intent(getContext(), CreateTrainActivity.class);
                    intent.putExtra("date", training.getDate());
                    intent.putExtra("exercise", training.getExercise());
                    intent.putExtra("additionalInfo", training.getAdditionalInfo());
                    intent.putExtra("weight", training.getWeight());
                    intent.putExtra("isRecord", "0");
                    startActivity(intent);
                }

                @Override
                public void onRecordClick(Record record) {
                    Intent intent = new Intent(getContext(), CreateTrainActivity.class);
                    intent.putExtra("date", record.getDate());
                    intent.putExtra("exercise", record.getExercise());
                    intent.putExtra("additionalInfo", record.getAdditionalInfo());
                    intent.putExtra("weight", record.getWeight());
                    intent.putExtra("isRecord", "1");
                    startActivity(intent);
                }
            }));
        } else {
            tvHeader.setText("Результаты по " + exercise);
            recyclerView.setAdapter(new TrainingAdapterStatistics(trainings, new TrainingAdapterStatistics.OnItemClickListener() {
                @Override
                public void onTrainingClick(Training training) {
                    Intent intent = new Intent(getContext(), CreateTrainActivity.class);
                    intent.putExtra("date", training.getDate());
                    intent.putExtra("exercise", training.getExercise());
                    intent.putExtra("additionalInfo", training.getAdditionalInfo());
                    intent.putExtra("weight", training.getWeight());
                    intent.putExtra("isRecord", "0");
                    startActivity(intent);
                }

                @Override
                public void onRecordClick(Record record) {
                    Intent intent = new Intent(getContext(), CreateTrainActivity.class);
                    intent.putExtra("date", record.getDate());
                    intent.putExtra("exercise", record.getExercise());
                    intent.putExtra("additionalInfo", record.getAdditionalInfo());
                    intent.putExtra("weight", record.getWeight());
                    intent.putExtra("isRecord", "1");
                    startActivity(intent);
                }
            }));
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}