package com.universe.android.activity;

import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;


import com.universe.android.R;
import com.universe.android.adapter.DrawerAdapter;
import com.universe.android.fragment.SurveySelectionFragment;
import com.universe.android.model.DrawerItem;
import com.universe.android.utility.AppConstants;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private Toolbar mToolbar;
    private LinearLayoutManager layoutManager;
    private DrawerAdapter mDrawerAdapter;
    private RecyclerView mRecyclerView;
    private ArrayList<DrawerItem> mDrawerItemList;

    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private int mContainerId;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initialization();
        setUpElements();
        setUpListeners();
    }

    private void setUpListeners() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();


        mDrawerAdapter.setOnItemClickLister(new DrawerAdapter.OnItemSelecteListener() {
            @Override
            public void onItemSelected(View v, int position) {
                addFragment(new SurveySelectionFragment(), mContainerId);
            }
        });
    }


    private void setUpElements() {
        //Dummy Data
        mDrawerItemList = new ArrayList<DrawerItem>();
        DrawerItem item = new DrawerItem();
        item.setIcon(R.drawable.ic_dashboard);
        item.setTitle("Dash Board");
        mDrawerItemList.add(item);

        DrawerItem item2 = new DrawerItem();
        item2.setIcon(R.drawable.survey_report);
        item2.setTitle("Survey Report");
        mDrawerItemList.add(item2);

        DrawerItem item3 = new DrawerItem();
        item3.setIcon(R.drawable.survey_report);
        item3.setTitle("Sync Responses");
        mDrawerItemList.add(item3);

        DrawerItem item4 = new DrawerItem();
        item4.setIcon(R.drawable.survey_report);
        item4.setTitle("Questionaire");
        mDrawerItemList.add(item4);

        DrawerItem item5 = new DrawerItem();
        item5.setIcon(R.drawable.ic_analytics);
        item5.setTitle("Analytics");
        mDrawerItemList.add(item5);

        DrawerItem item6 = new DrawerItem();
        item6.setIcon(R.drawable.managementteams);
        item6.setTitle("Manage Teams");
        mDrawerItemList.add(item6);

        DrawerItem item7 = new DrawerItem();
        item7.setIcon(R.drawable.help);
        item7.setTitle("Help");
        mDrawerItemList.add(item7);

        DrawerItem item8 = new DrawerItem();
        item8.setIcon(R.drawable.survey_report);
        item8.setTitle("Update Application");
        mDrawerItemList.add(item8);

        DrawerItem item9 = new DrawerItem();
        item9.setIcon(R.drawable.ic_logout);
        item9.setTitle("Logout");
        mDrawerItemList.add(item9);

        mDrawerAdapter = new DrawerAdapter(mDrawerItemList, mContext);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mDrawerAdapter);

    }

    private void initialization() {
        mToolbar = findViewById(R.id.appBar);
        setSupportActionBar(mToolbar);
        mRecyclerView = findViewById(R.id.drawerRecyclerView);
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mContainerId = R.id.fragment_container;


    }


}
