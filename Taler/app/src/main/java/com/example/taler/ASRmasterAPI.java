package com.example.taler;

import android.annotation.SuppressLint;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
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
import java.util.HashMap;
import java.util.Map;


/**
 * How to use:
 *
 * 1. ImageButton - 녹음 시작/끝 버튼 (버튼 이미지 토글기능 갖춰야 함 (ex: button_record.xml))
 * 2. TextView
 * 3. ASRmasterAPI asr = new ASRmasterAPI(Button, textView);
 */

public class ASRmasterAPI implements View.OnClickListener {

    String accessKey ="508e34e6-11b3-44c1-a47d-d1a1ef4bb69f";

    ImageButton buttonStart;
    TextView textResult;

    int curMode;
    String result = "";

    int maxLenSpeech = 16000 * 45;
    byte [] speechData = new byte [maxLenSpeech * 2];
    int lenSpeech = 0;
    boolean isRecording = false;
    boolean forceStop = false;

    /**
     *
     * @param mode 0: 한글 인식, 1: 영어 인식, 2: 영어발음평가
     */
    public ASRmasterAPI(View button, View text, int mode) {
        buttonStart = (ImageButton) button;
        textResult = (TextView) text;
        curMode = mode;

        buttonStart.setOnClickListener(this);
    }

    public ASRmasterAPI(View button, int mode) {
        buttonStart = (ImageButton) button;
        curMode = mode;

        buttonStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (isRecording) {
            forceStop = true;
        } else {
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
                if (textResult != null) textResult.setText("ERROR: " + t.toString());
                forceStop = false;
                isRecording = false;
            }
        }
    }

    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public synchronized void handleMessage(Message msg) {
            Bundle bd = msg.getData();
            String v = bd.getString("status");
            switch (msg.what) {
                // 녹음이 시작되었음(버튼)
                case 1:
                    if (textResult != null) textResult.setText(v);
                    buttonStart.setSelected(true);
                    break;
                // 녹음이 정상적으로 종료되었음(버튼 또는 max time)
                case 2:
                    if (textResult != null) textResult.setText(v);
                    buttonStart.setSelected(false);
                    buttonStart.setEnabled(false);
                    break;
                // 녹음이 비정상적으로 종료되었음(마이크 권한 등)
                case 3:
                    if (textResult != null) textResult.setText(v);
                    buttonStart.setSelected(false);
                    break;
                // 인식이 비정상적으로 종료되었음(timeout 등)
                case 4:
                    if (textResult != null) textResult.setText(v);
                    buttonStart.setSelected(false);
                    buttonStart.setEnabled(true);
                    break;
                // 인식이 정상적으로 종료되었음 (thread내에서 exception포함)
                case 5:
                    if (textResult != null) textResult.setText(StringEscapeUtils.unescapeJava(result));
                    buttonStart.setSelected(false);
                    buttonStart.setEnabled(true);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    public void SendMessage(String str, int id) {
        Message msg = handler.obtainMessage();
        Bundle bd = new Bundle();
        bd.putString("status", str);
        msg.what = id;
        msg.setData(bd);
        handler.sendMessage(msg);
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
        String languageCode;
        String audioContents;

        Gson gson = new Gson();

        switch (curMode) {
            case 0: // "한국어인식"
                languageCode = "korean";
                break;
            case 1: // "영어인식"
                languageCode = "english";
                break;
            case 2: // "영어발음평가"
                languageCode = "english";
                openApiURL = "http://aiopen.etri.re.kr:8000/WiseASR/Pronunciation";
                break;
            default:
                return "ERROR: invalid mode";
        }

        Map<String, Object> request = new HashMap<>();
        Map<String, String> argument = new HashMap<>();

        audioContents = Base64.encodeToString(
                speechData, 0, lenSpeech * 2, Base64.NO_WRAP);

        argument.put("language_code", languageCode);
        argument.put("audio", audioContents);

        request.put("access_key", accessKey);
        request.put("argument", argument);

        URL url;
        Integer responseCode;
        String responBody;
        try {
            url = new URL(openApiURL);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestMethod("POST");
            con.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(con.getOutputStream());
            wr.write(gson.toJson(request).getBytes("UTF-8"));
            wr.flush();
            wr.close();

            responseCode = con.getResponseCode();
            if (responseCode == 200) {
                InputStream is = new BufferedInputStream(con.getInputStream());
                responBody = readStream(is);
                return subString(responBody);
            } else
                return "ERROR: " + Integer.toString(responseCode);
        } catch (Throwable t) {
            return "ERROR: " + t.toString();
        }
    }

    public String subString(String str) {
        int index = str.indexOf("recognized");
        int startIndex = index + 15;
        int endIndex = str.indexOf("}", startIndex) - 4;

        return str.substring(startIndex, endIndex);
    }

    public String getResult() {
        return result;
    }

    public boolean resultCheck(View text) {
        TextView str = (TextView) text;
        String script = str.getText().toString().toLowerCase();

        // 특수문자 및 기타처리
        script = script.replace("?", "");
        script = script.replace("!", "");
        script = script.replace(".", "");
        script = script.replace(",", "");
        script = script.replace("~", "");
        result = result.replaceAll("\\\\u0027", "'");
        result = result.replaceAll(" are", "'re");

        Log.d("MyTag", "기존: ["+script+"]");
        Log.d("MyTag", "음성: ["+result+"]");

        if (result.equals(script)) {
            // 정답
            Log.d("MyTag", "정답!");
            return true;
        }
        else {
            // 틀림
            Log.d("MyTag", "틀림!");
            return false;
        }
    }}