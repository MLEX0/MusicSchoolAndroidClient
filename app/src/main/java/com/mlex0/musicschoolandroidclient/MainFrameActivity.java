package com.mlex0.musicschoolandroidclient;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.mlex0.musicschoolandroidclient.Adapter.MarkAdapter;
import com.mlex0.musicschoolandroidclient.Classes.Constants;

import com.mlex0.musicschoolandroidclient.Fragments.ChatsFragment;
import com.mlex0.musicschoolandroidclient.Fragments.UsersFragment;
import com.mlex0.musicschoolandroidclient.Model.Mark;
import com.mlex0.musicschoolandroidclient.Model.StudentSchedule;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainFrameActivity extends AppCompatActivity {


    //MainFrameActivity
    private BottomNavigationView BottomNav;

    ConstraintLayout scheduleCL;
    ConstraintLayout marksCL;
    ConstraintLayout chatsCL;
    ConstraintLayout profileCL;

    //ListView Mark
    ArrayList<Mark> MarkList;
    MarkAdapter MarkListAdapter;
    ListView MarkView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frame);

        Init();

        ChangeByRole();

        SetBottomNavigationSelectedListener();

        updateSchedule();
    }

    private void Init(){
        BottomNav = findViewById(R.id.bottom_navigation);

        //Constraint Layouts
        scheduleCL = findViewById(R.id.scheduleCL);
        marksCL = findViewById(R.id.marksCL);
        chatsCL = findViewById(R.id.chatsCL);
        profileCL = findViewById(R.id.profileCL);

        //Chats
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());

        viewPagerAdapter.addFragment(new ChatsFragment(),"Чаты");
        viewPagerAdapter.addFragment(new UsersFragment(),"Пользователи");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

        MarkList = new ArrayList<>();
        MarkListAdapter = new MarkAdapter(this, MarkList);
        MarkView = findViewById(R.id.lvMark);
        MarkView.setAdapter(MarkListAdapter);


    }

    private void SetBottomNavigationSelectedListener() {

        BottomNav.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                //Постраничная навигация при нажатии на кнопку
                if(item.getItemId() == R.id.nav_schedule){
                    showScheduleLayout();
                    updateSchedule();
                }
                if(item.getItemId() == R.id.nav_marks){
                    showMarksLayout();
                    updateMarks();
                }
                if(item.getItemId() == R.id.nav_chats){
                    showChatsLayout();
                    updateChats();
                }
                if(item.getItemId() == R.id.nav_profile){
                    showProfileLayout();
                    updateProfile();
                }
                return true;
            }
        });
    }



    private void ChangeByRole(){

    }


    //ScheduleStart

    private void showScheduleLayout(){
        scheduleCL.setVisibility(View.VISIBLE);
        marksCL.setVisibility(View.GONE);
        chatsCL.setVisibility(View.GONE);
        profileCL.setVisibility(View.GONE);
    }
    private void updateSchedule(){

        String url = Constants.ApiUrl + "schedule/group/ХОРЕО-1";

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                List<StudentSchedule> scheduleItems = new ArrayList<>();
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject OneResponse = new JSONObject(jsonArray.get(i).toString());
                    StudentSchedule scheduleItem = new StudentSchedule();
                    scheduleItem.setIDLesson(OneResponse.get("IDLesson").toString());
                    scheduleItem.setGroupNumber(OneResponse.get("GroupNumber").toString());
                    scheduleItem.setWeekDayNumber(OneResponse.get("WeekDayNumber").toString());
                    scheduleItem.setLessonDate((OneResponse.get("LessonDate").toString()));
                    scheduleItem.setDayOfWeek(OneResponse.get("DayOfWeek").toString());
                    scheduleItem.setStartTime(OneResponse.get("StartTime").toString());
                    scheduleItem.setSubjectName(OneResponse.get("SubjectName").toString());
                    scheduleItem.setClassroomNumber(OneResponse.get("ClassroomNumber").toString());
                    scheduleItem.setFloor(OneResponse.get("Floor").toString());
                    scheduleItem.setTeacher(OneResponse.get("Teacher").toString());
                    scheduleItems.add(scheduleItem);
                }
                DrawSchedule(scheduleItems);

            } catch (JSONException e) {

                Log.i("exception", e.getMessage());
            }
        }, error -> {
            // method to handle errors.
            Toast.makeText(this, "Отсутствует подключение к серверу", Toast.LENGTH_SHORT).show();
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

    private void DrawSchedule(List<StudentSchedule> scheduleItems){


        for (StudentSchedule scheduleItem : scheduleItems) {
            //Понедельник
            if (scheduleItem.getWeekDayNumber().equals("1") && scheduleItem.getDayOfWeek().equals("Понедельник")){
                LinearLayout llMonday = findViewById(R.id.ll_monday_items);
                llMonday.removeAllViews();

                TextView tvLessonNumber = new TextView(this);
                TextView tvLessonName = new TextView(this);
                TextView tvLessonTime = new TextView(this);
                TextView tvClassRoom = new TextView(this);
                TextView tvTeacher = new TextView(this);
                TextView tvBreakTime = new TextView(this);

                LinearLayout llLessonTime = new LinearLayout(this);
                llLessonTime.setOrientation(LinearLayout.HORIZONTAL);

                tvLessonNumber.setText("Урок ");
                tvLessonNumber.setTextSize(20);
                tvLessonNumber.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonNumber.setPadding(30, 10, 0, 4);

                tvLessonTime.setText(scheduleItem.getStartTime());
                tvLessonTime.setTextSize(18);
                tvLessonTime.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonTime.setPadding(20, 4, 0, 4);

                llLessonTime.addView(tvLessonNumber);
                llLessonTime.addView(tvLessonTime);

                tvLessonName.setText(scheduleItem.getSubjectName());
                tvLessonName.setTextSize(30);
                tvLessonName.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonName.setPadding(30, 4, 0, 4);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                layoutParams.gravity = Gravity.END | Gravity.TOP;
                tvClassRoom.setLayoutParams(layoutParams);

                tvClassRoom.setText("Каб." + scheduleItem.getClassroomNumber());
                tvClassRoom.setTextSize(22);
                tvClassRoom.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvClassRoom.setPadding(30, 4, 0, 4);

                tvTeacher.setText("Преподаватель: " + scheduleItem.getTeacher());
                tvTeacher.setTextSize(20);
                tvTeacher.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvTeacher.setPadding(30, 4, 0, 4);

                View line1 = new View(this);
                line1.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line1.setLayoutParams(layoutParams1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line1.setPadding(0, 5,0,5);
                }

                View line2 = new View(this);
                line2.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line2.setLayoutParams(layoutParams2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line2.setPadding(0, 5,0,5);
                }

                tvBreakTime.setText("Перемена: 0 минут");
                tvBreakTime.setTextSize(20);
                tvBreakTime.setTextColor(Color.parseColor("#cccccc"));
                tvBreakTime.setPadding(30, 5, 0, 5);

                GradientDrawable breakShape = new GradientDrawable();
                breakShape.setShape(GradientDrawable.RECTANGLE);
                breakShape.setColor(Color.parseColor("#7899D4"));
                breakShape.setStroke(3, Color.WHITE);
                tvBreakTime.setBackground(breakShape);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(20);
                shape.setColor(Color.parseColor("#13293D"));
                shape.setStroke(3, Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    shape.setPadding(0, 5,0,5);
                }


                llMonday.setBackground(shape);
                llMonday.addView(llLessonTime);
                llMonday.addView(tvLessonName);
                llMonday.addView(tvClassRoom);
                llMonday.addView(tvTeacher);

                llMonday.addView(line1);

                /*if(i != numberOfLessons-1) {
                    llMonday.addView(tvBreakTime);
                    llMonday.addView(line2);
                }*/
            }
            //Вторник
            if (scheduleItem.getWeekDayNumber().equals("2") && scheduleItem.getDayOfWeek().equals("Вторник")){
                LinearLayout lltuesday = findViewById(R.id.ll_tuesday_items);
                lltuesday.removeAllViews();

                TextView tvLessonNumber = new TextView(this);
                TextView tvLessonName = new TextView(this);
                TextView tvLessonTime = new TextView(this);
                TextView tvClassRoom = new TextView(this);
                TextView tvTeacher = new TextView(this);
                TextView tvBreakTime = new TextView(this);

                LinearLayout llLessonTime = new LinearLayout(this);
                llLessonTime.setOrientation(LinearLayout.HORIZONTAL);

                tvLessonNumber.setText("Урок ");
                tvLessonNumber.setTextSize(20);
                tvLessonNumber.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonNumber.setPadding(30, 10, 0, 4);

                tvLessonTime.setText(scheduleItem.getStartTime());
                tvLessonTime.setTextSize(18);
                tvLessonTime.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonTime.setPadding(20, 4, 0, 4);

                llLessonTime.addView(tvLessonNumber);
                llLessonTime.addView(tvLessonTime);

                tvLessonName.setText(scheduleItem.getSubjectName());
                tvLessonName.setTextSize(30);
                tvLessonName.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonName.setPadding(30, 4, 0, 4);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                layoutParams.gravity = Gravity.END | Gravity.TOP;
                tvClassRoom.setLayoutParams(layoutParams);

                tvClassRoom.setText("Каб." + scheduleItem.getClassroomNumber());
                tvClassRoom.setTextSize(22);
                tvClassRoom.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvClassRoom.setPadding(30, 4, 0, 4);

                tvTeacher.setText("Преподаватель: " + scheduleItem.getTeacher());
                tvTeacher.setTextSize(20);
                tvTeacher.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvTeacher.setPadding(30, 4, 0, 4);

                View line1 = new View(this);
                line1.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line1.setLayoutParams(layoutParams1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line1.setPadding(0, 5,0,5);
                }

                View line2 = new View(this);
                line2.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line2.setLayoutParams(layoutParams2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line2.setPadding(0, 5,0,5);
                }

                tvBreakTime.setText("Перемена: 0 минут");
                tvBreakTime.setTextSize(20);
                tvBreakTime.setTextColor(Color.parseColor("#cccccc"));
                tvBreakTime.setPadding(30, 5, 0, 5);

                GradientDrawable breakShape = new GradientDrawable();
                breakShape.setShape(GradientDrawable.RECTANGLE);
                breakShape.setColor(Color.parseColor("#7899D4"));
                breakShape.setStroke(3, Color.WHITE);
                tvBreakTime.setBackground(breakShape);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(20);
                shape.setColor(Color.parseColor("#13293D"));
                shape.setStroke(3, Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    shape.setPadding(0, 5,0,5);
                }


                lltuesday.setBackground(shape);
                lltuesday.addView(llLessonTime);
                lltuesday.addView(tvLessonName);
                lltuesday.addView(tvClassRoom);
                lltuesday.addView(tvTeacher);

                lltuesday.addView(line1);

                /*if(i != numberOfLessons-1) {
                    llMonday.addView(tvBreakTime);
                    llMonday.addView(line2);
                }*/
            }
            //Среда
            if (scheduleItem.getWeekDayNumber().equals("3") && scheduleItem.getDayOfWeek().equals("Среда")){
                LinearLayout llMonday = findViewById(R.id.ll_wednesday_items);
                llMonday.removeAllViews();

                TextView tvLessonNumber = new TextView(this);
                TextView tvLessonName = new TextView(this);
                TextView tvLessonTime = new TextView(this);
                TextView tvClassRoom = new TextView(this);
                TextView tvTeacher = new TextView(this);
                TextView tvBreakTime = new TextView(this);

                LinearLayout llLessonTime = new LinearLayout(this);
                llLessonTime.setOrientation(LinearLayout.HORIZONTAL);

                tvLessonNumber.setText("Урок ");
                tvLessonNumber.setTextSize(20);
                tvLessonNumber.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonNumber.setPadding(30, 10, 0, 4);

                tvLessonTime.setText(scheduleItem.getStartTime());
                tvLessonTime.setTextSize(18);
                tvLessonTime.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonTime.setPadding(20, 4, 0, 4);

                llLessonTime.addView(tvLessonNumber);
                llLessonTime.addView(tvLessonTime);

                tvLessonName.setText(scheduleItem.getSubjectName());
                tvLessonName.setTextSize(30);
                tvLessonName.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonName.setPadding(30, 4, 0, 4);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                layoutParams.gravity = Gravity.END | Gravity.TOP;
                tvClassRoom.setLayoutParams(layoutParams);

                tvClassRoom.setText("Каб." + scheduleItem.getClassroomNumber());
                tvClassRoom.setTextSize(22);
                tvClassRoom.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvClassRoom.setPadding(30, 4, 0, 4);

                tvTeacher.setText("Преподаватель: " + scheduleItem.getTeacher());
                tvTeacher.setTextSize(20);
                tvTeacher.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvTeacher.setPadding(30, 4, 0, 4);

                View line1 = new View(this);
                line1.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line1.setLayoutParams(layoutParams1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line1.setPadding(0, 5,0,5);
                }

                View line2 = new View(this);
                line2.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line2.setLayoutParams(layoutParams2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line2.setPadding(0, 5,0,5);
                }

                tvBreakTime.setText("Перемена: 0 минут");
                tvBreakTime.setTextSize(20);
                tvBreakTime.setTextColor(Color.parseColor("#cccccc"));
                tvBreakTime.setPadding(30, 5, 0, 5);

                GradientDrawable breakShape = new GradientDrawable();
                breakShape.setShape(GradientDrawable.RECTANGLE);
                breakShape.setColor(Color.parseColor("#7899D4"));
                breakShape.setStroke(3, Color.WHITE);
                tvBreakTime.setBackground(breakShape);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(20);
                shape.setColor(Color.parseColor("#13293D"));
                shape.setStroke(3, Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    shape.setPadding(0, 5,0,5);
                }


                llMonday.setBackground(shape);
                llMonday.addView(llLessonTime);
                llMonday.addView(tvLessonName);
                llMonday.addView(tvClassRoom);
                llMonday.addView(tvTeacher);

                llMonday.addView(line1);

                /*if(i != numberOfLessons-1) {
                    llMonday.addView(tvBreakTime);
                    llMonday.addView(line2);
                }*/
            }
            //Четверг
            if (scheduleItem.getWeekDayNumber().equals("4") && scheduleItem.getDayOfWeek().equals("Четверг")){
                LinearLayout llMonday = findViewById(R.id.ll_thursday_items);
                llMonday.removeAllViews();

                TextView tvLessonNumber = new TextView(this);
                TextView tvLessonName = new TextView(this);
                TextView tvLessonTime = new TextView(this);
                TextView tvClassRoom = new TextView(this);
                TextView tvTeacher = new TextView(this);
                TextView tvBreakTime = new TextView(this);

                LinearLayout llLessonTime = new LinearLayout(this);
                llLessonTime.setOrientation(LinearLayout.HORIZONTAL);

                tvLessonNumber.setText("Урок ");
                tvLessonNumber.setTextSize(20);
                tvLessonNumber.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonNumber.setPadding(30, 10, 0, 4);

                tvLessonTime.setText(scheduleItem.getStartTime());
                tvLessonTime.setTextSize(18);
                tvLessonTime.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonTime.setPadding(20, 4, 0, 4);

                llLessonTime.addView(tvLessonNumber);
                llLessonTime.addView(tvLessonTime);

                tvLessonName.setText(scheduleItem.getSubjectName());
                tvLessonName.setTextSize(30);
                tvLessonName.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonName.setPadding(30, 4, 0, 4);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                layoutParams.gravity = Gravity.END | Gravity.TOP;
                tvClassRoom.setLayoutParams(layoutParams);

                tvClassRoom.setText("Каб." + scheduleItem.getClassroomNumber());
                tvClassRoom.setTextSize(22);
                tvClassRoom.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvClassRoom.setPadding(30, 4, 0, 4);

                tvTeacher.setText("Преподаватель: " + scheduleItem.getTeacher());
                tvTeacher.setTextSize(20);
                tvTeacher.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvTeacher.setPadding(30, 4, 0, 4);

                View line1 = new View(this);
                line1.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line1.setLayoutParams(layoutParams1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line1.setPadding(0, 5,0,5);
                }

                View line2 = new View(this);
                line2.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line2.setLayoutParams(layoutParams2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line2.setPadding(0, 5,0,5);
                }

                tvBreakTime.setText("Перемена: 0 минут");
                tvBreakTime.setTextSize(20);
                tvBreakTime.setTextColor(Color.parseColor("#cccccc"));
                tvBreakTime.setPadding(30, 5, 0, 5);

                GradientDrawable breakShape = new GradientDrawable();
                breakShape.setShape(GradientDrawable.RECTANGLE);
                breakShape.setColor(Color.parseColor("#7899D4"));
                breakShape.setStroke(3, Color.WHITE);
                tvBreakTime.setBackground(breakShape);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(20);
                shape.setColor(Color.parseColor("#13293D"));
                shape.setStroke(3, Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    shape.setPadding(0, 5,0,5);
                }


                llMonday.setBackground(shape);
                llMonday.addView(llLessonTime);
                llMonday.addView(tvLessonName);
                llMonday.addView(tvClassRoom);
                llMonday.addView(tvTeacher);

                llMonday.addView(line1);

                /*if(i != numberOfLessons-1) {
                    llMonday.addView(tvBreakTime);
                    llMonday.addView(line2);
                }*/
            }
            //Пятница
            if (scheduleItem.getWeekDayNumber().equals("5")  && scheduleItem.getDayOfWeek().equals("Пятница")){
                LinearLayout llMonday = findViewById(R.id.ll_friday_items);
                llMonday.removeAllViews();

                TextView tvLessonNumber = new TextView(this);
                TextView tvLessonName = new TextView(this);
                TextView tvLessonTime = new TextView(this);
                TextView tvClassRoom = new TextView(this);
                TextView tvTeacher = new TextView(this);
                TextView tvBreakTime = new TextView(this);

                LinearLayout llLessonTime = new LinearLayout(this);
                llLessonTime.setOrientation(LinearLayout.HORIZONTAL);

                tvLessonNumber.setText("Урок ");
                tvLessonNumber.setTextSize(20);
                tvLessonNumber.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonNumber.setPadding(30, 10, 0, 4);

                tvLessonTime.setText(scheduleItem.getStartTime());
                tvLessonTime.setTextSize(18);
                tvLessonTime.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonTime.setPadding(20, 4, 0, 4);

                llLessonTime.addView(tvLessonNumber);
                llLessonTime.addView(tvLessonTime);

                tvLessonName.setText(scheduleItem.getSubjectName());
                tvLessonName.setTextSize(30);
                tvLessonName.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonName.setPadding(30, 4, 0, 4);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                layoutParams.gravity = Gravity.END | Gravity.TOP;
                tvClassRoom.setLayoutParams(layoutParams);

                tvClassRoom.setText("Каб." + scheduleItem.getClassroomNumber());
                tvClassRoom.setTextSize(22);
                tvClassRoom.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvClassRoom.setPadding(30, 4, 0, 4);

                tvTeacher.setText("Преподаватель: " + scheduleItem.getTeacher());
                tvTeacher.setTextSize(20);
                tvTeacher.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvTeacher.setPadding(30, 4, 0, 4);

                View line1 = new View(this);
                line1.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line1.setLayoutParams(layoutParams1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line1.setPadding(0, 5,0,5);
                }

                View line2 = new View(this);
                line2.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line2.setLayoutParams(layoutParams2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line2.setPadding(0, 5,0,5);
                }

                tvBreakTime.setText("Перемена: 0 минут");
                tvBreakTime.setTextSize(20);
                tvBreakTime.setTextColor(Color.parseColor("#cccccc"));
                tvBreakTime.setPadding(30, 5, 0, 5);

                GradientDrawable breakShape = new GradientDrawable();
                breakShape.setShape(GradientDrawable.RECTANGLE);
                breakShape.setColor(Color.parseColor("#7899D4"));
                breakShape.setStroke(3, Color.WHITE);
                tvBreakTime.setBackground(breakShape);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(20);
                shape.setColor(Color.parseColor("#13293D"));
                shape.setStroke(3, Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    shape.setPadding(0, 5,0,5);
                }


                llMonday.setBackground(shape);
                llMonday.addView(llLessonTime);
                llMonday.addView(tvLessonName);
                llMonday.addView(tvClassRoom);
                llMonday.addView(tvTeacher);

                llMonday.addView(line1);

                /*if(i != numberOfLessons-1) {
                    llMonday.addView(tvBreakTime);
                    llMonday.addView(line2);
                }*/
            }
            //Суббота
            if (scheduleItem.getWeekDayNumber().equals("6")  && scheduleItem.getDayOfWeek().equals("Суббота")){
                LinearLayout llMonday = findViewById(R.id.ll_saturday_items);
                llMonday.removeAllViews();

                TextView tvLessonNumber = new TextView(this);
                TextView tvLessonName = new TextView(this);
                TextView tvLessonTime = new TextView(this);
                TextView tvClassRoom = new TextView(this);
                TextView tvTeacher = new TextView(this);
                TextView tvBreakTime = new TextView(this);

                LinearLayout llLessonTime = new LinearLayout(this);
                llLessonTime.setOrientation(LinearLayout.HORIZONTAL);

                tvLessonNumber.setText("Урок ");
                tvLessonNumber.setTextSize(20);
                tvLessonNumber.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonNumber.setPadding(30, 10, 0, 4);

                tvLessonTime.setText(scheduleItem.getStartTime());
                tvLessonTime.setTextSize(18);
                tvLessonTime.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonTime.setPadding(20, 4, 0, 4);

                llLessonTime.addView(tvLessonNumber);
                llLessonTime.addView(tvLessonTime);

                tvLessonName.setText(scheduleItem.getSubjectName());
                tvLessonName.setTextSize(30);
                tvLessonName.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvLessonName.setPadding(30, 4, 0, 4);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                layoutParams.gravity = Gravity.END | Gravity.TOP;
                tvClassRoom.setLayoutParams(layoutParams);

                tvClassRoom.setText("Каб." + scheduleItem.getClassroomNumber());
                tvClassRoom.setTextSize(22);
                tvClassRoom.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvClassRoom.setPadding(30, 4, 0, 4);

                tvTeacher.setText("Преподаватель: " + scheduleItem.getTeacher());
                tvTeacher.setTextSize(20);
                tvTeacher.setTextColor(Color.parseColor("#FFFFFFFF"));
                tvTeacher.setPadding(30, 4, 0, 4);

                View line1 = new View(this);
                line1.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line1.setLayoutParams(layoutParams1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line1.setPadding(0, 5,0,5);
                }

                View line2 = new View(this);
                line2.setBackgroundColor(Color.WHITE);
                LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        5
                );
                line2.setLayoutParams(layoutParams2);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    line2.setPadding(0, 5,0,5);
                }

                tvBreakTime.setText("Перемена: 0 минут");
                tvBreakTime.setTextSize(20);
                tvBreakTime.setTextColor(Color.parseColor("#cccccc"));
                tvBreakTime.setPadding(30, 5, 0, 5);

                GradientDrawable breakShape = new GradientDrawable();
                breakShape.setShape(GradientDrawable.RECTANGLE);
                breakShape.setColor(Color.parseColor("#7899D4"));
                breakShape.setStroke(3, Color.WHITE);
                tvBreakTime.setBackground(breakShape);

                GradientDrawable shape = new GradientDrawable();
                shape.setShape(GradientDrawable.RECTANGLE);
                shape.setCornerRadius(20);
                shape.setColor(Color.parseColor("#13293D"));
                shape.setStroke(3, Color.WHITE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    shape.setPadding(0, 5,0,5);
                }


                llMonday.setBackground(shape);
                llMonday.addView(llLessonTime);
                llMonday.addView(tvLessonName);
                llMonday.addView(tvClassRoom);
                llMonday.addView(tvTeacher);

                llMonday.addView(line1);

                /*if(i != numberOfLessons-1) {
                    llMonday.addView(tvBreakTime);
                    llMonday.addView(line2);
                }*/
            }
        }

        LinearLayout llMonday = findViewById(R.id.ll_monday_items);
        TextView Monday = findViewById(R.id.tv_monday_header);
        if(llMonday.getChildCount() != 0) {
            llMonday.setVisibility(View.VISIBLE);
            Monday.setVisibility(View.VISIBLE);
        }
        else {
            llMonday.setVisibility(View.GONE);
            Monday.setVisibility(View.GONE);

        }
        LinearLayout llTuesday = findViewById(R.id.ll_tuesday_items);
        TextView Tuesday = findViewById(R.id.tv_tuesday_header);
        if(llTuesday.getChildCount() != 0) {
            llTuesday.setVisibility(View.VISIBLE);
            Tuesday.setVisibility(View.VISIBLE);
        }
        else {
            llTuesday.setVisibility(View.GONE);
            Tuesday.setVisibility(View.GONE);
        }
        LinearLayout llWednesday = findViewById(R.id.ll_wednesday_items);
        TextView Wednesday = findViewById(R.id.tv_wednesday_header);
        if(llWednesday.getChildCount() != 0) {
            llWednesday.setVisibility(View.VISIBLE);
            Wednesday.setVisibility(View.VISIBLE);
        }
        else {
            llWednesday.setVisibility(View.GONE);
            Wednesday.setVisibility(View.GONE);
        }
        LinearLayout llThursday = findViewById(R.id.ll_thursday_items);
        TextView Thursday = findViewById(R.id.tv_thursday_header);
        if(llThursday.getChildCount() != 0) {
            llThursday.setVisibility(View.VISIBLE);
            Thursday.setVisibility(View.VISIBLE);
        }
        else {
            llThursday.setVisibility(View.GONE);
            Thursday.setVisibility(View.GONE);
        }
        LinearLayout llFriday = findViewById(R.id.ll_friday_items);
        TextView Friday = findViewById(R.id.tv_friday_header);
        if(llFriday.getChildCount() != 0) {
            llFriday.setVisibility(View.VISIBLE);
            Friday.setVisibility(View.VISIBLE);
        }
        else {
            llFriday.setVisibility(View.GONE);
            Friday.setVisibility(View.GONE);
        }
        LinearLayout llSaturday = findViewById(R.id.ll_saturday_items);
        TextView Saturday = findViewById(R.id.tv_saturday_header);
        if(llSaturday.getChildCount() != 0) {
            llSaturday.setVisibility(View.VISIBLE);
            Saturday.setVisibility(View.VISIBLE);
        }
        else {
            llSaturday.setVisibility(View.GONE);
            Saturday.setVisibility(View.GONE);
        }

    }

    //ScheduleEnd

    //MarksStart

    private void showMarksLayout(){
        scheduleCL.setVisibility(View.GONE);
        marksCL.setVisibility(View.VISIBLE);
        chatsCL.setVisibility(View.GONE);
        profileCL.setVisibility(View.GONE);
    }
    private void updateMarks(){

        String url = Constants.ApiUrl + "mark/studentall/1";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            try {
                JSONArray jsonArray = new JSONArray(response);
                MarkList.clear();
                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject OneResponse = new JSONObject(jsonArray.get(i).toString());
                    Mark mark = new Mark();
                    mark.setID(OneResponse.get("ID").toString());
                    mark.setLastName(OneResponse.get("LastName").toString());
                    mark.setFirstName(OneResponse.get("FirstName").toString());
                    mark.setPatronymic(OneResponse.get("Patronymic").toString());
                    mark.setGroupNumber(OneResponse.get("GroupNumber").toString());
                    mark.setSubjectName(OneResponse.get("SubjectName").toString());
                    mark.setGrade(OneResponse.get("Grade").toString());
                    mark.setLessonDate(OneResponse.get("LessonDate").toString());
                    MarkList.add(mark);
                }
                MarkListAdapter.notifyDataSetChanged();

            } catch (JSONException e) {

                Log.i("exception", e.getMessage());
            }
        }, error -> {
            Toast.makeText(this, "Отсутствует подключение к серверу", Toast.LENGTH_SHORT).show();
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>(super.getHeaders());
                params.put("Authorization","Bearer " + Constants.UserToken);
                return params;
            }
        };
        queue.add(request);

    }



    //MarksEnd



    //ChatStart

    private void showChatsLayout(){
        scheduleCL.setVisibility(View.GONE);
        marksCL.setVisibility(View.GONE);
        chatsCL.setVisibility(View.VISIBLE);
        profileCL.setVisibility(View.GONE);
    }
    private void updateChats() {


    }


    class ViewPagerAdapter extends FragmentPagerAdapter {

        private ArrayList<Fragment> fragments;
        private ArrayList<String> titles;

        ViewPagerAdapter(FragmentManager fm){
            super(fm);
            this.fragments=new ArrayList<>();
            this.titles=new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        public void addFragment(Fragment fragment,String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    //ChatEnd


    //ProfileStart

    private void showProfileLayout() {
        scheduleCL.setVisibility(View.GONE);
        marksCL.setVisibility(View.GONE);
        chatsCL.setVisibility(View.GONE);
        profileCL.setVisibility(View.VISIBLE);
    }
    private void updateProfile() {

    }

    public void ToTuner(View view){
        Intent tuner = new Intent(MainFrameActivity.this, TunerActivity.class);
        startActivity(tuner);
    }

    //ProfileEnd

}