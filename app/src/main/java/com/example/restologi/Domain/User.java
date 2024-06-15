package com.example.restologi.Domain;

public class User {
    public String uid;
    public String name;
    public String email;
    public String phoneNumber;
    public String address;

    public User() {
    }

    public User(String uid, String name, String email) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = "";
    }

    public User(String uid, String name, String email, String phoneNumber) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
    }

    public User(String uid, String name, String email, String phoneNumber, String address) {
        this.uid = uid;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }
}
