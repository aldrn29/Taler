package com.example.taler;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MediaActivity extends AppCompatActivity {

    MediaPlayer player;
    TextView text;

    ASRmasterAPI asr;
    VideoView videoView;
    ImageButton playButton;
    ImageButton recordButton;   // 녹음 start, end
    TextView textResult;        // 녹음 결과
    int mode;

    ArrayList<String> files = new ArrayList<>();
    int fileNum = 0;

    final String root = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/";
    final String[] directoryName = {"Modern.Family.S01E01.mp4","Modern.Family.S01E01.kor","Modern.Family.S01E01.eng"};
    FirebaseStorage storage = FirebaseStorage.getInstance("gs://taler-db.appspot.com");
    StorageReference storageRef = storage.getReference();
    StorageReference pathReference = storageRef.child("Modern.Family.S01E01.mp4/Modern.Family.01."+ fileNum +".mp4");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        String temp = root + directoryName[0] + "%2F";

        videoView = findViewById(R.id.videoView);
        playButton = findViewById(R.id.imageButton_play);
        recordButton = findViewById(R.id.imageButton_record);
        textResult = findViewById(R.id.textView_result);

        // 음성인식 API 연결
        asr = new ASRmasterAPI(recordButton, textResult, 1);

        setFiles();
        videoView.setVideoURI(Uri.parse(files.get(fileNum)));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                // To do: 준비 완료되면 비디오 첫 화면 띄우기
            }
        });

        // Button Event: play
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // button image change
                if (!view.isSelected()) {
                    view.setSelected(true);
                    videoView.start();

                    // txt
                    text = findViewById(R.id.textView_script);
                    String s = "";//"https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/Modern.Family.S01E01.eng%2FModern.Family.01.1.txt?alt=media&token=1275cdc7-f1eb-4934-b94b-8ff4ca42cf6a"
                    //text.setText(s);

                } else {
                    view.setSelected(false);
                    videoView.pause();
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

    public void setFiles() {

        files.add("https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/Modern.Family.S01E01.mp4%2FModern.Family.01.1.mp4?alt=media&token=ca7d2326-dd4f-4452-98fa-2aee3811c84a");
        files.add("https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/Modern.Family.S01E01.mp4%2FModern.Family.01.2.mp4?alt=media&token=851f5403-f109-4e10-ad3d-316bc46af344");
        files.add("https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/Modern.Family.S01E01.mp4%2FModern.Family.01.3.mp4?alt=media&token=56e6d425-ea54-4b12-ae11-442ed62244f9");
    }
}