package com.grig.mytraining.ui.weight;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.grig.mytraining.MyHelper;
import com.grig.mytraining.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Date;


public class WeightFragment extends Fragment {
    String firstDate;
    TextView maxWeightValue, minWeightValue, maxWeight, minWeight, maxWeightDate, minWeightDate;
    Spinner spinnerTimeInterval;
    ArrayAdapter<String> arrayAdapterSpinnerTimeInterval;
    GraphView graph;
    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight, container, false);

        firstDate = MyHelper.MyDBHelper.getFirstDate();
        graph = view.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        graph.getGridLabelRenderer().setNumVerticalLabels(6);
        graph.getViewport().setXAxisBoundsManual(false);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getContext(), new SimpleDateFormat("dd.MM.yy")));

        maxWeight = view.findViewById(R.id.maxWeight);
        minWeight = view.findViewById(R.id.minWeight);
        maxWeightValue = view.findViewById(R.id.maxWeightValue);
        minWeightValue = view.findViewById(R.id.minWeightValue);
        maxWeightDate = view.findViewById(R.id.maxWeightDate);
        minWeightDate = view.findViewById(R.id.minWeightDate);

        arrayAdapterSpinnerTimeInterval = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item_time_interval,
                new String[] {"две недели", "месяц", "два месяца", "всё время"});
        arrayAdapterSpinnerTimeInterval.setDropDownViewResource(R.layout.spinner_drop_down);
        spinnerTimeInterval = view.findViewById(R.id.spinner_time_interval_weight);
        spinnerTimeInterval.setAdapter(arrayAdapterSpinnerTimeInterval);
        spinnerTimeInterval.setSelection(1);
        spinnerTimeInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                showResults();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return view;
    }

    public void showResults() {
        LocalDate dateStart = LocalDate.now();
        LocalDate dateEnd = LocalDate.now();
        switch (spinnerTimeInterval.getSelectedItem().toString()) {
            case "две недели":
                dateStart = dateStart.minusWeeks(2);
                break;
            case "месяц":
                dateStart = dateStart.minusMonths(1);
                break;
            case "два месяца":
                dateStart = dateStart.minusMonths(2);
                break;
            case "всё время":
                dateStart = LocalDate.MIN;
                break;
        }

        Cursor cursorMaxWeight = MyHelper.MyDBHelper.getDatabase().rawQuery("select max(weight), date from training where date between ? and ?",
                new String[] {dateStart.toString(), dateEnd.toString()});
        Cursor cursorMinWeight = MyHelper.MyDBHelper.getDatabase().rawQuery("select min(weight), date from training where date between ? and ?",
                new String[] {dateStart.toString(), dateEnd.toString()});
        maxWeight.setText("Максимальный");
        minWeight.setText("Минимальный");
        if (cursorMaxWeight.moveToNext() && cursorMaxWeight.getString(0) != null
                && cursorMaxWeight.getString(1) != null) {
            maxWeightValue.setText(String.valueOf(cursorMaxWeight.getString(0)));
            System.out.println("max  " + cursorMaxWeight.getString(1) + "    " + cursorMaxWeight.getString(0));
            maxWeightDate.setText(cursorMaxWeight.getString(1).substring(2));
            initGrath(dateStart.toString(), dateEnd.toString());
        } else {
            maxWeightValue.setText("-");
            maxWeightValue.setText("-");
            graph.setVisibility(View.INVISIBLE);
        }
        if (cursorMinWeight.moveToNext() && cursorMinWeight.getString(0) != null
                && cursorMinWeight.getString(1) != null) {
            minWeightValue.setText(cursorMinWeight.getString(0));
            minWeightDate.setText(cursorMinWeight.getString(1).substring(2));
        } else {
            minWeightValue.setText("-");
            minWeightValue.setText("-");
        }
        cursorMaxWeight.close();
        cursorMinWeight.close();
    }

    private void initGrath(String dateStart, String dateEnd){
        graph.setVisibility(View.VISIBLE);
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

        Date maxDate = null, minDate = null;

        Cursor cursor = MyHelper.MyDBHelper.getDatabase().rawQuery(
                "SELECT date, weight FROM training WHERE date BETWEEN ? AND ? ORDER BY date",
                new String[] {dateStart, dateEnd});
        if (cursor.moveToNext()) {
            try {
                minDate = dateFormat.parse(cursor.getString(0));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            cursor.moveToPrevious();
        }
        while (cursor.moveToNext()) {
            try {
                System.out.println("date" + dateFormat.parse(cursor.getString(0)) + " " + cursor.getFloat(1));
                series.appendData(new DataPoint(dateFormat.parse(cursor.getString(0)), cursor.getFloat(1)), false, 25);
            } catch (ParseException e) {
                System.out.println("crash");
                e.printStackTrace();
            }
        }
        System.out.println("get max date");
        cursor.moveToPrevious();
        try {
            maxDate = dateFormat.parse(cursor.getString(0));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        series.setColor(Color.parseColor("#CC461B"));
        graph.addSeries(series);
        graph.getViewport().setMinX(minDate.getTime());
        graph.getViewport().setMaxX(maxDate.getTime());
    }
}
