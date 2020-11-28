package com.example.taler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.taler.Profile.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MediaActivity extends AppCompatActivity {

    ASRmasterAPI asr;
    VideoView videoView;
    TextView textTitle, textNumber, textScript, textResult, textTemp;
    ImageButton prevButton, nextButton, playButton, recordButton, checkButton;

    enum fileKind { MP4, TXT_KOR, TXT_ENG }
    final String root = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/";
    final String[] directoryName = { "Modern.Family.S01E01.mp4", "Modern.Family.S01E01.kor", "Modern.Family.S01E01.eng" };
    int fileNum = 1;
    int hintNum = 0;

    //RDB
    FirebaseDatabase mDatabase;
    DatabaseReference mUserRef, mDatabaseRef;
    FirebaseAuth mAuth;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        videoView = findViewById(R.id.videoView);
        textTitle = findViewById(R.id.textView_title);
        textNumber = findViewById(R.id.textView_number);
        prevButton = findViewById(R.id.imageButton_prev);
        nextButton = findViewById(R.id.imageButton_next);
        playButton = findViewById(R.id.imageButton_play);
        recordButton = findViewById(R.id.imageButton_record);
        checkButton = findViewById(R.id.imageButton_check);
        textScript = findViewById(R.id.textView_script);
        textResult = findViewById(R.id.textView_result);
        textTemp = findViewById(R.id.textView_temp);

        //RDB
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        final FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        mUserRef = mDatabase.getReference("/users/" + currentUser.getUid());

        // 음성인식 API 연결
        asr = new ASRmasterAPI(recordButton, textResult, 1);

        // init setting
        textTitle.setText("Modern Family");
        textNumber.setText("" + fileNum);
        textScript.setText("Hint!!");
        setTextFromUrl(textTemp, getFileUri(fileKind.TXT_ENG, fileNum));
        videoView.setVideoURI(Uri.parse(getFileUri(fileKind.MP4, fileNum)));
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {

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
                if (fileNum < 10) {
                    fileNum++;
                    setView();
                }
            }
        });

        textScript.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (hintNum == 0) {
                    setTextFromUrl(textScript, getFileUri(fileKind.TXT_KOR, fileNum));
                    hintNum = 1;
                } else if (hintNum == 1) {
                    setTextFromUrl(textScript, getFileUri(fileKind.TXT_ENG, fileNum));
                    hintNum = 2;
                } else {
                    textScript.setText("Hint!!");
                    hintNum = 0;
                }
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean check = asr.resultCheck(textTemp);

                if (check) {
                    checkProgress(fileNum);

                    Toast.makeText(getApplicationContext(), "정답입니다!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "다시 시도해보세요..", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void setView() {
        videoView.setVideoURI(Uri.parse(getFileUri(fileKind.MP4, fileNum)));
        playButton.setSelected(false);
        textNumber.setText("" + fileNum);
        textScript.setText("Hint!!");
        textResult.setText("");
        setTextFromUrl(textTemp, getFileUri(fileKind.TXT_ENG, fileNum));
        hintNum = 0;
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

    private void checkProgress(final int num){
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int nNum = num-1;
                User user = snapshot.getValue(User.class);
                int point = user.counter_video;
                ArrayList<Boolean> endList = user.video_progress;
                if(!endList.get(nNum)){
                    mUserRef.child("video_progress").child(nNum + "").setValue(true);
                    mUserRef.child("counter_video").setValue(point+1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}