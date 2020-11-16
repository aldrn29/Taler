package com.example.taler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
//Todo 넘어온 PK로 이미지, 오디오, 선택지를 변경시킨다. 또 선택지에 따라 이어서 변경하도록 한다. 같은 타이틀, 다음카드로. 음성인식/평가로 넘어간다
public class StoryCardActivity extends AppCompatActivity {
    int CHECK_NUM = 0; // 스위치 변경확인

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        String id = "";
        String title = "";
//        img = thumbnail

        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");
        title = extras.getString("title");

        TextView textView = findViewById(R.id.test_text);

        String str = id + '\n' + title;
        textView.setText(str);

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