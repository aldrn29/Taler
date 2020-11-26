package com.example.taler.Profile;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class User {
    public String username;
    public String email;
    public int point;

    public User() {
        //디폴트 생성자, DataSnapshot.getValue(User.class) 필요사항
    }

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.point = 1;
    }
}
