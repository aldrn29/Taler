package com.example.taler.Profile;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class User {
    public String userId;
    public String email;
    public int counter_story, counter_music, counter_video;
    public ArrayList<Boolean> story_progress;
    public ArrayList<Boolean> music_progress;
    public ArrayList<Boolean> video_progress;

    public User() {
        //디폴트 생성자, DataSnapshot.getValue(User.class) 필요사항
    }

    public User(String userId, String email, int counter, ArrayList<Boolean> endingList, ArrayList<Boolean> musicList, ArrayList<Boolean> videoList) {
        this.userId = userId;
        this.email = email;
        this.counter_story = counter;
        this.counter_music = counter;
        this.counter_video = counter;
        this.story_progress = endingList;
        this.music_progress = musicList;
        this.video_progress = videoList;
    }


    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("email", email);
        result.put("counter_story", counter_story);
        result.put("counter_music", counter_music);
        result.put("counter_video", counter_video);
        result.put("story_progress", story_progress);
        result.put("music_progress", music_progress);
        result.put("video_progress", video_progress);

        return result;
    }
}
