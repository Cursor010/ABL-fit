//package com.grig.mytraining.ui.chronology
//
//import android.annotation.SuppressLint
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.view.ViewGroup
//import android.widget.ArrayAdapter
//import android.widget.RadioButton
//import com.google.android.material.bottomsheet.BottomSheetDialogFragment
//import com.grig.mytraining.MyHelper
//import com.grig.mytraining.R
//import kotlinx.android.synthetic.main.filter_chron_bottom_sheet.*
//import java.time.LocalDate
//
//class FragmentFilterChron : BottomSheetDialogFragment() {
//    @SuppressLint("ResourceAsColor", "PrivateResource", "ResourceType")
//    override fun onCreateView(
//        inflater: LayoutInflater,
//        container: ViewGroup?,
//        savedInstanceState: Bundle?
//    ):
//            View? {
//        return inflater.inflate(R.layout.filter_chron_bottom_sheet, container, false)
//    }
//
//    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
//        super.onViewCreated(view, savedInstanceState)
//        autoCompleteTextView2.setAdapter(ArrayAdapter(view.context, R.layout.auto_compleate, MyHelper.MyDBHelper.getExercises()))
//    }
//
//    private fun applyChanged() {
//        this.dismiss()
//        ChronologyFragment.datesInterval = getDatesIntervalFromString(view?.findViewById<RadioButton>
//            (radioGroup.checkedRadioButtonId)?.text.toString())
//        ChronologyFragment.exercise = autoCompleteTextView2.text.toString()
//    }
//
//    private fun getDatesIntervalFromString(interval: String): Array<String> {
//        return when (interval) {
//            "Две недели" -> arrayOf(LocalDate.now().minusWeeks(2).toString(), LocalDate.now().toString())
//            "Месяц" -> arrayOf(LocalDate.now().minusMonths(1).toString(), LocalDate.now().toString())
//            "Два месяца" -> arrayOf(LocalDate.now().minusMonths(2).toString(), LocalDate.now().toString())
//            else -> arrayOf(LocalDate.MIN.toString(), LocalDate.now().toString())
//        }
//    }
//}