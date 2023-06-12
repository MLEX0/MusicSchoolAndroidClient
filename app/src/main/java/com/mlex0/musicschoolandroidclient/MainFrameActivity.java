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
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.tabs.TabLayout;
import com.mlex0.musicschoolandroidclient.Fragments.ChatsFragment;
import com.mlex0.musicschoolandroidclient.Fragments.UsersFragment;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

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

        /*LinearLayout llMonday = findViewById(R.id.ll_monday_items);

        int numberOfLessons = 5;
        for (int i = 0; i < numberOfLessons; i++) {
            TextView tvLesson = new TextView(this);

            tvLesson.setText("Урок " + (i+1));
            tvLesson.setTextSize(24);
            tvLesson.setTextColor(Color.parseColor("#FFFFFFFF"));
            tvLesson.setPadding(8, 4, 0, 4);
            llMonday.addView(tvLesson);
        }*/

        LinearLayout llMonday = findViewById(R.id.ll_monday_items);

        llMonday.removeAllViews();

        int numberOfLessons = 5;
        for (int i = 0; i < numberOfLessons; i++)
        {
            TextView tvLessonNumber = new TextView(this);
            TextView tvLessonName = new TextView(this);
            TextView tvLessonTime = new TextView(this);
            TextView tvClassRoom = new TextView(this);
            TextView tvTeacher = new TextView(this);
            TextView tvBreakTime = new TextView(this);

            LinearLayout llLessonTime = new LinearLayout(this);
            llLessonTime.setOrientation(LinearLayout.HORIZONTAL);

            tvLessonNumber.setText("Урок " + (i+1));
            tvLessonNumber.setTextSize(20);
            tvLessonNumber.setTextColor(Color.parseColor("#FFFFFFFF"));
            tvLessonNumber.setPadding(30, 10, 0, 4);

            tvLessonTime.setText((8+i+1) +":00-" +(10+i) +":00");
            tvLessonTime.setTextSize(18);
            tvLessonTime.setTextColor(Color.parseColor("#FFFFFFFF"));
            tvLessonTime.setPadding(20, 4, 0, 4);

            llLessonTime.addView(tvLessonNumber);
            llLessonTime.addView(tvLessonTime);

            tvLessonName.setText("Музыка");
            tvLessonName.setTextSize(30);
            tvLessonName.setTextColor(Color.parseColor("#FFFFFFFF"));
            tvLessonName.setPadding(30, 4, 0, 4);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            );
            layoutParams.gravity = Gravity.END | Gravity.TOP;
            tvClassRoom.setLayoutParams(layoutParams);

            tvClassRoom.setText("Каб." + (50+i));
            tvClassRoom.setTextSize(22);
            tvClassRoom.setTextColor(Color.parseColor("#FFFFFFFF"));
            tvClassRoom.setPadding(30, 4, 0, 4);

            tvTeacher.setText("Преподаватель: Липовна Ирина Владимировна");
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

            if(i != numberOfLessons-1) {
                llMonday.addView(tvBreakTime);
                llMonday.addView(line2);
            }

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