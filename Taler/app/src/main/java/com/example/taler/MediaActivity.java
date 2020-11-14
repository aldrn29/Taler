package com.example.taler;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaActivity extends AppCompatActivity {

    MediaPlayer player;
    TextView text;

    ASRmasterAPI asr;
    ImageButton playButton;
    ImageButton recordButton;   // 녹음 start, end
    TextView textResult;        // 녹음 결과
    int mode;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        playButton = findViewById(R.id.imageButton_play);
        recordButton = findViewById(R.id.imageButton_record);
        textResult = findViewById(R.id.textView_result);

        asr = new ASRmasterAPI(recordButton, textResult, 1);

        /*
        * start(): 재생 시작/ stop(): 정지/ prepare(): 준비/ pause(): 일시 정지
        * release(): 메모리 해제/ seekTo(): 재생 위치 지정/ getCurrentPosition(): 재생 위치/ getDuration(): 재생 시간
        * getVideoHeight():영상 높이값/ getVideoWidth():영상 너비값/ setLooping():반복 설정/ setVolumn():볼륨 설정
        */

        // Button Event: play
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // button image change
                if (!view.isSelected()) {
                    view.setSelected(true);

                    // mp3 file create
                    player = MediaPlayer.create(MediaActivity.this, R.raw.friends_101);
                    player.start();

                    // txt
                    text = findViewById(R.id.textView_script);
                    String s = readTxtfile(MediaActivity.this, R.raw.friends_);
                    text.setText(s);

                } else {
                    view.setSelected(false);
                    player.pause();
                }
            }
        });


    }

    public String readTxtfile(Context context, int resId) {
        String result = "";
        InputStream txtResource = context.getResources().openRawResource(resId);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int i;
        try {
            i = txtResource.read();
            while (i != -1) {
                byteArrayOutputStream.write(i);
                i = txtResource.read();
            }
            result = new String(byteArrayOutputStream.toByteArray(), "UTF-8");
            txtResource.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result.trim();
    }
}