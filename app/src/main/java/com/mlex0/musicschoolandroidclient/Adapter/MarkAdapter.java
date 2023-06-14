package com.mlex0.musicschoolandroidclient.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mlex0.musicschoolandroidclient.Model.Mark;
import com.mlex0.musicschoolandroidclient.R;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MarkAdapter extends ArrayAdapter<Mark> {

    public MarkAdapter(@NonNull Context context, ArrayList<Mark> userArrayList) {
        super(context, R.layout.marks_item, (List<Mark>) userArrayList);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Mark mark = getItem(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(getContext())
                    .inflate(R.layout.marks_item, parent, false);
        }

        TextView subject = convertView.findViewById(R.id.tv_subject);
        TextView date = convertView.findViewById(R.id.tv_date);
        TextView grade = convertView.findViewById(R.id.grade);

        subject.setText("Предмет: " + mark.getSubjectName());


        String dateString = "2023-02-14T13:45:00.000Z";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        formatter = formatter.withZone(ZoneId.of("UTC"));
        LocalDateTime dateTime = LocalDateTime.parse(dateString, formatter);


        date.setText("Дата: " + dateTime.toString().replace('T', ' ').replace('-', '.'));

        grade.setText(mark.getGrade());

        return convertView;
    }
}
