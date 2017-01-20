package ru.dev2dev.firebasetest.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by dmitriy on 04.01.17.
 */

@IgnoreExtraProperties
public class Joke {

    public String date;
    public String jokeText;
    public String username;
    public int likeCount;
    public ArrayList<String> voteUserNames = new ArrayList<>();

    public Joke() {

    }

    public Joke(String date, String jokeText, String username) {
        this.date = date;
        this.jokeText = jokeText;
        this.username = username;
    }

}
