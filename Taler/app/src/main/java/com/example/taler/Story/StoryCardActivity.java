package com.example.taler.Story;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    Integer num = 1;


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
        recordedStr= asr.result;


        Bundle extras = getIntent().getExtras();

        title = extras.getString("title");


        //Todo FB를 여기서 읽어온다. title id에 해당하는 사진들을 1번부터 차례로
        //우선 선지는 "number one", "number two"로 통일하자. 테스트를 위해
        showCard(num);
        choice1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("start");
                System.out.print("recordedText:");
                System.out.print(recorded.getText().toString());
                System.out.println("end");
                System.out.print("start");
                System.out.print("choiceText:");
                System.out.print(choice1.getText().toString());
                System.out.println("end");
                choice1Str = " " + choice1.getText().toString()+ " \n";
                System.out.println(choice1Str);
                if(num < 8 && choice1Str.equals(recorded.getText().toString())) {
                    num = num*2;
                    showCard(num);
                }
//                else Toast.makeText(StoryCardActivity.this, "Story End", Toast.LENGTH_SHORT).show();
            }
        });
        choice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.print("start");
                System.out.print("recordedText:");
                System.out.print(recorded.getText().toString());
                System.out.println("end");
                System.out.print("start");
                System.out.print("choiceText:");
                System.out.print(choice2.getText().toString());
                System.out.println("end");
                choice2Str = " " + choice2.getText().toString()+ " \n";
                System.out.println(choice2Str);
                if(num < 8 && choice2Str.equals(recorded.getText().toString())) {
                    num = num*2 + 1;
                    showCard(num);
                }
//                else Toast.makeText(StoryCardActivity.this, "Story End", Toast.LENGTH_SHORT).show();
            }
        });



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
        Picasso.get().load(front_url + title + "%2F" + "image" + "%2F"+ num + ".jpeg?alt=media&token=" + num).fit().into(cardImg);
        setTextFromUrl(script, front_url + title + "%2F" + "script" + "%2F"+ num + ".txt?alt=media&token=" + num);
        setTextFromUrl(choice1, front_url + title + "%2F" + "choice1" + "%2F"+ num + ".txt?alt=media&token=" + num);
        setTextFromUrl(choice2, front_url + title + "%2F" + "choice2" + "%2F"+ num + ".txt?alt=media&token=" + num);
    }
//    https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/StoryCardDir%2FAlice%2Fchoice1%2F1.txt?alt=media&token=1
}