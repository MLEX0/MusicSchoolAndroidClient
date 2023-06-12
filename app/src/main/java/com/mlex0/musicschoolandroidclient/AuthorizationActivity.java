package com.mlex0.musicschoolandroidclient;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mlex0.musicschoolandroidclient.Classes.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AuthorizationActivity extends AppCompatActivity {

    EditText AuthEmail;
    EditText AuthPassword;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        AuthEmail = findViewById(R.id.email);
        AuthPassword = findViewById(R.id.password);

    }

    public void OnClickAuthorization(View view) {
        String Login = AuthEmail.getText().toString();
        String Password = AuthPassword.getText().toString();

        if (Login.isEmpty()) {
            Toast.makeText(AuthorizationActivity.this,
                    "Поле 'Email' не может быть пустым!", Toast.LENGTH_SHORT).show();
        } else if (Password.isEmpty()) {
            Toast.makeText(AuthorizationActivity.this,
                    "Поле 'Password' не может быть пустым!", Toast.LENGTH_SHORT).show();
        } else {
            Authorization(Login, Password, this);
        }
    }


    public static void Authorization(String email, String password, Context context) {

        String url = Constants.ApiUrl + "auth/login/";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respObj = new JSONObject(response);
                    if (respObj.has("message")) {
                        Toast.makeText(context,
                                respObj.get("message").toString(), Toast.LENGTH_SHORT).show();
                    } else if (respObj.has("token") && respObj.has("IdUser")) {

                        String IdUser = respObj.get("IdUser").toString();
                        String accessToken = respObj.get("token").toString();

                        //Constants.CurrentUser = ApiMethods.GetUser(IdUser, context, accessToken);
                        //Toast.makeText(context,
                        //Constants.CurrentUser.getStringUser() + "\n" + Constants.UserToken, Toast.LENGTH_LONG).show();
                        Constants.UserToken = respObj.get("token").toString();
                        Constants.UserId = respObj.get("IdUser").toString();
                        Constants.UserImage = respObj.get("UserImage").toString();
                        Constants.IDRole = respObj.get("IDRole").toString();
                        Constants.UserLogin = email;

                        Intent login = new Intent(context, MainFrameActivity.class);
                        context.startActivity(login);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // method to handle errors.
                Toast.makeText(context, "Отсутствует подключение к серверу", Toast.LENGTH_SHORT).show();
                Log.i("volleyError", error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();

                params.put("Email", email);
                params.put("Password", password);

                return params;
            }
        };
        queue.add(request);
    }
}
