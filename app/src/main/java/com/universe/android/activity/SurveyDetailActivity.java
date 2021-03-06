package com.universe.android.activity;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.renderscript.ScriptIntrinsicYuvToRGB;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.universe.android.R;
import com.universe.android.adapter.CustomerListAdapter;
import com.universe.android.adapter.StatusAdapter;
import com.universe.android.adapter.SurveyDetailAdapter;
import com.universe.android.adapter.WorkFLowDetailAdapter;
import com.universe.android.enums.DesignationEnum;
import com.universe.android.fragment.SurveyDetailDialogFragment;
import com.universe.android.helper.FontClass;

import com.universe.android.helper.RecyclerTouchListener;
import com.universe.android.model.AnswersModal;
import com.universe.android.model.CustomerModal;
import com.universe.android.model.StatusModel;
import com.universe.android.realmbean.RealmAnswers;
import com.universe.android.realmbean.RealmCustomer;
import com.universe.android.realmbean.RealmSurveys;
import com.universe.android.utility.AppConstants;
import com.universe.android.utility.Prefs;
import com.universe.android.utility.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by gaurav.pandey on 24-01-2018.
 */

public class SurveyDetailActivity extends BaseActivity implements SurveyDetailDialogFragment.SetDataListListener {
    //decalre the Views here
    private static final int MY_PERMISSIONS_REQUEST_WRITE_STORAGE = 1000;
    private RecyclerView recyclerViewSurveyDetail;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SwipeRefreshLayout swipeRefreshLayoutStatus;
    private ImageView imageViewBack, imageViewFilter, imageViewDownload;
    private FloatingActionMenu floatingActionMenu;
    private FloatingActionButton floatingCrystal, floatingRetailers;
    private TextView textViewToday, textViewtarget, textViewAchievement, textViewInProgress;
    private TextView textViewNewRetailers, textViewCrystalmembers, textViewCompletedQuestionaire;
    private LinearLayoutManager linearLayoutManager;
    private SurveyDetailAdapter surveyDetailAdapter;
    private TextView textViewtargetCount, textViewCompletedCount, textViewAchievementPercentage, textViewInProgressCount;
    private TextView textViewNewRetailersCount, textViewCrystalMembersCount;
    private RelativeLayout relativelayoutTarget, relativeLayoutSubmitted, realtiveLayoutAchivement, relativelayoutInprogress;
    private RelativeLayout realativeNewRetailers, relativeLayoutCrystalCustomers;
    private ArrayList<AnswersModal> stringArrayList;

    FragmentManager fm = getSupportFragmentManager();
    private String fromDate, toDate, statusData;
    Intent intent;
    private String surveyId;
    private ArrayList<CustomerModal> stringArrayListCutomer;
    Date fromDateTime = null;
    Date toDates = null;
    private ArrayList<String> headerList = new ArrayList<>();

    long realmCystal = 0;
    long realmSubmitted = 0;
    long realmInprogress = 0;
    long realmNewRetailer = 0;

