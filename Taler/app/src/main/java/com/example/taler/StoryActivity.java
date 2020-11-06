package com.example.taler;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;

import org.apache.commons.lang.StringEscapeUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class StoryActivity extends AppCompatActivity {
    public static final String PREFS_NAME = "prefs";
    private static final String MSG_KEY = "status";

    Button buttonSpeech;
    TextView textResult;
    Spinner spinnerMode;
    EditText editID;

    String curMode;
    String result;

    int maxLenSpeech = 16000 * 45;
    byte [] speechData = new byte [maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            String v = bd.getString(MSG_KEY);
            switch (msg.what) {
                // 녹음이 시작되었음(버튼)
                case 1:
                    textResult.setText(v);
                    buttonSpeech.setText("PUSH TO STOP");
                    break;
                // 녹음이 정상적으로 종료되었음(버튼 또는 max time)
                case 2:
                    textResult.setText(v);
                    buttonSpeech.setEnabled(false);
                    break;
                // 녹음이 비정상적으로 종료되었음(마이크 권한 등)
                case 3:
                    textResult.setText(v);
                    buttonSpeech.setText("PUSH TO START");
                    break;
                // 인식이 비정상적으로 종료되었음(timeout 등)
                case 4:
                    textResult.setText(v);
                    buttonSpeech.setEnabled(true);
                    buttonSpeech.setText("PUSH TO START");
                    break;
                // 인식이 정상적으로 종료되었음 (thread내에서 exception포함)
                case 5:
                    textResult.setText(StringEscapeUtils.unescapeJava(result));
                    buttonSpeech.setEnabled(true);
                    buttonSpeech.setText("PUSH TO START");
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void SendMessage(String str, int id) {
        Message msg = handler.obtainMessage();
        Bundle bd = new Bundle();
        bd.putString(MSG_KEY, str);
        msg.what = id;
        msg.setData(bd);
        handler.sendMessage(msg);
    }

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
                // 재생 버튼 눌렀을때
                if(CHECK_NUM == 0) {
                    toggle.setSelected(true);
                    CHECK_NUM = 1;
                }
                // 되감기 버튼 눌렀을 때
                else{
                    toggle.setSelected(false);
                    CHECK_NUM = 0;
                }
            }
        });
    }
}