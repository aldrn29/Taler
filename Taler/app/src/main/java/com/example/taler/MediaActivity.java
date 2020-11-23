package com.example.taler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import static com.example.taler.fileKind.MP4;
import static com.example.taler.fileKind.TXT_ENG;
import static com.example.taler.fileKind.TXT_KOR;

enum fileKind { MP4, TXT_KOR, TXT_ENG }

public class MediaActivity extends AppCompatActivity {

    ASRmasterAPI asr;
    VideoView videoView;
    TextView textNumber, textScript, textResult;
    ImageButton prevButton, nextButton, playButton, recordButton;
    Button engButton, korButton;

    final String root = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/";
    final String[] directoryName = { "Modern.Family.S01E01.mp4", "Modern.Family.S01E01.kor", "Modern.Family.S01E01.eng" };
    int fileNum = 1;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        videoView = findViewById(R.id.videoView);
        textNumber = findViewById(R.id.textView_number);
        prevButton = findViewById(R.id.imageButton_prev);
        nextButton = findViewById(R.id.imageButton_next);
        playButton = findViewById(R.id.imageButton_play);
        recordButton = findViewById(R.id.imageButton_record);
        textScript = findViewById(R.id.textView_script);
        textResult = findViewById(R.id.textView_result);
        engButton = findViewById(R.id.button_eng);
        korButton = findViewById(R.id.button_kor);

        // 음성인식 API 연결
        asr = new ASRmasterAPI(recordButton, textResult, 1);

        // init setting
        textNumber.setText("#." + fileNum);
        videoView.setVideoURI(Uri.parse(getFileUri(MP4, fileNum)));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // To do: 준비 완료되면 비디오 첫 화면 띄우기
            }
        });
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                playButton.setSelected(false);
            }
        });

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // button image change & video play
                if (!view.isSelected()) {
                    view.setSelected(true);
                    videoView.start();
                } else {
                    view.setSelected(false);
                    videoView.pause();
                }
            }
        });

        prevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fileNum != 1) {
                    fileNum--;
                    setView();
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // To do: 예외처리 수정
                if (fileNum < 3) {
                    fileNum++;
                    setView();
                }
            }
        });

        engButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextFromUrl(textScript, getFileUri(TXT_ENG, fileNum));
            }
        });

        korButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setTextFromUrl(textScript, getFileUri(TXT_KOR, fileNum));
            }
        });
    }

    public void setView() {
        videoView.setVideoURI(Uri.parse(getFileUri(MP4, fileNum))); // To do: 첫화면 띄우기
        playButton.setSelected(false);
        textNumber.setText("#." + fileNum);
        textScript.setText("");
        textResult.setText("");
    }

    public String getFileUri(@NotNull fileKind kind, int num) {
        String fileUri = "";

        switch (kind)
        {
            case MP4: fileUri = root + directoryName[kind.ordinal()] + "%2F" + num + ".mp4?alt=media&token=" + num; break;
            case TXT_KOR:
            case TXT_ENG: {
                fileUri = root + directoryName[kind.ordinal()] + "%2F" + num + ".txt?alt=media&token=" + num;
                break;
            }
            default: break;
        }
        return fileUri;
    }

    public void setTextFromUrl(final TextView text, final String url_input) {
        new Thread(new Runnable() {
            public void run() {
                final ArrayList<String> urls = new ArrayList<String>(); //to read each line
                try {
                    URL url = new URL(url_input); //My text file location
                    //First open the connection
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(60000); // timing out in a minute

                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String str;
                    while ((str = in.readLine()) != null) {
                        urls.add(str);
                    }
                    in.close();
                } catch (Exception e) {
                    Log.d("MyTag", e.toString());
                }

                runOnUiThread(new Runnable() {
                    public void run() {
                        text.setText(urls.get(0));
                    }
                });
            }
        }).start();
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