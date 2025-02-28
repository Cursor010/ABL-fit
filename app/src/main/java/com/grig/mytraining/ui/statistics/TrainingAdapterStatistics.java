//package com.grig.mytraining.ui.statistics;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.grig.mytraining.R;
//import com.grig.mytraining.ui.Record;
//import com.grig.mytraining.ui.Training;
//
//import java.util.List;
//
//public class TrainingAdapterStatistics extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
//    private final List<Object> items;
//    private final byte TRAINING = 0, RECORD = 1;
//
//    public TrainingAdapterStatistics(List<Object> items) {
//        this.items = items;
//    }
//    @NonNull
//    @Override
//    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
////        RecyclerView.ViewHolder viewHolder;
//        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
//        if (viewType == RECORD) {
//            View v1 = inflater.inflate(R.layout.list_item_record_statistics, parent, false);
//            return new ViewHolder2(v1);
//        }
//        View v2 = inflater.inflate(R.layout.list_item_statistics, parent, false);
//        return new ViewHolder1(v2);
//    }
//
//    @Override
//    public int getItemViewType(int position) {
//        if (items.get(position) instanceof Training) {
//            return TRAINING;
//        } else if (items.get(position) instanceof Record) {
//            return RECORD;
//        }
//        return -1;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
//        switch (this.getItemViewType(position)) {
//            case TRAINING:
//                ViewHolder1 mHolder1 = (ViewHolder1) holder;
//                Training training = (Training) items.get(position);
//                mHolder1.dateView.setText(training.date);
//                mHolder1.additionalInfoView.setText(training.additionalInfo);
//                mHolder1.weightView.setText(training.weight);
//                break;
//            case RECORD:
//                ViewHolder2 mHolder2 = (ViewHolder2) holder;
//                Record record = (Record) items.get(position);
//                mHolder2.dateView.setText(record.getDate());
//                mHolder2.additionalInfoView.setText(record.getAdditionalInfo());
//                mHolder2.weightView.setText(record.getWeight());
//                break;
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return items.size();
//    }
//
//    public static class ViewHolder1 extends RecyclerView.ViewHolder {
//        final TextView dateView, additionalInfoView, weightView;
//        ViewHolder1(View view){
//            super(view);
//            dateView = view.findViewById(R.id.tvDateStat);
//            additionalInfoView = view.findViewById(R.id.tvAdditionalInfoStat);
//            weightView = view.findViewById(R.id.tvWeightStat);
//        }
//    }
//        public static class ViewHolder2 extends RecyclerView.ViewHolder {
//        final TextView dateView, additionalInfoView, weightView;
//        ViewHolder2(View view){
//            super(view);
//            dateView = view.findViewById(R.id.tvRecordDateStat);
//            additionalInfoView = view.findViewById(R.id.tvRecordAdditionalInfoStat);
//            weightView = view.findViewById(R.id.tvRecordWeightStat);
//        }
//    }
//}