package ru.dev2dev.firebasetest.models;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by dmitriy on 04.01.17.
 */
@IgnoreExtraProperties
public class User {

    public String name;
    public String email;

    public User() {

    }

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }
}
