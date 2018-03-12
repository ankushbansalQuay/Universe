package com.universe.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.universe.android.R;
import com.universe.android.helper.FontClass;
import com.universe.android.model.UserModel;
import com.universe.android.utility.Utility;

import java.util.ArrayList;

/**
 * Created by gaurav.pandey on 24-01-2018.
 */

public class WorkFLowUserAdapter extends RecyclerView.Adapter<WorkFLowUserAdapter.SurveyViewHolder> {
    private Context mContext;
    private ArrayList<UserModel> stringArrayList;
    private OnItemSelecteListener mListener;

    public WorkFLowUserAdapter(Context mContext, ArrayList<UserModel> stringArrayList) {
        this.mContext = mContext;
        this.stringArrayList = stringArrayList;

    }

    @Override
    public SurveyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.workflow_user_item, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(SurveyViewHolder holder, int position) {
        holder.textViewCD.setTypeface(FontClass.openSansBold(mContext));
        holder.textViewStatus.setTypeface(FontClass.openSansRegular(mContext));

        if (Utility.validateString(stringArrayList.get(position).getUserName()))
        holder.textViewCD.setText(stringArrayList.get(position).getUserName());

            holder.imgCD.setText(stringArrayList.get(position).getUserStatus());
        holder.textViewStatus.setText(stringArrayList.get(position).getUserDateStatus());





    }

    public void setOnItemClickLister(OnItemSelecteListener mListener) {
        this.mListener = mListener;
    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    public interface OnItemSelecteListener {
        public void onItemSelected(View v, int position);
    }

    public class SurveyViewHolder extends RecyclerView.ViewHolder {
        private TextView imgCD, textViewCD, textViewStatus;
        private ImageView imgArrow;
        public SurveyViewHolder(View itemView) {
            super(itemView);
            imgCD = itemView.findViewById(R.id.imgCD);
            textViewCD = itemView.findViewById(R.id.textViewCD);
            textViewStatus=itemView.findViewById(R.id.textViewStatus);
            imgArrow=itemView.findViewById(R.id.imgArrow);

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemSelected(view, getAdapterPosition());

                }
            });*/

        }
    }
}