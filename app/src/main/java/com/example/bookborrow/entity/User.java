package com.example.bookborrow.entity;

public class User {
    private String userID;
    private String email;
    private String password;
    private String nameSurname;
    private String lat;
    private String longt;
    public String getUserID() {
        return userID;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getNameSurname() {
        return nameSurname;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNameSurname(String nameSurname) {
        this.nameSurname = nameSurname;
    }

    public String getLat() {
        return lat;
    }

    public String getLongt() {
        return longt;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLongt(String longt) {
        this.longt = longt;
    }
}
