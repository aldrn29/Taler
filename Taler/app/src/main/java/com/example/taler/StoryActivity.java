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

    Button buttonStart;
    TextView textResult; //음성 인식 결과를 보여준다.
    Spinner spinnerMode;
//    EditText editID;

    String curMode;
    String result;

    int maxLenSpeech = 16000 * 45;
    byte [] speechData = new byte [maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;

    int CHECK_NUM = 0; // 스위치 변경확인

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
                    buttonStart.setText("PUSH TO STOP");
                    break;
                // 녹음이 정상적으로 종료되었음(버튼 또는 max time)
                case 2:
                    textResult.setText(v);
                    buttonStart.setEnabled(false);
                    break;
                // 녹음이 비정상적으로 종료되었음(마이크 권한 등)
                case 3:
                    textResult.setText(v);
                    buttonStart.setText("PUSH TO START");
                    break;
                // 인식이 비정상적으로 종료되었음(timeout 등)
                case 4:
                    textResult.setText(v);
                    buttonStart.setEnabled(true);
                    buttonStart.setText("PUSH TO START");
                    break;
                // 인식이 정상적으로 종료되었음 (thread내에서 exception포함)
                case 5:
                    textResult.setText(StringEscapeUtils.unescapeJava(result));
                    buttonStart.setEnabled(true);
                    buttonStart.setText("PUSH TO START");
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textResult = (TextView)findViewById(R.id.textResult);
//        spinnerMode = (Spinner)findViewById(R.id.spinnerMode);
//        editID = (EditText)findViewById(R.id.editID);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//        editID.setText(settings.getString("client-id", "YOUR_CLIENT_ID"));

//        ArrayList<String> modeArr = new ArrayList<>();
//        modeArr.add("한국어인식");
//        modeArr.add("영어인식");
//        modeArr.add("영어발음평가");
//        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(
//                this, android.R.layout.simple_spinner_item, modeArr);
//        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        spinnerMode.setAdapter(modeAdapter);
//        spinnerMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                curMode = parent.getItemAtPosition(pos).toString();
//            }
//            public void onNothingSelected(AdapterView<?> parent) {
//                curMode = "";
//            }
//        });

        curMode = "영어인식";

//        editID.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if(actionId== EditorInfo.IME_ACTION_DONE){
//                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
//                    SharedPreferences.Editor editor = settings.edit();
//                    editor.putString("client-id", v.getText().toString());
//                    editor.apply();
//                }
//                return false;
//            }
//        });

        final Button buttonStart = findViewById(R.id.buttonStart);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //녹음중이면 멈추고
                if (isRecording) {
                    forceStop = true;
                }
                // 아니면 녹음 시작
                else {
                    try {
                        new Thread(new Runnable() {
                            public void run() {
                                SendMessage("Recording...", 1);
                                try {
                                    recordSpeech();
                                    SendMessage("Recognizing...", 2);
                                } catch (RuntimeException e) {
                                    SendMessage(e.getMessage(), 3);
                                    return;
                                }
                                //여기가 결과 송출인듯..
                                Thread threadRecog = new Thread(new Runnable() {
                                    public void run() {
                                        result = sendDataAndGetResult();
                                    }
                                });
                                threadRecog.start();
                                try {
                                    threadRecog.join(20000);
                                    if (threadRecog.isAlive()) {
                                        threadRecog.interrupt();
                                        SendMessage("No response from server for 20 secs", 4);
                                    } else {
                                        SendMessage("OK", 5);
                                    }
                                } catch (InterruptedException e) {
                                    SendMessage("Interrupted", 4);
                                }
                            }
                        }).start();
                    } catch (Throwable t) {
                        textResult.setText("ERROR: " + t.toString());
                        forceStop = false;
                        isRecording = false;
                    }
                }
            }
        });

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

    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }

    public void recordSpeech() throws RuntimeException {
        try {
            int bufferSize = AudioRecord.getMinBufferSize(
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT);
            AudioRecord audio = new AudioRecord(
                    MediaRecorder.AudioSource.VOICE_RECOGNITION,
                    16000, // sampling frequency
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    bufferSize);
            lenSpeech = 0;
            if (audio.getState() != AudioRecord.STATE_INITIALIZED) {
                throw new RuntimeException("ERROR: Failed to initialize audio device. Allow app to access microphone");
            }
            else {
                short [] inBuffer = new short [bufferSize];
                forceStop = false;
                isRecording = true;
                audio.startRecording();
                while (!forceStop) {
                    int ret = audio.read(inBuffer, 0, bufferSize);
                    for (int i = 0; i < ret ; i++ ) {
                        if (lenSpeech >= maxLenSpeech) {
                            forceStop = true;
                            break;
                        }
                        speechData[lenSpeech*2] = (byte)(inBuffer[i] & 0x00FF);
                        speechData[lenSpeech*2+1] = (byte)((inBuffer[i] & 0xFF00) >> 8);
                        lenSpeech++;
                    }
                }
                audio.stop();
                audio.release();
                isRecording = false;
            }
        } catch(Throwable t) {
            throw new RuntimeException(t.toString());
        }
    }

    public String sendDataAndGetResult () {
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Recognition";
        String accessKey = "0909ffdc-4971-4e04-b1bc-c9cabd71dabc".trim(); //0909ffdc-4971-4e04-b1bc-c9cabd71dabc
        String languageCode = "english";
        String audioContents;

        Gson gson = new Gson();

//        switch (curMode) {
//            case "한국어인식":
//                languageCode = "korean";
//                break;
//            case "영어인식":
//                languageCode = "english";
//                break;
//            case "영어발음평가":
//                languageCode = "english";
//                openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";
//                break;
//            default:
//                return "ERROR: invalid mode";
//        }

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech*2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        argument.put("audio", audioContents);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        Integer responseCode;
        String responBody;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            if ( responseCode == 200 ) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                responBody = readStream(is);
                return responBody;
            }
            else
                return "ERROR: " + Integer.toString(responseCode);
        } catch (Throwable t) {
            return "ERROR: " + t.toString();
        }
    }

}