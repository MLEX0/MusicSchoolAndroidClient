package com.mlex0.musicschoolandroidclient.Model;

public class TeacherSchedule {
    private String IDLesson;
    private String GroupNumber;
    private String WeekDayNumber;
    private String LessonDate;
    private String DayOfWeek;
    private String StartTime;
    private String SubjectName;
    private String ClassroomNumber;
    private String Floor;

    public String getIDLesson() {
        return IDLesson;
    }

    public void setIDLesson(String IDLesson) {
        this.IDLesson = IDLesson;
    }

    public String getGroupNumber() {
        return GroupNumber;
    }

    public void setGroupNumber(String groupNumber) {
        GroupNumber = groupNumber;
    }

    public String getWeekDayNumber() {
        return WeekDayNumber;
    }

    public void setWeekDayNumber(String weekDayNumber) {
        WeekDayNumber = weekDayNumber;
    }

    public String getLessonDate() {
        return LessonDate;
    }

    public void setLessonDate(String lessonDate) {
        LessonDate = lessonDate;
    }

    public String getDayOfWeek() {
        return DayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        DayOfWeek = dayOfWeek;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getSubjectName() {
        return SubjectName;
    }

    public void setSubjectName(String subjectName) {
        SubjectName = subjectName;
    }

    public String getClassroomNumber() {
        return ClassroomNumber;
    }

    public void setClassroomNumber(String classroomNumber) {
        ClassroomNumber = classroomNumber;
    }

    public String getFloor() {
        return Floor;
    }

    public void setFloor(String floor) {
        Floor = floor;
    }
}
