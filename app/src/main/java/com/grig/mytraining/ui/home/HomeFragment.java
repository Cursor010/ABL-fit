package com.grig.mytraining.ui.home;

import static androidx.recyclerview.widget.RecyclerView.OVER_SCROLL_NEVER;
import static com.grig.mytraining.MyApplication.getAppContext;
import static org.threeten.bp.LocalDate.now;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.style.ImageSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.grig.mytraining.MainActivity;
import com.grig.mytraining.MyHelper;
import com.grig.mytraining.R;
import com.grig.mytraining.ui.home.notes.NotesActivity;
import com.grig.mytraining.ui.home.settings.SettingsActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import org.threeten.bp.LocalDate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class HomeFragment extends Fragment {
    Animation animButtonPlus;
    Animation animButtonDumbbell, animButtonDumbbellFirst;
    Animation animButtonNote, animButtonNoteRight, animButtonNoteLeft, animButtonNoteFirst, animButtonNoteBack;
    FloatingActionButton floatingButtonPlus, floatingButtonNote, floatingButtonDumbbell;
    ImageButton imageButtonMySettings;
    boolean isTurn = false;
    boolean isQueryCalendar = false, isQueryViewPager = false;
    MaterialCalendarView mcv;
    ViewPager2 viewPager2;
    LocalDate currentDate = now();
    ProgressBar progressBar;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mcv = view.findViewById(R.id.calendarView);
        viewPager2 = view.findViewById(R.id.viewPager);

        progressBar = view.findViewById(R.id.progressBar);
        imageButtonMySettings = view.findViewById(R.id.imageButtonMySettings);
        floatingButtonPlus = view.findViewById(R.id.floatingButtonPlus);
        floatingButtonDumbbell = view.findViewById(R.id.floatingButtonDumbbell);
        floatingButtonNote = view.findViewById(R.id.floatingButtonNote);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        new Handler().postDelayed(this::initAll, 100);
    }

    private void initAll() {
        progressBar.setVisibility(View.VISIBLE);
        floatingButtonPlus.setOnClickListener(view1 -> startAnimationFloatingButtons(view1.getContext()));

        imageButtonMySettings.setOnClickListener(v -> startActivity(new Intent(v.getContext(), SettingsActivity.class)));
        floatingButtonDumbbell.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), CreateTrainActivity.class);
            startActivity(intent);
        });

        floatingButtonNote.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NotesActivity.class);
            startActivity(intent);
        });

        initCalendar();
        progressBar.setVisibility(View.INVISIBLE);
        initViewPager();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        floatingButtonDumbbell.setVisibility(View.INVISIBLE);
        floatingButtonNote.setVisibility(View.INVISIBLE);
        isTurn = false;
    }
    private void initCalendar() {
        mcv.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.months)));

        mcv.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));

        // Добавляем декораторы
        mcv.addDecorator(new TodayDecorator()); // Декоратор для текущего дня
        mcv.addDecorator(new FullDecorator(MyHelper.MyDBHelper.TrainingDaysFromDB.trainingDays));
        mcv.addDecorator(new EventTrainingDecorator(MyHelper.MyDBHelper.TrainingDaysFromDB.trainingDays));

        mcv.setRightArrow(R.drawable.chevron_right);
        mcv.setLeftArrow(R.drawable.chevron_left);

        mcv.setOnDateChangedListener((widget, date, selected) -> {
            if (MyHelper.MyDBHelper.TrainingDaysFromDB.trainingDays.contains(date)) {
                ((MainActivity) getActivity()).switchToChronologyFromCalendar(
                        LocalDate.of(date.getYear(), date.getMonth(), date.getDay()).toString()
                );
            }
        });

        mcv.setOnMonthChangedListener((widget, date) -> {
            if (date.getMonth() <= currentDate.getMonthValue()
                    && date.getYear() == currentDate.getYear() && !isQueryViewPager) {
                isQueryCalendar = true;
                viewPager2.setCurrentItem(date.getMonth() - 1);
            }
            isQueryViewPager = false;
        });
        mcv.setVisibility(View.VISIBLE);
    }

    public void initViewPager() {
        List<String> months = Arrays.asList(getResources().getStringArray(R.array.months));

        List<SliderStatItem> stats = new ArrayList<>();
        List<Integer> monthsTrainings = new ArrayList<>();
        for (CalendarDay calendarDay : MyHelper.MyDBHelper.TrainingDaysFromDB.trainingDays) {
            if (calendarDay.getYear() == currentDate.getYear()) {
                // Получаем месяц (1-12) и корректируем индекс для списка (0-11)
                monthsTrainings.add(calendarDay.getMonth() - 1);
            }
        }


        for (int i = 0; i < currentDate.getMonthValue(); i++) {
            // Используем i как индекс (0-11), соответствующий массиву
            stats.add(new SliderStatItem(months.get(i), String.valueOf(Collections.frequency(monthsTrainings, i))));
        }
        viewPager2.setAdapter(new SliderAdapter(stats, viewPager2));        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(3);
        viewPager2.getChildAt(0).setOverScrollMode(OVER_SCROLL_NEVER);
        viewPager2.setCurrentItem(currentDate.getMonthValue());

        viewPager2.setPageTransformer((page, position) -> {
            page.setTranslationX(0);
            float r = 1 - Math.abs(position);
            page.setScaleY(0.60f + r * 0.4f);
            page.setScaleX(0.60f + r * 0.4f);
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                if (!isQueryCalendar) {
                    isQueryViewPager = true;
                    new Handler().postDelayed(() -> mcv.setCurrentDate(CalendarDay.from(
                            currentDate.getYear(), position % currentDate.getMonthValue() + 1, 1), true), 200);
                }
                isQueryCalendar = false;
            }
        });
    }

    private static ArrayList<Drawable> getCalendarDesign() {
        ArrayList<Drawable> designs = new ArrayList<>();
        @SuppressLint("UseCompatLoadingForDrawables") Drawable designDefault = getAppContext().getDrawable(R.drawable.calendar_design_deffault);
        @SuppressLint("UseCompatLoadingForDrawables") Drawable designSpec = getAppContext().getDrawable(R.drawable.calendar_design_deffault_spec);
        designs.add(designDefault);
        designs.add(designSpec);
        return designs;
    }

    private static class EventTrainingDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> mCalendarDayCollection;

        public EventTrainingDecorator(HashSet<CalendarDay> calendarDayCollection) {
            mCalendarDayCollection = calendarDayCollection;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            LocalDate today = LocalDate.now();
            return mCalendarDayCollection.contains(day) &&
                    !(day.getYear() == today.getYear()
                            && day.getMonth() == today.getMonthValue()
                            && day.getDay() == today.getDayOfMonth());
        }

        @Override
        public void decorate(DayViewFacade view) {
            Drawable d = ContextCompat.getDrawable(getAppContext(), R.drawable.dumbbell4);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            view.addSpan(new TextAppearanceSpan(getAppContext(), R.style.CalendarTVStyle));
            view.setSelectionDrawable(getCalendarDesign().get(1));
        }
    }

    private static class FullDecorator implements DayViewDecorator {
        private final HashSet<CalendarDay> mCalendarDayCollection;

        public FullDecorator(HashSet<CalendarDay> calendarDayCollection) {
            mCalendarDayCollection = calendarDayCollection;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            LocalDate today = LocalDate.now();
            return !mCalendarDayCollection.contains(day) &&
                    !(day.getYear() == today.getYear()
                            && day.getMonth() == today.getMonthValue()
                            && day.getDay() == today.getDayOfMonth());
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.setSelectionDrawable(getCalendarDesign().get(0));
        }
    }

    private class TodayDecorator implements DayViewDecorator {
        private final CalendarDay today;

        public TodayDecorator() {
            this.today = CalendarDay.from(LocalDate.now());
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(today);
        }

        @Override
        public void decorate(DayViewFacade view) {
            Drawable d = ContextCompat.getDrawable(getAppContext(), R.drawable.dumbbell4);
            d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE);
            view.addSpan(new TextAppearanceSpan(getAppContext(), R.style.CalendarTodayStyle));
            view.setSelectionDrawable(getCalendarDesign().get(1));
        }
    }

    private void startAnimationFloatingButtons(@NonNull Context context) {
        if (!isTurn) {
            animButtonPlus = AnimationUtils.loadAnimation(context, R.anim.rotate_plus_to_cross);
            animButtonDumbbell = AnimationUtils.loadAnimation(context, R.anim.anim_dumbbell_appear);
            animButtonNoteRight = AnimationUtils.loadAnimation(context, R.anim.anim_note_appear_roteate_right);
            animButtonNoteLeft = AnimationUtils.loadAnimation(context, R.anim.anim_note_appear_rotete_left);
            animButtonNoteBack = AnimationUtils.loadAnimation(context, R.anim.anim_note_appear_rotate_back);

            animButtonDumbbellFirst = AnimationUtils.loadAnimation(context, R.anim.anim_dumbell_first_appear);
            animButtonNoteFirst = AnimationUtils.loadAnimation(context, R.anim.anim_note_first_appear);

            floatingButtonNote.startAnimation(animButtonNoteFirst);
            new Handler().postDelayed(() -> {
                if (isTurn)
                    floatingButtonNote.startAnimation(animButtonNoteRight);
            }, 160);
            new Handler().postDelayed(() -> {
                if (isTurn)
                    floatingButtonNote.startAnimation(animButtonNoteLeft);
            }, 110 + 160);
            new Handler().postDelayed(() -> {
                if (isTurn)
                    floatingButtonNote.startAnimation(animButtonNoteBack);
            }, 320 + 160);

            floatingButtonDumbbell.startAnimation(animButtonDumbbellFirst);
            new Handler().postDelayed(() -> {
                if (isTurn)
                    floatingButtonDumbbell.startAnimation(animButtonDumbbell);
            }, 160);


            floatingButtonDumbbell.setVisibility(View.VISIBLE);
            floatingButtonNote.setVisibility(View.VISIBLE);
        } else {
            animButtonPlus = AnimationUtils.loadAnimation(context, R.anim.rotate_cross_to_plus);
            animButtonDumbbell = AnimationUtils.loadAnimation(context, R.anim.anim_dumbbell_disappear);
            animButtonNote = AnimationUtils.loadAnimation(context, R.anim.anim_note_disappear);
            floatingButtonNote.startAnimation(animButtonNote);
            floatingButtonDumbbell.startAnimation(animButtonDumbbell);

            floatingButtonDumbbell.setVisibility(View.INVISIBLE);
            floatingButtonNote.setVisibility(View.INVISIBLE);
        }
        isTurn = !isTurn;
        floatingButtonPlus.startAnimation(animButtonPlus);
    }
}