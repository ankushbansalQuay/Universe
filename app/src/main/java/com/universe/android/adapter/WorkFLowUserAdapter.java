package com.universe.android.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.universe.android.R;
import com.universe.android.enums.DesignationEnum;
import com.universe.android.helper.FontClass;
import com.universe.android.listneners.PageChangeInterface;
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
    private int lastCheckedPosition = -1;
    SparseBooleanArray mSelectedItems = new SparseBooleanArray();
    private PageChangeInterface pageChangeInterface;


    public WorkFLowUserAdapter(Context mContext, ArrayList<UserModel> stringArrayList,PageChangeInterface pageChangeInterface1) {
        this.mContext = mContext;
        this.stringArrayList = stringArrayList;
        pageChangeInterface=pageChangeInterface1;

    }

    @Override
    public SurveyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.workflow_user_item, parent, false);
        return new SurveyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SurveyViewHolder holder, final int position) {
        holder.textViewCD.setTypeface(FontClass.openSansBold(mContext));
        holder.textViewStatus.setTypeface(FontClass.openSansRegular(mContext));

        if (Utility.validateString(stringArrayList.get(position).getUserDateStatus()))
        holder.textViewCD.setText(stringArrayList.get(position).getUserDateStatus());

        if ("Submitted".equalsIgnoreCase(stringArrayList.get(position).getUserStatus())) {
            holder.imgCD.setBackgroundResource(R.drawable.green_circle);
        }else if ("Initiated".equalsIgnoreCase(stringArrayList.get(position).getUserStatus())){
            holder.imgCD.setBackgroundResource(R.drawable.yellow_circle);
        }else  if ("Approved".equalsIgnoreCase(stringArrayList.get(position).getUserStatus())){
            holder.imgCD.setBackgroundResource(R.drawable.green_circle);
        }else if ("Rejected".equalsIgnoreCase(stringArrayList.get(position).getUserStatus())){
            holder.imgCD.setBackgroundResource(R.drawable.red_circle);
        }else if ("".equalsIgnoreCase(stringArrayList.get(position).getUserStatus())){
           // holder.imgCD.setBackgroundResource(R.drawable.red_circle);
        }else {

        }

        holder.textViewStatus.setText(stringArrayList.get(position).getUserDateStatus());
        holder.textViewStatus.setVisibility(View.GONE);
        if(position == lastCheckedPosition) {
            holder.imgArrow.setImageResource(R.drawable.arrow_down);
        } else {
            holder.imgArrow.setImageResource(R.drawable.icon_right_arrow);
        }
        holder.llUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.animateView(v);
              //  holder.imgArrow.setImageResource(R.drawable.arrow_down);


                if(position == lastCheckedPosition) {
                    holder.imgArrow.setImageResource(R.drawable.icon_right_arrow);
                    mSelectedItems.put(position, false);
                    pageChangeInterface.onDataPass("",position,"0");
                } else {
                    holder.imgArrow.setImageResource(R.drawable.arrow_down);
                    mSelectedItems.put(position, true);
                    pageChangeInterface.onDataPass("",position,"1");
                }
                lastCheckedPosition = holder.getAdapterPosition();

                notifyDataSetChanged();




            }
        });






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
        private LinearLayout llUser;
        public SurveyViewHolder(View itemView) {
            super(itemView);
            imgCD = itemView.findViewById(R.id.imgCD);
            textViewCD = itemView.findViewById(R.id.textViewCD);
            textViewStatus=itemView.findViewById(R.id.textViewStatus);
            imgArrow=itemView.findViewById(R.id.imgArrow);
            llUser=itemView.findViewById(R.id.llUser);

           /* itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemSelected(view, getAdapterPosition());

                }
            });*/

        }
    }
}
