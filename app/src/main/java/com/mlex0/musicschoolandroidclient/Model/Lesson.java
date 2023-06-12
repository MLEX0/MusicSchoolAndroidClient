package com.mlex0.musicschoolandroidclient.Model;

public class Lesson {
    private String name;
    private String teacher;
    private String startTime;
    private String endTime;

    public Lesson(String name, String teacher, String startTime, String endTime) {
        this.name = name;
        this.teacher = teacher;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public String getTeacher() {
        return teacher;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
}
