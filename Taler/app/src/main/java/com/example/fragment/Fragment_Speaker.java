package com.example.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.taler.FloatingActivity;
import com.example.taler.MainActivity;
import com.example.taler.MediaActivity;
import com.example.taler.MediaMenu.MediaMenuActivity;
import com.example.taler.PopTestActivity;
import com.example.taler.Profile.ProfileFragment;
import com.example.taler.SharedViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.taler.R;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import static android.app.Activity.RESULT_OK;


public class Fragment_Speaker extends Fragment {


    private SharedViewModel sharedViewModel;        //fragment 간 데이터 전송을 위한 것.

    ImageButton btn_speak;   //누르고 말하기
    ImageButton btn_clear;
    TextView user_speaking; //유저 말한 문장
    TextView access_key;    //엑세스 키
    String result;
    int maxLenSpeech = 16000 * 45;
    byte [] speechData = new byte [maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;

    public static final String PREFS_NAME = "prefs";
    private static final String MSG_KEY = "status";
    boolean isFabOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    //사용자 음성 입력. 녹음 핸들러
    @SuppressLint("HandlerLeak")
    final Handler handler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            String v = bd.getString(MSG_KEY);
            switch (msg.what) {
                // 녹음이 시작되었음(버튼)
                case 1:
                    if (user_speaking != null)user_speaking.setText(v);
                    btn_speak.setSelected(true);
                    break;
                // 녹음이 정상적으로 종료되었음(버튼 또는 max time)
                case 2:
                    if (user_speaking != null)user_speaking.setText(v);
                    btn_speak.setSelected(false);
                    btn_speak.setEnabled(false);
                    break;
                // 녹음이 비정상적으로 종료되었음(마이크 권한 등)
                case 3:
                    if (user_speaking != null)user_speaking.setText(v);
                    btn_speak.setSelected(false);
                    break;
                // 인식이 비정상적으로 종료되었음(timeout 등)
                case 4:
                    if (user_speaking != null)user_speaking.setText(v);
                    btn_speak.setSelected(false);
                    btn_speak.setEnabled(true);
                    break;
                // 인식이 정상적으로 종료되었음 (thread내에서 exception포함)
                case 5:
                    if (user_speaking != null)user_speaking.setText(StringEscapeUtils.unescapeJava(result));
                    btn_speak.setSelected(false);
                    btn_speak.setEnabled(true);
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

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_speaker_input_page, container, false);
        btn_speak = view.findViewById(R.id.btn_speak);
        btn_clear = view.findViewById(R.id.btn_check);
        user_speaking = view.findViewById(R.id.tv_user_speaking);
        access_key = view.findViewById(R.id.editText_access);


        //floating button
        final FloatingActionButton fab = view.findViewById(R.id.fab_menu);
        final FloatingActionButton fab1 = view.findViewById(R.id.fab_home);
        final FloatingActionButton fab2 = view.findViewById(R.id.fab_word);
        //final FloatingActionButton fab3 = view.findViewById(R.id.fab_user);

        final Animation fab_open = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_open);
        final Animation fab_close = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.fab_close);


        SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);

        //액세스 키 검증
        access_key.setText("363703ed-f93e-4ade-9422-cfff23a396fd");

        access_key.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    SharedPreferences settings = getActivity().getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("client-id", v.getText().toString());
                    editor.apply();
                }
                return false;
            }
        });

        //유저가 말한 것을 녹음하는 작업. 응답이 없으면 에러 메세지.
        btn_speak.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if (isRecording) {
                    forceStop = true;
                } else {
                    try {
                        new Thread(new Runnable() {
                            public void run() {
                                SendMessage("Recording...", 1);
                                try {
                                    recordSpeech();     //녹음 처리 함수 호출
                                    SendMessage("Recognizing...", 2);
                                } catch (RuntimeException e) {
                                    SendMessage(e.getMessage(), 3);
                                    return;
                                }

                                Thread threadRecog = new Thread(new Runnable() {
                                    public void run() {
                                        result = sendDataAndGetResult();
                                        //결과 반환 줄
                                        btn_clear.setOnClickListener(new View.OnClickListener() {
                                            public void onClick(View v) {
                                                //result = result.replaceAll("\\u0027", "");
                                                result = result.replaceAll(" \n", "");
                                                sharedViewModel.setData(result);
                                            }
                                        });
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
                        if (user_speaking != null) user_speaking.setText("ERROR: " + t.toString());
                        forceStop = false;
                        isRecording = false;
                    }
                }
            }
        });

        //floating 버튼-----------------------------------------------------------------------------//
        //main 버튼, menu
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabOpen) {
                    fab1.startAnimation(fab_close);
                    fab2.startAnimation(fab_close);
                    fab1.setClickable(false);
                    fab2.setClickable(false);
                    isFabOpen = false;
                } else {
                    fab1.startAnimation(fab_open);
                    fab2.startAnimation(fab_open);
                    fab1.setClickable(true);
                    fab2.setClickable(true);
                    isFabOpen = true;
                }
            }
        });
        //home 버튼. mediamenuActivity로 간다. -----------------------------------------------------//
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabOpen) {
                    fab1.startAnimation(fab_close);
                    fab2.startAnimation(fab_close);
                    fab1.setClickable(false);
                    fab2.setClickable(false);
                    isFabOpen = false;
                } else {
                    fab1.startAnimation(fab_open);
                    fab2.startAnimation(fab_open);
                    fab1.setClickable(true);
                    fab2.setClickable(true);
                    isFabOpen = true;
                }
                Intent intent = new Intent(getActivity(), MediaMenuActivity.class);
                startActivity(intent);
            }
        });
        //진행도 기록버튼----------------------------------------------------------------------------//
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFabOpen) {
                    fab1.startAnimation(fab_close);
                    fab2.startAnimation(fab_close);
                    fab1.setClickable(false);
                    fab2.setClickable(false);
                    isFabOpen = false;
                } else {
                    fab1.startAnimation(fab_open);
                    fab2.startAnimation(fab_open);
                    fab1.setClickable(true);
                    fab2.setClickable(true);
                    isFabOpen = true;
                }
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        //-----------------------------------------------------------------------------------------//
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class); //fragment_listen으로 값을 전달하기 위한 것.
        /*
        final TextView clearText = getView().findViewById(R.id.tv_user_speaking);
        sharedViewModel.getData().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s){
                clearText.setText(s);
        }
        });

         */
    }




    //읽은 값을 스트링으로 반환.
    public static String readStream(InputStream in) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader r = new BufferedReader(new InputStreamReader(in),1000);
        for (String line = r.readLine(); line != null; line =r.readLine()){
            sb.append(line);
        }
        in.close();
        return sb.toString();
    }
    //녹음의 길이. 예외처리.
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

    //결과 반환 함수
    public String sendDataAndGetResult () {
        String openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Recognition";
        String accessKey = access_key.getText().toString().trim();
        String languageCode = "english";
        String audioContents;

        Gson gson = new Gson();

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech*2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        argument.put("audio", audioContents);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        int responseCode;
        String responBody;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            //URLEncoder.encode("'", "UTF8");     //과연 특수문자를 인식할 것인가. json //u0027
            wr.write(gson.toJson(request).getBytes("UTF_8"));
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            if ( responseCode == 200 ) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                responBody = readStream(is);
                return subString(responBody);
            }
            else
                return "ERROR: " + Integer.toString(responseCode);
        }
        catch (Throwable t) {
            return "ERROR: " + t.toString();
        }
    }
    //결과 자르기 함수
    public String subString(String str) {
        int index = str.indexOf("recognized");
        int startIndex = index + 15;
        int endIndex = str.indexOf("}", startIndex) - 4;

        return str.substring(startIndex, endIndex);
    }

}