package com.example.taler.Story;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.taler.ASRmasterAPI;
import com.example.taler.R;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//Todo 넘어온 PK로 이미지, 오디오, 선택지를 변경시킨다. 또 선택지에 따라 이어서 변경하도록 한다. 같은 타이틀, 다음카드로. 음성인식/평가로 넘어간다
public class StoryCardActivity extends AppCompatActivity {
    int CHECK_NUM = 0; // 스위치 변경확인
    ASRmasterAPI asr;
    ImageView cardImg;
    ImageButton toggleButton;
    ImageButton speechButton;
    TextView script, choice1, choice2, recorded;
    String front_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/StoryCardDir%2F";
    String title, choice1Str, choice2Str, recordedStr= "";
    Integer depth = 2;
    Integer num = 1;
    boolean leafNode = false;
    boolean toggle_script = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);
        cardImg = findViewById(R.id.card_image);
        script = findViewById(R.id.script);
        choice1 = findViewById(R.id.choice1);
        choice2 = findViewById(R.id.choice2);
//        toggleButton = findViewById(R.id.toggle_button_play);
        speechButton = findViewById(R.id.speech_button);
        recorded = findViewById(R.id.recorded_text);
        asr = new ASRmasterAPI(speechButton, recorded, 1);
        recordedStr= asr.getResult();

        Bundle extras = getIntent().getExtras();

        title = extras.getString("title");

        //leafnode 판단을 하고 아니라면 정상작동을하고 맞다면 false로 바꾸고 마지막 엔딩 동작을 한다.
        leafNode = false;
        if(leafNode){
            choice1.setEnabled(false);
            choice2.setEnabled(false);
        }

        showCard(num);
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo 앞에 숫자도 붙이자
                choice1Str = " " + choice1.getText().toString()+ " \n";
                if(num < 8 && choice1Str.equals(recorded.getText().toString())) {
                    num = num*2;
                    if(num > 3){
                        leafNode = true;
                    }
                    showCard(num);
                }
//                else Toast.makeText(StoryCardActivity.this, "Story End", Toast.LENGTH_SHORT).show();
            }
        });

        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choice2Str = " " + choice2.getText().toString()+ " \n";

                if(num < 8 && choice2Str.equals(recorded.getText().toString())) {
                    num = num*2 + 1;
                    if(num > 3){
                        leafNode = true;
                    }
                    showCard(num);
                }
//                else Toast.makeText(StoryCardActivity.this, "Story End", Toast.LENGTH_SHORT).show();
            }
        });

        // 스크립트 visibility Button
        if(toggle_script){
            script.setVisibility(View.INVISIBLE);
        }
        else{
            script.setVisibility(View.GONE);
        }



//        //Button Event: play and replay
//        toggleButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (CHECK_NUM == 0) {
//                    toggleButton.setSelected(true);
//                    CHECK_NUM = 1;
//                } else {
//                    toggleButton.setSelected(false);
//                    CHECK_NUM = 0;
//                }
//            }
//        });

        cardImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!toggle_script){
                    // 이미지 뷰 불투명, 텍스트 보이기
                    toggle_script = true;
                }
                else{
                    toggle_script = false;
                }
            }
        });


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
                //since we are in background thread, to post results we have to go back to ui thread. do the following for that
                runOnUiThread(new Runnable() {
                    public void run() {
                        text.setText(urls.get(0)); // My TextFile has 3 lines
                    }
                });
            }
        }).start();
    }

    public void showCard(int num){
        Picasso.get().load(front_url + title + "%2F" + "image" + "%2F"+ num + ".JPG?alt=media&token=" + num).fit().into(cardImg);
        setTextFromUrl(script, front_url + title + "%2F" + "script" + "%2F"+ num + ".txt?alt=media&token=" + num);
        if(num <= 3) {
            setTextFromUrl(choice1, front_url + title + "%2F" + "choice1" + "%2F"+ num + ".txt?alt=media&token=" + num);
            setTextFromUrl(choice2, front_url + title + "%2F" + "choice2" + "%2F"+ num + ".txt?alt=media&token=" + num);
        }
        else {
            choice1.setText("  ");
            choice2.setText("  ");
        }
        recorded.setText("");
        toggle_script = false;

    }
//    https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/StoryCardDir%2FAlice%2Fchoice1%2F1.txt?alt=media&token=1
}