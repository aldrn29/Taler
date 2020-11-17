package com.example.taler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
//Todo 넘어온 PK로 이미지, 오디오, 선택지를 변경시킨다. 또 선택지에 따라 이어서 변경하도록 한다. 같은 타이틀, 다음카드로. 음성인식/평가로 넘어간다
public class StoryCardActivity extends AppCompatActivity {
    int CHECK_NUM = 0; // 스위치 변경확인

    ASRmasterAPI asr;
    ImageButton toggleButton;
    ImageButton speechButton;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        textView = findViewById(R.id.test_text);
        toggleButton = findViewById(R.id.toggle_button_play);
        speechButton = findViewById(R.id.speech_button);
        asr = new ASRmasterAPI(speechButton, textView, 1);

        String id = "";
        String title = "";
//        img = thumbnail

        Bundle extras = getIntent().getExtras();

        id = extras.getString("id");
        title = extras.getString("title");

        String str = id + '\n' + title;
        textView.setText(str);

        //Button Event: play and replay
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(CHECK_NUM == 0) {
                    toggleButton.setSelected(true);
                    CHECK_NUM = 1;
                } else{
                    toggleButton.setSelected(false);
                    CHECK_NUM = 0;
                }
            }
        });


    }
}