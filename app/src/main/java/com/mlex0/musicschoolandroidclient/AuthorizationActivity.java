package com.mlex0.musicschoolandroidclient;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.mlex0.musicschoolandroidclient.Classes.Constants;
import com.mlex0.musicschoolandroidclient.Model.Student;
import com.mlex0.musicschoolandroidclient.Model.Teacher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class AuthorizationActivity extends AppCompatActivity {

    EditText AuthEmail;
    EditText AuthPassword;
    CheckBox RememberMe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authorization);

        AuthEmail = findViewById(R.id.email);
        AuthPassword = findViewById(R.id.password);
        RememberMe = findViewById(R.id.checkBoxRememberMe);

        // Получаем SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("Auth", Context.MODE_PRIVATE);

        // Получаем логин и пароль
        String email = sharedPreferences.getString("email", "");
        String password = sharedPreferences.getString("password", "");

        // Получаем состояние галочки "запомнить меня"
        boolean rememberMe = sharedPreferences.getBoolean("remember_me", false);

        if (rememberMe) {
            AuthEmail.setText(email);
            AuthPassword.setText(password);
            RememberMe.setChecked(true);
        }
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
            if(RememberMe.isChecked()){
                // Получаем SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("Auth", Context.MODE_PRIVATE);

                // Получаем объект Editor для редактирования SharedPreferences
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Сохраняем логин и пароль
                editor.putString("email", Login);
                editor.putString("password", Password);

                // Сохраняем состояние галочки "запомнить меня"
                editor.putBoolean("remember_me", true);

                // Применяем изменения
                editor.apply();
            } else {
                SharedPreferences sharedPreferences = getSharedPreferences("Auth", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("remember_me", false);
                editor.remove("email");
                editor.remove("password");
                editor.apply();
            }
            Authorization(Login, Password, this);
        }
    }


    public void Authorization(String email, String password, Context context) {

        String url1 = Constants.ApiUrl + "auth/login/";

        RequestQueue queue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(Request.Method.POST, url1,
                new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject respObj = new JSONObject(response);
                    if (respObj.has("message")) {
                        Toast.makeText(context,
                                respObj.get("message").toString(), Toast.LENGTH_SHORT).show();
                    } else if (respObj.has("token") && respObj.has("IdUser")) {

                        Constants.UserToken = respObj.get("token").toString();
                        Constants.UserId = respObj.get("IdUser").toString();
                        Constants.UserImage = respObj.get("UserImage").toString();
                        Constants.IDRole = respObj.get("IDRole").toString();
                        Constants.UserLogin = email;

                        if(Constants.IDRole.equals("3")) {
                            String url2 = Constants.ApiUrl + "user/student/" + Constants.UserId;

                            RequestQueue queue = Volley.newRequestQueue(context);

                            StringRequest request = new StringRequest(Request.Method.GET, url2, response1 -> {
                                try {
                                    JSONObject OneResponse = new JSONObject(response1);
                                    Student student = new Student();
                                    student.setID(OneResponse.get("ID").toString());
                                    student.setFirstName(OneResponse.get("FirstName").toString());
                                    student.setLastName(OneResponse.get("LastName").toString());
                                    student.setPatronymic(OneResponse.get("Patronymic").toString());
                                    student.setBirthday(OneResponse.get("Birthday").toString());
                                    student.setIdStudyGroup(OneResponse.get("IDStudyGroup").toString());
                                    student.setIdUser(OneResponse.get("IDUser").toString());
                                    student.setPhone(OneResponse.get("Phone").toString());
                                    student.setGender(OneResponse.get("IDGender").toString());

                                    Constants.UserStudent = student;
                                } catch (JSONException e) {

                                    Log.i("exception", e.getMessage());
                                }
                            }, error -> {
                                // method to handle errors.
                                Toast.makeText(context, "Отсутствует подключение к серверу", Toast.LENGTH_SHORT).show();
                            }) {


                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>(super.getHeaders());
                                    //if(params==null)params = new HashMap<>();
                                    params.put("Authorization","Bearer " + Constants.UserToken);
                                    return params;
                                }


                            };
                            queue.add(request);
                        }

                        if(Constants.IDRole.equals("2")){
                            String url3 = Constants.ApiUrl + "user/teacher/" + Constants.UserId;

                            RequestQueue queue = Volley.newRequestQueue(context);

                            StringRequest request = new StringRequest(Request.Method.GET, url3, response2 -> {
                                try {
                                    JSONObject OneResponse = new JSONObject(response2);
                                    Teacher teacher = new Teacher();
                                    teacher.setID(OneResponse.get("ID").toString());
                                    teacher.setFirstName(OneResponse.get("FirstName").toString());
                                    teacher.setLastName(OneResponse.get("LastName").toString());
                                    teacher.setPatronymic(OneResponse.get("Patronymic").toString());
                                    teacher.setBirthday(OneResponse.get("Birthday").toString());
                                    teacher.setUserId(OneResponse.get("IDUser").toString());
                                    teacher.setPhone(OneResponse.get("Phone").toString());
                                    teacher.setGender(OneResponse.get("IDGender").toString());

                                    Constants.UserTeacher = teacher;
                                } catch (JSONException e) {

                                    Log.i("voll", e.getMessage());
                                }
                            }, error -> {
                                // method to handle errors.
                                Toast.makeText(context, "Отсутствует подключение к серверу", Toast.LENGTH_SHORT).show();
                            }) {


                                @Override
                                public Map<String, String> getHeaders() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>(super.getHeaders());
                                    //if(params==null)params = new HashMap<>();
                                    params.put("Authorization","Bearer " + Constants.UserToken);
                                    return params;
                                }
                            };
                            queue.add(request);
                        }

                        Intent login = new Intent(context, MainFrameActivity.class);
                        context.startActivity(login);
                        finish();
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
