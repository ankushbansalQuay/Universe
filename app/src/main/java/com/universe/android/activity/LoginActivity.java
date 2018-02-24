package com.universe.android.activity;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.widget.AppCompatButton;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.universe.android.R;
import com.universe.android.activity.admin.FormQuestionActivity;
import com.universe.android.enums.FormEnum;
import com.universe.android.helper.FontClass;
import com.universe.android.helper.GPSTracker;
import com.universe.android.okkhttp.APIClient;
import com.universe.android.okkhttp.UniverseAPI;
import com.universe.android.realmbean.RealmController;
import com.universe.android.resource.Login.login.LoginRequest;
import com.universe.android.resource.Login.login.LoginResponse;
import com.universe.android.resource.Login.login.LoginService;
import com.universe.android.utility.AppConstants;
import com.universe.android.utility.Prefs;
import com.universe.android.utility.Utility;
import com.universe.android.web.BaseApiCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import in.editsoft.api.exception.APIException;
import in.editsoft.api.util.App;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private EditText editTextInputEmail, editTextInputPassword;
    private AppCompatButton textViewLogin;
    private TextView textViewForgotPassword;
    private TextInputLayout textInputLayoutEmailLogin, textInputLayoutPassword;
    private GPSTracker gpsTracker;

    private double latitude;
    private double longitude;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initialization();
        setUpElements();
    }

    private void initialization() {
        textViewLogin = findViewById(R.id.btn_login);
        editTextInputEmail = findViewById(R.id.input_email);
        editTextInputPassword = findViewById(R.id.input_password);
        textViewForgotPassword = findViewById(R.id.link_forgot_password);
        textInputLayoutEmailLogin = findViewById(R.id.textInputLayoutEmailLogin);
        textInputLayoutPassword = findViewById(R.id.textInputLayoutPassword);

        editTextInputEmail.setTypeface(FontClass.openSansLight(mContext));
        editTextInputPassword.setTypeface(FontClass.openSansLight(mContext));
        textViewLogin.setTypeface(FontClass.openSansRegular(mContext));
        textViewForgotPassword.setTypeface(FontClass.openSansRegular(mContext));
        textInputLayoutEmailLogin.setTypeface(FontClass.openSansRegular(mContext));
        textInputLayoutPassword.setTypeface(FontClass.openSansRegular(mContext));

        gpsTracker = new GPSTracker(LoginActivity.this);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
    }


    private void setUpElements() {
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Utility.animateView(v);
               login();
               // getSurveyResponse();

            }
        });
        textViewForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, ForgotPasswordActivity.class));
                finish();
                // overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login() {
        String email = editTextInputEmail.getText().toString();
        String pass = editTextInputPassword.getText().toString();
        if (validateDetails(email, pass)) {
            if (!Utility.isConnected()) {
                Utility.showToast(R.string.msg_disconnected);
            } else {
                submitLoginRequest(email, pass);
            }
        }

    }

    private boolean validateDetails(String email, String pass) {
        // Utility.hideSoftKeyboard(mA);
        if (!Utility.validateString(email)) {
            Utility.showToast(R.string.msg_enter_email);
            return false;
        } else if (!Utility.validateEmail(email)) {
            Utility.showToast(R.string.msg_email_error);
            return false;
        } else if (!Utility.validateString(pass)) {
            Utility.showToast(R.string.msg_enter_pass1);
            return false;
        } else if (pass.length() < 5) {
            Utility.showToast(R.string.msg_pass_error);
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }


    public void submitLoginRequest(String email, String password) {
        showProgress(R.string.msg_load_default);
        JSONObject jsonSubmitReq=new JSONObject();
        try {
            jsonSubmitReq.put(AppConstants.EMAIL,"parth@e2eprojects.com");
            jsonSubmitReq.put(AppConstants.PASSWORD,"pass123456");
            jsonSubmitReq.put(AppConstants.LAT,"27");
            jsonSubmitReq.put(AppConstants.LNG,"22");
        } catch (JSONException e) {
            e.printStackTrace();
        }


        OkHttpClient okHttpClient = APIClient.getHttpClient();
        RequestBody requestBody = RequestBody.create(UniverseAPI.JSON, jsonSubmitReq.toString());
        String url = UniverseAPI.WEB_SERVICE_LOGIN_METHOD;



        /* else if (formId.equalsIgnoreCase(FormEnum.category.toString())) {
            url = UniverseAPI.WEB_SERVICE_CREATE_CATEGORY_METHOD;
        } else if (formId.equalsIgnoreCase(FormEnum.customer.toString())) {
            url = UniverseAPI.WEB_SERVICE_CREATE_CUSTOMER_METHOD;
        } else if (formId.equalsIgnoreCase(FormEnum.client.toString())) {
            url = UniverseAPI.WEB_SERVICE_CREATE_ClIENT_METHOD;
        }*/


        Request request = APIClient.getPostRequest(this, url, requestBody);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONObject jsonObject=jsonResponse.getJSONObject(AppConstants.RESPONSE);
                            Prefs.putStringPrefs(AppConstants.TYPE, jsonObject.optString(AppConstants.TYPE));

                                JSONObject jsonObject1 = jsonObject.getJSONObject(AppConstants.DETAIL);
                                new RealmController().saveUserDetail(jsonObject1.toString());
                                JSONArray mapping=jsonObject.getJSONArray("mapping");
                                Prefs.putStringPrefs(AppConstants.MAPPING,mapping.toString());




                          //  JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                        /*    Prefs.putStringPrefs(AppConstants.TOKEN_ID, responseBean.getAccessToken());
                            Prefs.putStringPrefs(AppConstants.UserId, responseBean.get_id());
                            Prefs.putStringPrefs(AppConstants.password, responseBean.getPassword());
                            Prefs.putStringPrefs(AppConstants.email, responseBean.getEmail());
                            Prefs.putStringPrefs(AppConstants.name, responseBean.getName());
                            Prefs.putLongPrefs(AppConstants.phone, responseBean.getPhone());
                            Prefs.putStringPrefs(AppConstants.designationLevel, responseBean.getDesignationLevel());
                            Prefs.putStringPrefs(AppConstants.DESIGNATION, responseBean.getDesignation());
                            Prefs.putStringPrefs(AppConstants.picture, responseBean.getPicture());
                            Prefs.putStringPrefs(AppConstants.location, responseBean.getLocation());
                            Prefs.putBooleanPrefs(AppConstants.Login_Status, true);*/

                            getSurveyResponse();

                           // new RealmController().saveQuestions(array.toString());
                        }

                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }
/*
    //hit web service here
    public void submitLoginRequest(String email, String password) {
       showProgress(R.string.msg_load_default);


        //  showProgress();
        // showProgress(R.string.msg_load_default);
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);
        loginRequest.setLat("27" );
        loginRequest.setLng("22" );
        LoginService loginService = new LoginService();
        loginService.executeService(loginRequest, new BaseApiCallback<LoginResponse>() {
            @Override
            public void onComplete() {

            }

            @Override
            public void onSuccess(@NonNull LoginResponse response) {
                super.onSuccess(response);
                LoginResponse.ResponseBean responseBean = response.getResponse();
                Prefs.putStringPrefs(AppConstants.TOKEN_ID, responseBean.getAccessToken());
                Prefs.putStringPrefs(AppConstants.UserId, responseBean.get_id());
                Prefs.putStringPrefs(AppConstants.password, responseBean.getPassword());
                Prefs.putStringPrefs(AppConstants.email, responseBean.getEmail());
                Prefs.putStringPrefs(AppConstants.name, responseBean.getName());
                Prefs.putLongPrefs(AppConstants.phone, responseBean.getPhone());
                Prefs.putStringPrefs(AppConstants.designationLevel, responseBean.getDesignationLevel());
                Prefs.putStringPrefs(AppConstants.DESIGNATION, responseBean.getDesignation());
                Prefs.putStringPrefs(AppConstants.picture, responseBean.getPicture());
                Prefs.putStringPrefs(AppConstants.location, responseBean.getLocation());
                Prefs.putStringPrefs(AppConstants.LATTITUDE, responseBean.getLat());
                Prefs.putStringPrefs(AppConstants.LONGITUDE, responseBean.getLng());
                Prefs.putBooleanPrefs(AppConstants.Login_Status, true);

                getSurveyResponse();


            }

            @Override
            public void onFailure(APIException e) {
                super.onFailure(e);
                Utility.showToast(e.getData());
            }
        });

    }
*/


    private void getQuestionsResponse() {
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = UniverseAPI.WEB_SERVICE_ALLFORM_METHOD;
        Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                            new RealmController().saveQuestions(array.toString());
                        }
                        getSurveyQuestionsResponse();
                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }


    private void getSurveyResponse() {
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = UniverseAPI.WEB_SERVICE_LIST_ADMIN_SURVEY_METHOD;

        Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                            new RealmController().saveSurveysResponse(array.toString());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getClientResponse();
                            }
                        });


                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }


    private void getClientResponse() {


        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = UniverseAPI.WEB_SERVICE_LIST_CLIENT_METHOD;


        Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                            new RealmController().saveClientsResponse(array.toString());
                        }


                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCustomerResponse();
                            }
                        });


                    } else {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }


    private void getCustomerResponse() {


        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = UniverseAPI.WEB_SERVICE_LIST_CUSTOMER_METHOD;


        Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                            new RealmController().saveCustomersResponse(array.toString());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getCategoryResponse();
                            }
                        });


                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }


    private void getCategoryResponse() {


        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = UniverseAPI.WEB_SERVICE_LIST_CATEGORY_METHOD;


        Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                            new RealmController().saveCategoryResponse(array.toString());
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                getQuestionsResponse();
                            }
                        });


                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }

    private void getSurveyQuestionsResponse() {
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = UniverseAPI.WEB_SERVICE_LIST_QUESTION_METHOD;
        Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                            new RealmController().saveSurveyQuestions(array.toString());
                        }

                        getAnswersResponse();
                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }


    private void getAnswersResponse() {
        OkHttpClient okHttpClient = APIClient.getHttpClient();
        String url = UniverseAPI.WEB_SERVICE_LIST_ANSWERS_METHOD;
        Request request = APIClient.getRequest(mContext, url);
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, final IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Utility.showToast(e.getMessage());
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {

                    if (response != null && response.isSuccessful()) {
                        String responseData = response.body().string();
                        if (responseData != null) {
                            JSONObject jsonResponse = new JSONObject(responseData);
                            JSONArray array = jsonResponse.getJSONArray(AppConstants.RESPONSE);
                            new RealmController().saveAnswers(array.toString());
                        }
                        dismissProgress();
                        goToMain();
                    } else {
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                }

            }
        });

    }



    private void goToMain() {
        Prefs.putBooleanPrefs(AppConstants.Login_Status, true);
        Intent intent = new Intent(mContext, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        finish();
    }
}