    private CardView cardViewDownaload;
    private TextView textViewTitle;
    private String titleString;
    private String fromdate, todate;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.surveyreportfragment);
        initialization();
        prepareHeaderList();
        prepareList(getString(R.string.completed));
        setUpElements();
        setUpListeners();
        setUpClickListeners();
        setCounts();
    }

    private void prepareHeaderList() {
        headerList = new ArrayList<>();
        // headerList.add("#");
        List<String> tablesHeader = new ArrayList<>();
        tablesHeader = Arrays.asList(getResources().getStringArray(R.array.survey_report));
        for (int i = 0; i < tablesHeader.size(); i++) {
            headerList.add(tablesHeader.get(i));
        }

    }

    private void setCountsFilter(String type, Date fromDate, Date toDate) {
        Realm realm = Realm.getDefaultInstance();
        String designation = Prefs.getStringPrefs(AppConstants.TYPE);
        try {
            int count = 0;
            RealmSurveys realmSurveys = realm.where(RealmSurveys.class).equalTo(AppConstants.ID, surveyId).findFirst();
            if (realmSurveys != null) {
                count = realmSurveys.getTarget();
            }
            realmSubmitted = realm.where(RealmAnswers.class).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.requester_status, "1").count();
            realmInprogress = realm.where(RealmAnswers.class).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.requester_status, "5").count();
            realmNewRetailer = realm.where(RealmAnswers.class).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.CUSTOMER, AppConstants.NEW).equalTo(AppConstants.requester_status, "1").count();
            realmCystal = realm.where(RealmAnswers.class).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.CUSTOMER, AppConstants.CrystalCustomer).equalTo(AppConstants.requester_status, "1").count();

            int n = count;
            int v = (int) realmSubmitted;
            int percent = v * 100 / n;
            textViewtargetCount.setText(String.valueOf(count));
            textViewCompletedCount.setText(String.valueOf(realmSubmitted));
            textViewAchievementPercentage.setText(String.valueOf(percent).concat("%"));
            textViewInProgressCount.setText(String.valueOf(realmInprogress));
            textViewNewRetailersCount.setText(String.valueOf(realmNewRetailer));
            textViewCrystalMembersCount.setText(String.valueOf(realmCystal));
        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }

    private void setCounts() {
        Realm realm = Realm.getDefaultInstance();
        try {
            int count = 0;
            RealmSurveys realmSurveys = realm.where(RealmSurveys.class).equalTo(AppConstants.ID, surveyId).findFirst();
            if (realmSurveys != null) {
                count = realmSurveys.getTarget();
            }
            realmSubmitted = realm.where(RealmAnswers.class).equalTo(AppConstants.requester_status, "1").count();
            realmInprogress = realm.where(RealmAnswers.class).equalTo(AppConstants.requester_status, "5").count();
            realmNewRetailer = realm.where(RealmAnswers.class).equalTo(AppConstants.CUSTOMER, AppConstants.NEW).equalTo(AppConstants.requester_status, "1").count();
            realmCystal = realm.where(RealmAnswers.class).equalTo(AppConstants.CUSTOMER, AppConstants.CrystalCustomer).equalTo(AppConstants.requester_status, "1").count();

            int n = count;
            int v = (int) realmSubmitted;
            int percent = v * 100 / n;
            textViewtargetCount.setText(String.valueOf(count));
            textViewCompletedCount.setText(String.valueOf(realmSubmitted));
            textViewAchievementPercentage.setText(String.valueOf(percent).concat("%"));
            textViewInProgressCount.setText(String.valueOf(realmInprogress));
            textViewNewRetailersCount.setText(String.valueOf(realmNewRetailer));
            textViewCrystalMembersCount.setText(String.valueOf(realmCystal));
            textViewCompletedQuestionaire.setText("Submitted Questionnaire".concat("(").concat(String.valueOf(realmSubmitted).concat(")")));

        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
        } finally {
            realm.close();
        }
    }


    private void setUpListeners() {
        imageViewDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.animateView(v);
                if (isStoragePermissionGranted()) {
                    String title = ((TextView) findViewById(R.id.textViewSurveyDetailActivity)).getText().toString();
                    createExcelFileReport(headerList, stringArrayList, title.replace(" ", "_"), title.replace(" ", "_") + ".xls", title, getResources().getString(R.string.sharetitle) + " of " + title + "\n\n" + getResources().getString(R.string.thankyou));
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_WRITE_STORAGE);
                    }
                }
            }
        });
        imageViewBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageViewFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SurveyDetailDialogFragment dFragment = new SurveyDetailDialogFragment();
                dFragment.show(fm, "Dialog Fragment");
            }
        });
        floatingActionMenu.setClosedOnTouchOutside(true);
        floatingCrystal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchCustomersActivity.class);
                intent.putExtra(AppConstants.SURVEYID, surveyId);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                floatingActionMenu.close(true);

            }
        });

        floatingRetailers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, SearchCustomersActivity.class);
                intent.putExtra(AppConstants.SURVEYID, surveyId);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                floatingActionMenu.close(true);

            }
        });
        relativelayoutTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //prepareTargetsList();

            }
        });
        relativeLayoutSubmitted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewDownaload.setVisibility(View.VISIBLE);
                textViewCompletedQuestionaire.setText("Submitted Questionnaire".concat("(").concat(String.valueOf(realmSubmitted).concat(")")));
                if (fromDateTime == null)
                    prepareList(getString(R.string.completed));
                else
                    prepareListFilter(getString(R.string.completed), fromDateTime, toDates);
            }
        });

        realtiveLayoutAchivement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                if (fromDateTime == null)
                //  prepareList(getString(R.string.completed));
