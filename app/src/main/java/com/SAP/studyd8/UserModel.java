package com.SAP.studyd8;

import java.util.List;

public class UserModel {
    String userId, user_username, user_firstName, user_lastName, user_major, user_university, user_studyHabits;
    List<String> courses;

    public UserModel(String userId, String user_username, String user_firstName, String user_lastName, String user_major, String user_university, String user_studyHabits, List<String> courses) {
        this.userId = userId;
        this.user_username = user_username;
        this.user_firstName = user_firstName;
        this.user_lastName = user_lastName;
        this.user_major = user_major;
        this.user_university = user_university;
        this.user_studyHabits = user_studyHabits;
        this.courses = courses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUser_username() {
        return user_username;
    }

    public void setUser_username(String user_username) {
        this.user_username = user_username;
    }

    public String getUser_firstName() {
        return user_firstName;
    }

    public void setUser_firstName(String user_firstName) {
        this.user_firstName = user_firstName;
    }

    public String getUser_lastName() {
        return user_lastName;
    }

    public void setUser_lastName(String user_lastName) {
        this.user_lastName = user_lastName;
    }

    public String getUser_major() {
        return user_major;
    }

    public void setUser_major(String user_major) {
        this.user_major = user_major;
    }

    public String getUser_university() {
        return user_university;
    }

    public void setUser_university(String user_university) {
        this.user_university = user_university;
    }

    public String getUser_studyHabits() {
        return user_studyHabits;
    }

    public void setUser_studyHabits(String user_studyHabits) {
        this.user_studyHabits = user_studyHabits;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
