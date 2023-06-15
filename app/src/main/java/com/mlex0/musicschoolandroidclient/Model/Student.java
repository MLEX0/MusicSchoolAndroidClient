package com.mlex0.musicschoolandroidclient.Model;

public class Student {
    private String ID;
    private String FirstName;
    private String LastName;
    private String Patronymic;
    private String Birthday;
    private String IdStudyGroup;
    private String IdUser;
    private String Phone;
    private String Gender;

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getPatronymic() {
        return Patronymic;
    }

    public void setPatronymic(String patronymic) {
        Patronymic = patronymic;
    }

    public String getBirthday() {
        return Birthday;
    }

    public void setBirthday(String birthday) {
        Birthday = birthday;
    }

    public String getIdStudyGroup() {
        return IdStudyGroup;
    }

    public void setIdStudyGroup(String idStudyGroup) {
        IdStudyGroup = idStudyGroup;
    }

    public String getIdUser() {
        return IdUser;
    }

    public void setIdUser(String idUser) {
        IdUser = idUser;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public String getGender() {
        return Gender;
    }

    public void setGender(String gender) {
        Gender = gender;
    }
}