//                else
//                    prepareListFilter(getString(R.string.completed), fromDateTime, toDates);

            }
        });
        relativelayoutInprogress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewDownaload.setVisibility(View.VISIBLE);
                textViewCompletedQuestionaire.setText("Inprogress Questionnaire".concat("(").concat(String.valueOf(realmInprogress).concat(")")));
                if (fromDateTime == null)
                    prepareList(getString(R.string.inprogress));
                else
                    prepareListFilter(getString(R.string.inprogress), fromDateTime, toDates);

            }
        });
        realativeNewRetailers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewDownaload.setVisibility(View.GONE);
                if (fromDateTime == null)
                    prepareList(getString(R.string.newretailor));
                else
                    prepareListFilter(getString(R.string.newretailor), fromDateTime, toDates);


            }
        });
        relativeLayoutCrystalCustomers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cardViewDownaload.setVisibility(View.GONE);
                if (fromDateTime == null)
                    prepareList(getString(R.string.crystalmembers));
                else
                    prepareListFilter(getString(R.string.crystalmembers), fromDateTime, toDates);
            }
        });
        recyclerViewSurveyDetail.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewSurveyDetail, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if(stringArrayList.get(position).getStatus().equalsIgnoreCase("1")) {

                    Intent intent = null;

                    //  if (!Utility.validateString(stringArrayList.get(position).getStatus()) || stringArrayList.get(position).getStatus().equalsIgnoreCase("5")) {
                    intent = new Intent(mContext, CategoryExpandableListActivity.class);
                    //  }
                    intent.putExtra(AppConstants.STR_TITLE, titleString);
                    intent.putExtra(AppConstants.SURVEYID, surveyId);

                    intent.putExtra(AppConstants.CUSTOMERID, stringArrayList.get(position).get_id());

                    intent.putExtra(AppConstants.CUSTOMER, stringArrayList.get(position).getCustomer());
                    startActivity(intent);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    private void setUpElements() {
        surveyDetailAdapter = new SurveyDetailAdapter(mContext, stringArrayList);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewSurveyDetail.setLayoutManager(linearLayoutManager);
        recyclerViewSurveyDetail.setItemAnimator(new DefaultItemAnimator());
        recyclerViewSurveyDetail.setAdapter(surveyDetailAdapter);

    }

    private void setUpClickListeners() {
        surveyDetailAdapter.setOnItemClickLister(new SurveyDetailAdapter.OnItemSelecteListener() {
            @RequiresApi(api = Build.VERSION_CODES.ECLAIR)
            @Override
            public void onItemSelected(View v, int position) {
                Intent intent = null;


                intent = new Intent(mContext, CategoryExpandableListActivity.class);


                intent.putExtra(AppConstants.STR_TITLE, titleString);
                intent.putExtra(AppConstants.SURVEYID, surveyId);

                    intent.putExtra(AppConstants.CUSTOMERID, stringArrayList.get(position).get_id());

                intent.putExtra(AppConstants.CUSTOMER, stringArrayList.get(position).getCustomer());
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);

            }
        });
        recyclerViewSurveyDetail.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerViewSurveyDetail, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                Intent intent = null;

                //  if (!Utility.validateString(stringArrayList.get(position).getStatus()) || stringArrayList.get(position).getStatus().equalsIgnoreCase("5")) {
                intent = new Intent(mContext, CategoryExpandableListActivity.class);
                //  }
                intent.putExtra(AppConstants.STR_TITLE, titleString);
                intent.putExtra(AppConstants.SURVEYID, surveyId);

                intent.putExtra(AppConstants.CUSTOMERID, stringArrayList.get(position).get_id());

                intent.putExtra(AppConstants.CUSTOMER, stringArrayList.get(position).getCustomer());
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    String title = ((TextView) findViewById(R.id.textViewSurveyDetailActivity)).getText().toString();
                    createExcelFileReport(headerList, stringArrayList, title.replace(" ", "_"), title.replace(" ", "_") + ".xls", title, getResources().getString(R.string.sharetitle) + " of " + title + "\n\n" + getResources().getString(R.string.thankyou));

                } else {
                    Toast.makeText(SurveyDetailActivity.this, getString(R.string.please_allow_the_permission_to_share_file), Toast.LENGTH_LONG).show();
                }
                return;
            }
        }

    }

    private void initialization() {
        intent = getIntent();
        stringArrayList = new ArrayList<>();

        cardViewDownaload = findViewById(R.id.carView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerViewSurveyDetail = findViewById(R.id.recylerViewSurveyDetail);
        imageViewBack = findViewById(R.id.imageviewback);
        imageViewFilter = findViewById(R.id.imageviewfilter);
        textViewTitle = findViewById(R.id.textViewSurveyDetailActivity);
        imageViewDownload = findViewById(R.id.imageViewDownload);
        textViewToday = findViewById(R.id.textViewToday);
        textViewtarget = findViewById(R.id.textViewtarget);
        textViewAchievement = findViewById(R.id.textViewAchievement);
        textViewInProgress = findViewById(R.id.textViewInProgress);
        textViewNewRetailers = findViewById(R.id.textViewNewRetailers);
        textViewCrystalmembers = findViewById(R.id.textViewCrystalmembers);
        textViewCompletedQuestionaire = findViewById(R.id.textViewCompletedQuestionaire);
        floatingActionMenu = findViewById(R.id.floating_action_menu_customers);
        floatingCrystal = findViewById(R.id.floatingCrystal);
        floatingRetailers = findViewById(R.id.floatingRetailers);
        textViewtargetCount = findViewById(R.id.textViewtargetCount);
        textViewAchievementPercentage = findViewById(R.id.textViewAchievementPercentage);
        textViewCompletedCount = findViewById(R.id.textViewCompletedCount);
        textViewInProgressCount = findViewById(R.id.textViewInProgressCount);
        textViewNewRetailersCount = findViewById(R.id.textViewNewRetailersCount);
        textViewCrystalMembersCount = findViewById(R.id.textViewCrystalMembersCount);
        relativelayoutTarget = findViewById(R.id.relativelayoutTarget);
        relativeLayoutSubmitted = findViewById(R.id.relativeLayoutSubmitted);
        realtiveLayoutAchivement = findViewById(R.id.realtiveLayoutAchivement);
        relativelayoutInprogress = findViewById(R.id.relativelayoutInprogress);
        realativeNewRetailers = findViewById(R.id.realativeNewRetailers);
        relativeLayoutCrystalCustomers = findViewById(R.id.relativeLayoutCrystalCustomers);
        fromDate = Utility.getCurrentDate();
        toDate = Utility.getCurrentDate();
        textViewToday.setText(getResources().getString(R.string.today).concat(":" + "(".concat(fromDate + "-" + toDate).concat(")")));

        textViewToday.setTypeface(FontClass.openSemiBold(mContext));
        textViewtarget.setTypeface(FontClass.openSansRegular(mContext));
        textViewAchievement.setTypeface(FontClass.openSansRegular(mContext));
        textViewInProgress.setTypeface(FontClass.openSansRegular(mContext));
        textViewNewRetailers.setTypeface(FontClass.openSansRegular(mContext));
        textViewCrystalmembers.setTypeface(FontClass.openSansRegular(mContext));
        textViewtargetCount.setTypeface(FontClass.openSansRegular(mContext));
        textViewAchievementPercentage.setTypeface(FontClass.openSansRegular(mContext));
        textViewCompletedCount.setTypeface(FontClass.openSansRegular(mContext));
        textViewInProgressCount.setTypeface(FontClass.openSansRegular(mContext));
        textViewNewRetailersCount.setTypeface(FontClass.openSansRegular(mContext));
        textViewCrystalMembersCount.setTypeface(FontClass.openSansRegular(mContext));
        textViewCompletedQuestionaire.setTypeface(FontClass.openSansRegular(mContext));
        textViewTitle.setTypeface(FontClass.openSansRegular(mContext));
        surveyId = intent.getExtras().getString(AppConstants.SURVEYID);
        titleString = intent.getExtras().getString(AppConstants.TYPE);
        textViewTitle.setText(titleString);
        cardViewDownaload.setVisibility(View.VISIBLE);
//\        textViewFilter.setTypeface(FontClass.openSemiBold(mContext));

    }

    @Override
    protected void onResume() {
        super.onResume();

    }


    private void prepareTargetsList() {
        if (stringArrayList == null) stringArrayList = new ArrayList<>();
        stringArrayList.clear();
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<RealmCustomer> realmCustomers = realm.where(RealmCustomer.class).findAll();
            if (realmCustomers != null && realmCustomers.size() > 0) {
                for (int i = 0; i < realmCustomers.size(); i++) {
                    AnswersModal modal = new AnswersModal();
                    modal.set_id(realmCustomers.get(i).get_id());
                    RealmAnswers realmAnswers1 = realm.where(RealmAnswers.class).equalTo(AppConstants.SURVEYID, surveyId).equalTo(AppConstants.CUSTOMERID, realmCustomers.get(i).get_id()).findFirst();
                    if (realmAnswers1 != null) {
                        if (realmAnswers1.getCreatedAt() != null)
                            modal.setDate(AppConstants.format2.format(realmAnswers1.getCreatedAt()));
                        String status = realmAnswers1.getRequester_status();
                        modal.setStatus(status);
                    } else {
                        modal.setStatus("");
                    }
                    if (realmCustomers.get(i).getCustomer().equalsIgnoreCase(AppConstants.CrystalCustomer)) {
                        modal.setTitle(realmCustomers.get(i).getName());
                        modal.setState(realmCustomers.get(i).getState());
                        modal.setContactNo(realmCustomers.get(i).getContactNo());
                        modal.setTerritory(realmCustomers.get(i).getTerritory());

                    } else {
                        modal.setTitle(realmCustomers.get(i).getRetailerName());
                        modal.setState(realmCustomers.get(i).getState_code() + "");
                        modal.setContactNo(realmCustomers.get(i).getMobile());
                        modal.setTerritory(realmCustomers.get(i).getTerritory_code() + "");

                    }

                    modal.setPincode(realmCustomers.get(i).getPincode());

                    stringArrayList.add(modal);


                }
            }
        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
        } finally {
            realm.close();
        }

        if (surveyDetailAdapter != null) {
            surveyDetailAdapter.notifyDataSetChanged();
        }
    }

    private void prepareList(String type) {
        if (stringArrayList == null) stringArrayList = new ArrayList<>();
        stringArrayList.clear();
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<RealmAnswers> realmAnswers = null;
            realmAnswers = realm.where(RealmAnswers.class).equalTo(AppConstants.requester_status, "1").findAll();
            if (type.equalsIgnoreCase(getString(R.string.inprogress))) {
                realmAnswers = realm.where(RealmAnswers.class).equalTo(AppConstants.requester_status, "5").findAll();
            } else if (type.equalsIgnoreCase(getString(R.string.completed))) {
                realmAnswers = realm.where(RealmAnswers.class).equalTo(AppConstants.requester_status, "1").findAll();
            } else if (type.equalsIgnoreCase(getString(R.string.newretailor))) {
                realmAnswers = realm.where(RealmAnswers.class).equalTo(AppConstants.CUSTOMER, AppConstants.NEW).equalTo(AppConstants.requester_status, "1").findAll();
            } else if (type.equalsIgnoreCase(getString(R.string.crystalmembers))) {
                realmAnswers = realm.where(RealmAnswers.class).equalTo(AppConstants.CUSTOMER, AppConstants.CrystalCustomer).equalTo(AppConstants.requester_status, "1").findAll();
            }

            if (realmAnswers != null && realmAnswers.size() > 0) {
                for (int i = 0; i < realmAnswers.size(); i++) {
                    AnswersModal modal = new AnswersModal();
                    modal.set_id(realmAnswers.get(i).get_id());
                    RealmCustomer realmCustomer = realm.where(RealmCustomer.class).equalTo(AppConstants.ID, realmAnswers.get(i).getCustomerId()).findFirst();
                    if (realmCustomer.getCustomer().equalsIgnoreCase(AppConstants.CrystalCustomer)) {
                        modal.setTitle(realmCustomer.getName());
                        modal.setState(realmCustomer.getState());
                        modal.setContactNo(realmCustomer.getContactNo());
                        modal.setTerritory(realmCustomer.getTerritory());
                        modal.setStatus(realmAnswers.get(i).getRequester_status());
                    } else {
                        modal.setTitle(realmCustomer.getRetailerName());
                        modal.setState(realmCustomer.getAddress());
                        modal.setContactNo(realmCustomer.getMobile());
                        modal.setTerritory(realmCustomer.getTerritory_code() + "");
                        modal.setStatus(realmAnswers.get(i).getRequester_status());
                    }
                    modal.setCreatedAt(realmCustomer.getCreatedAt());
                    modal.setUpdatedAt(realmCustomer.getUpdatedAt());
                    modal.setPincode(realmCustomer.getPincode());
                    modal.setCustomerId(realmCustomer.getId());
                    modal.setCustomer(realmCustomer.getCustomer());
                    modal.setDate(AppConstants.format2.format(realmAnswers.get(i).getCreatedAt()));
                    stringArrayList.add(modal);
                }
            } else {
                Utility.showToast(getString(R.string.no_data));
            }
        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
        } finally {
            realm.close();
        }

        setUpElements();
    }

    private void prepareListFilter(String type, Date fromDate, Date toDate) {
        if (stringArrayList == null) stringArrayList = new ArrayList<>();
        stringArrayList.clear();
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<RealmAnswers> realmAnswers = null;
            realmAnswers = realm.where(RealmAnswers.class).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.requester_status, "1").findAll();
            if (type.equalsIgnoreCase(getString(R.string.inprogress))) {
                realmAnswers = realm.where(RealmAnswers.class).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.requester_status, "5").findAll();
            } else if (type.equalsIgnoreCase(getString(R.string.completed))) {
                realmAnswers = realm.where(RealmAnswers.class).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.requester_status, "1").findAll();
            } else if (type.equalsIgnoreCase(getString(R.string.newretailor))) {
                realmAnswers = realm.where(RealmAnswers.class).equalTo(AppConstants.CUSTOMER, AppConstants.NEW).equalTo(AppConstants.requester_status, "1").findAll();
            } else if (type.equalsIgnoreCase(getString(R.string.crystalmembers))) {
                realmAnswers = realm.where(RealmAnswers.class).equalTo(AppConstants.CUSTOMER, AppConstants.CrystalCustomer).between(AppConstants.CREATEDAT, fromDate, toDate).equalTo(AppConstants.requester_status, "1").findAll();
            }

            if (realmAnswers != null && realmAnswers.size() > 0) {
                for (int i = 0; i < realmAnswers.size(); i++) {
                    AnswersModal modal = new AnswersModal();
                    modal.set_id(realmAnswers.get(i).get_id());
                    RealmCustomer realmCustomer = realm.where(RealmCustomer.class).equalTo(AppConstants.ID, realmAnswers.get(i).getCustomerId()).findFirst();
                    if (realmCustomer.getCustomer().equalsIgnoreCase(AppConstants.CrystalCustomer)) {
                        modal.setTitle(realmCustomer.getName());
                        modal.setState(realmCustomer.getState());
                        modal.setContactNo(realmCustomer.getContactNo());
                        modal.setTerritory(realmCustomer.getTerritory());
                    } else {
                        modal.setTitle(realmCustomer.getRetailerName());
                        modal.setState(realmCustomer.getAddress());
                        modal.setContactNo(realmCustomer.getMobile());
                        modal.setTerritory(realmCustomer.getTerritory_code() + "");
                    }
                    modal.setPincode(realmCustomer.getPincode());
                    modal.setCustomerId(realmCustomer.getId());

                    modal.setCreatedAt(realmCustomer.getCreatedAt());
                    modal.setUpdatedAt(realmCustomer.getUpdatedAt());
                    modal.setStatus(realmAnswers.get(i).getCd_Status());
                    modal.setCustomer(realmCustomer.getCustomer());
                    modal.setDate(AppConstants.format2.format(realmAnswers.get(i).getCreatedAt()));
                    stringArrayList.add(modal);
                }
            } else {
                Utility.showToast(getString(R.string.no_data));
            }
        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
        } finally {
            realm.close();
        }

        if (surveyDetailAdapter != null) {
            surveyDetailAdapter.notifyDataSetChanged();
        }
    }

    private void prepareListCustomersFilter(Date fromDateTime, Date toDates) {
        if (stringArrayList == null) stringArrayList = new ArrayList<>();
        stringArrayList.clear();
        Realm realm = Realm.getDefaultInstance();
        try {
            RealmResults<RealmCustomer> realmCustomers = realm.where(RealmCustomer.class).findAll();//.equalTo(AppConstants.SURVEYID,surveyId).findAll();
            if (realmCustomers != null && realmCustomers.size() > 0) {
                for (int i = 0; i < realmCustomers.size(); i++) {
                    AnswersModal modal = new AnswersModal();
                    // modal.setId(realmCustomers.get(i).get_id());
                    RealmAnswers realmAnswers1 = realm.where(RealmAnswers.class).equalTo(AppConstants.CUSTOMERID, realmCustomers.get(i).get_id()).findFirst();
                    if (realmAnswers1 != null) {
                        String status = realmAnswers1.getCd_Status();
                        modal.setStatus(status);
                        modal.setDate(AppConstants.format2.format(realmAnswers1.getCreatedAt()));
                    } else {
                        modal.setStatus("0");
                        modal.setDate(AppConstants.format2.format(realmCustomers.get(i).getCreatedAt()));
                    }
                    modal.setTitle(realmCustomers.get(i).getName());
                    modal.setState(realmCustomers.get(i).getState());
                    modal.setTerritory(realmCustomers.get(i).getTerritory());
                    modal.setPincode(realmCustomers.get(i).getPincode());
                    modal.setContactNo(realmCustomers.get(i).getContactNo());
                    //modal.setStatus(type);

                    stringArrayList.add(modal);
                }
            }
        } catch (Exception e) {
            realm.close();
            e.printStackTrace();
        } finally {
            realm.close();
        }

        if (surveyDetailAdapter != null) {
            surveyDetailAdapter.notifyDataSetChanged();
        }
        setUpElements();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }


    @Override
    public void submitData(String statusString, String fromDateString, String toDateString, String type) {
        fromDate = fromDateString;
        toDate = toDateString;
        statusData = statusString;
        Date date = null;
        Date toDateTime = null;
        try {
            date = AppConstants.format2.parse(fromDate);
            fromDateTime = AppConstants.format3.parse(AppConstants.format3.format(date));
            date = AppConstants.format2.parse(toDate);
            toDateTime = AppConstants.format3.parse(AppConstants.format3.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        textViewToday.setText(new StringBuilder().append(getResources().getString(R.string.today)).append(":".concat("(".concat(fromDateString.concat("-").concat(toDateString))).concat(")")).toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(toDateTime);
        cal.add(Calendar.DATE, 1);
        toDates = cal.getTime();
        prepareListFilter(statusString, fromDateTime, toDates);
        setCountsFilter(statusString, fromDateTime, toDates);
    }


}
