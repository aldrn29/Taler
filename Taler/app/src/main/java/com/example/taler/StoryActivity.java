package com.example.taler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

public class StoryActivity extends AppCompatActivity {
    int CHECK_NUM = 0; // 스위치 변경확인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        //Button Event: play and replay
        final ImageButton toggle = findViewById(R.id.toggle_button_play);
        toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CHECK_NUM == 0) {
                    toggle.setSelected(true);
                    CHECK_NUM = 1;
                } else{
                    toggle.setSelected(false);
                    CHECK_NUM = 0;
                }
            }
        });
    }
}