package com.cvpc.schedule;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by Dgray on 02.04.2014.
 */
public class AuthorizationActivity extends Activity {

    //<JSON>
    //public static final String URL_TEMP = "http://serv.polytech.cv.ua/standalone/auth.php?req_code=111111&dev_id=11111111111";
   //Clean up shared preferences function!!!
    public static final String URL = "http://serv.polytech.cv.ua/standalone/auth.php?req_code=";
    private static RequestQueue myQueue;
    //</JSON>

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        final String PASSWORD = "password";
        final String SPF_NAME = "vidslogin";
        Button LoginButton;
        final EditText Password;
        final CheckBox chkRememberMe;

        //<JSON>
        myQueue = Volley.newRequestQueue(getApplicationContext());
        //</JSON>



        LoginButton = (Button) findViewById(R.id.checkButton);
        Password = (EditText) findViewById(R.id.password);
        chkRememberMe = (CheckBox) findViewById(R.id.rememberCheckBox);

        SharedPreferences loginPreferences = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
        Password.setText(loginPreferences.getString(PASSWORD, ""));



        View.OnClickListener oclLogin = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo == null) {
                    //Toast.makeText(getApplicationContext(), "No internet connection", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "З'єднання з мережею інтернет - відсутнє", Toast.LENGTH_LONG).show();
                }
                else if (Password.getText().toString().trim().equals("")){
                    //Toast.makeText(getApplicationContext(), "Password is empty", Toast.LENGTH_LONG).show();
                    Toast.makeText(getApplicationContext(), "Поле для паролю - пусте", Toast.LENGTH_LONG).show();
                }
                else {
                    //<JSON>
                    String RequestCode = Password.getText().toString().trim();
                    TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String DeviceID = telephonyManager.getDeviceId();

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, URL + RequestCode + "&dev_id=" + DeviceID, null, new Response.Listener<JSONObject>() {

                        @Override

                        public void onResponse(JSONObject json) {

                            try {

                                if (json.getJSONObject("response") != null){
                                    JSONObject JsonData = json.getJSONObject("response");
                                    String AccessToken = JsonData.getString("access_token");

                                    String strPassword = Password.getText().toString().trim();

                                    if (chkRememberMe.isChecked()) {
                                        SharedPreferences loginPreferences = getSharedPreferences(SPF_NAME, Context.MODE_PRIVATE);
                                        loginPreferences.edit().putString(PASSWORD, strPassword).commit();
                                    }

                                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                    intent.putExtra("AccessToken", AccessToken);
                                    startActivity(intent);
                                    finish();
                                }





                            } catch (JSONException e) {
                                e.printStackTrace();
                                //Toast.makeText(getApplicationContext(), "Enter correct password" , Toast.LENGTH_LONG).show();
                                Toast.makeText(getApplicationContext(), "Неправильний пароль" , Toast.LENGTH_LONG).show();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError VolleyError) {
                            Toast.makeText(getApplicationContext(), "Сталась помилка на сервері" , Toast.LENGTH_LONG).show();
                        }
                    });
                    //<JSON>
                    myQueue.add(jsonObjectRequest);
                    //</JSON>
                }
                // </JSON>

                }

            };

        LoginButton.setOnClickListener(oclLogin);

        }


    }
