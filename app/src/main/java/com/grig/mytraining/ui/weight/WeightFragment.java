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
//    DBHelper dbHelper;
//    SQLiteDatabase database;
    Float maxWeightText, minWeightText;
    TextView maxWeightValue, minWeightValue, maxWeight, minWeight, maxWeightDate, minWeightDate;
    String maxWeightDateText, minWeightDateText;
    Spinner spinnerTimeInterval;
    ArrayAdapter<String> arrayAdapterSpinnerTimeInterval;
    GraphView graph;
//    LineChart chart;
    @SuppressLint("SimpleDateFormat")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_weight, container, false);

//        chart = view.findViewById(R.id.chart);
        firstDate = MyHelper.MyDBHelper.getFirstDate();
        graph = view.findViewById(R.id.graph);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getActivity()));
        graph.getGridLabelRenderer().setNumHorizontalLabels(4); // only 4 because of the space
        graph.getGridLabelRenderer().setNumVerticalLabels(6); // only 4 because of the space
        graph.getViewport().setXAxisBoundsManual(false);
        graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(getContext(), new SimpleDateFormat("dd.MM.yy")));

//
//        EditText editTextStartDateWeight = view.findViewById(R.id.editTextStartDateWeight);
//        EditText editTextEndDateWeight = view.findViewById(R.id.editTextEndDateWeight);
//        currentDate = new Date().getTime();
//        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yy", Locale.getDefault());
//        String date = dateFormat.format(currentDate);
//        editTextStartDateWeight.setHint(date);
//        editTextEndDateWeight.setHint(date);
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
//        setChart();
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

// you can directly pass Date objects to DataPoint-Constructor
// this will convert the Date to double via Date#getTime()
//        System.out.println(d4);
        series.setColor(Color.parseColor("#CC461B"));
        graph.addSeries(series);

// set date label formatter

// set manual x bounds to have nice steps
//        System.out.println(d1);
//        System.out.println(d4);
        graph.getViewport().setMinX(minDate.getTime());
        graph.getViewport().setMaxX(maxDate.getTime());
//        graph.getViewport().setMinY(graph.getViewport().getMinY(true)-12);

// as we use dates as labels, the human rounding to nice readable numbers
// is not necessary

//        Date date1 = new Date();
//        try {
//            date1 = date1 = new SimpleDateFormat("dd/MM/yyyy").parse("30/02/2022");
//            System.out.println("parse");
//        } catch (ParseException e) {
//            System.out.println("cath");
//            e.printStackTrace();
//        }
//
//        System.out.println(date1);
//
//        LineGraphSeries<DataPoint> series = new LineGraphSeries<>();
//        series.appendData(new DataPoint(date1, 2), true, 25);
//        graph.addSeries(series);
//        graph.getGridLabelRenderer().setNumHorizontalLabels(2);
//        graph.getGridLabelRenderer().setHumanRounding(false);
    }

//    public void setChart() {
//        ArrayList<Entry> entries = new ArrayList<>();
//        XAxis xAxis = chart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new LineChartXAxisValueFormatter());
////        xAxis.setLabelsToSkip(0);
////        entries.add(new Entry(secondsSince1970Float, yValueFloat));
////        new BarEntry(TimeUnit.MILLISECONDS.toDays((long)1), 1);
//        System.out.println(new Date().getTime());
//        entries.add(new Entry(-100, 80));
//        entries.add(new Entry(2, 80));
//        entries.add(new Entry(100, 80));
////        entries.add(new Entry(TimeUnit.MILLISECONDS.toDays(2), 79));
////        entries.add(new Entry(TimeUnit.MILLISECONDS.toDays(3), 74));
////        entries.add(new Entry(TimeUnit.MILLISECONDS.toDays(4), 78));
////        entries.add(new Entry(TimeUnit.MILLISECONDS.toDays(5), 80));
////        entries.add(new Entry(3, 1));
////        entries.add(new Entry(4, 1));
////        entries.add(new Entry(5, 4));
////        entries.add(new Entry(6, 1));
//
//// На основании массива точек создадим первую линию с названием
//        LineDataSet dataset = new LineDataSet(entries, "График первый");
//        dataset.setMode(LineDataSet.Mode.CUBIC_BEZIER);
//        chart.animateY(500);
////        new Entry(secondsSince1970Float, yValueFloat)
//
//// Создадим переменную данных для графика
//        LineData data = new LineData(dataset);
//// Передадим данные для графика в сам график
//        chart.setData(data);
//        chart.getAxisLeft().setTextColor(Color.WHITE); // left y-axis
//        chart.getXAxis().setTextColor(Color.WHITE);
//        chart.getAxisLeft().setTextColor(Color.WHITE);
//        chart.getLegend().setTextColor(Color.WHITE);
//        chart.getDescription().setTextColor(Color.WHITE);
//
//// Не забудем отправить команду на перерисовку кадра, иначе график не отобразится
//        chart.invalidate();
//    }
//    public class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {
//
//        @Override
//        public String getFormattedValue(float value) {
//            System.out.println(value);
//
//            // Convert float value to date string
//            // Convert from seconds back to milliseconds to format time  to show to the user
//            long a = 1000*60*60*24*365;
//            long mill = (long)value*1000*60*60*24 + (new Date().getTime()-1000*60*60*24*365);
//
//            // Show time in local version
//            Date timeMilliseconds = new Date(mill);
//            System.out.println(timeMilliseconds);
//            SimpleDateFormat sdf = new SimpleDateFormat("dd MM");
//
//            return sdf.format(mill);
//        }
//    }
}
