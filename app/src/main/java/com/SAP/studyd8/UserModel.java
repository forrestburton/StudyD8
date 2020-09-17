package com.SAP.studyd8;

import java.util.List;

public class UserModel {
    String userId, username, firstName, lastName, major, university, studyHabits;
    List<String> courses;

    public UserModel(String userId, String username, String firstName, String lastName, String major, String university, String studyHabits, List<String> courses) {
        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.major = major;
        this.university = university;
        this.studyHabits = studyHabits;
        this.courses = courses;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getStudyHabits() {
        return studyHabits;
    }

    public void setStudyHabits(String studyHabits) {
        this.studyHabits = studyHabits;
    }

    public List<String> getCourses() {
        return courses;
    }

    public void setCourses(List<String> courses) {
        this.courses = courses;
    }
}
