package com.example.taler.Profile;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String userId;
    public String email;
    public int point;
    public ArrayList<Boolean> story_progress;

    public User() {
        //디폴트 생성자, DataSnapshot.getValue(User.class) 필요사항
    }

    public User(String userId, String email, int point, ArrayList<Boolean> story_progress) {
        this.userId = userId;
        this.email = email;
        this.point = point;
        this.story_progress = story_progress;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("email", email);
        result.put("point", point);
        result.put("story_progress", story_progress);

        return result;
    }
}
