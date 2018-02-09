package com.universe.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.universe.android.R;
import com.universe.android.activity.SurveyDetailActivity;
import com.universe.android.helper.FontClass;
import com.universe.android.utility.AppConstants;
import com.universe.android.workflows.WorkFlowsDetailActivity;

/**
 * Created by gaurav.pandey on 24-01-2018.
 */

public class SurveySelectionFragment extends BaseFragment {
    View view;
    private CardView cardViewRetailer, cardViewDistributor;
    private TextView textViewReatilers, retailerPending, textViewReatilersDate, textViewDistributor;
    private TextView textViewReatilersDistributor, distributorPending, textViewSelectRetailer, textViewSelectDistributor;
    private String strType;

    public static SurveySelectionFragment newInstance(String type) {
        SurveySelectionFragment myFragment = new SurveySelectionFragment();

        Bundle args = new Bundle();
        args.putString(AppConstants.TYPE, type);
        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strType = getArguments().getString(AppConstants.TYPE);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.surveydetailsselection, container, false);
        initialization();
        setupElemenets();
        return view;
    }

    private void setupElemenets() {
        cardViewRetailer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SurveyDetailActivity.class);
                if (strType.equalsIgnoreCase(AppConstants.WORKFLOWS)) {
                    intent = new Intent(getActivity(), WorkFlowsDetailActivity.class);
                    intent.putExtra(AppConstants.TYPE, strType);
                }

                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
        cardViewDistributor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SurveyDetailActivity.class);
                if (strType.equalsIgnoreCase(AppConstants.WORKFLOWS)) {
                    intent = new Intent(getActivity(), WorkFlowsDetailActivity.class);
                    intent.putExtra(AppConstants.TYPE, strType);
                }
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    private void initialization() {
        cardViewRetailer = view.findViewById(R.id.retailerSelection);
        cardViewDistributor = view.findViewById(R.id.retailerDistributor);
        textViewSelectRetailer = view.findViewById(R.id.textViewSelectRetailer);
        textViewSelectDistributor = view.findViewById(R.id.textViewSelectDistributor);
        textViewReatilersDate = view.findViewById(R.id.textViewReatilersDate);
        textViewReatilersDistributor = view.findViewById(R.id.textViewDistributorDate);
        retailerPending = view.findViewById(R.id.retailerPending);
        distributorPending = view.findViewById(R.id.distributorPending);
        textViewDistributor = view.findViewById(R.id.textViewDistributor);
        textViewReatilers = view.findViewById(R.id.textViewReatilers);

        textViewDistributor.setTypeface(FontClass.openSansBold(getActivity()));
        textViewReatilers.setTypeface(FontClass.openSansBold(getActivity()));
        textViewReatilersDate.setTypeface(FontClass.openSansLight(getActivity()));
        textViewReatilersDistributor.setTypeface(FontClass.openSansLight(getActivity()));
        textViewSelectDistributor.setTypeface(FontClass.openSansRegular(getActivity()));
        retailerPending.setTypeface(FontClass.openSansRegular(getActivity()));
        distributorPending.setTypeface(FontClass.openSansRegular(getActivity()));
        textViewSelectRetailer.setTypeface(FontClass.openSansRegular(getActivity()));
    }


}
