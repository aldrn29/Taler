package com.example.taler;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class MediaActivity extends AppCompatActivity {

    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        /*
        * start(): 재생 시작/ stop(): 정지/ prepare(): 준비/ pause(): 일시 정지
        * release(): 메모리 해제/ seekTo(): 재생 위치 지정/ getCurrentPosition(): 재생 위치/ getDuration(): 재생 시간
        * getVideoHeight():영상 높이값/ getVideoWidth():영상 너비값/ setLooping():반복 설정/ setVolumn():볼륨 설정
        * */

        // Button Event: play
        final ImageButton play = findViewById(R.id.imageButton_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // button image change
                if (!view.isSelected()) {
                    view.setSelected(true);

                    // mp3 file create
                    player = MediaPlayer.create(MediaActivity.this, R.raw.friends_101);
                    player.start();
                } else {
                    view.setSelected(false);
                    player.pause();
                }

            }
        });

        // Button Event: replay
        ImageButton replay = findViewById(R.id.imageButton_replay);
        replay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) player.seekTo(0);
            }
        });
    }
}