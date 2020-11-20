//Listen


package com.example.fragment;

import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.taler.R;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.media.PlaybackParams;

import org.w3c.dom.Text;

public class Fragment_Listen extends Fragment {

    MediaPlayer player;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    //StorageReference storageRef = storage.getReference();
    final int pausePosition=0;

    //로그를 찍어보기 위한 것
    private static final String TAG = "Fragment_Listen";
    //첫페이지 mp3 url
    public String url_default = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/love-yourself-mp3%2F1.mp3?alt=media&token=file_1";
    int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        //Fragment_Speaker로 값 전달
        /*
        getParentFragmentManager().setFragmentResultListener("key", this, new FragmentResultListener() {
            @Override
            public void onFragmentResult(@NonNull String key, @NonNull Bundle bundle) {
                // We use a String here, but any type that can be put in a Bundle is supported
                String result = bundle.getString("bundleKey");
                // Do something with the result...
            }
        });

         */
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        //버튼
        Button btnIncrease = root.findViewById(R.id.btn_next);  //페이지 증가 버튼
        Button btnDecrease = root.findViewById(R.id.btn_prev);  //페이지 감소 버튼
        final Button play = root.findViewById(R.id.btn_play);   //음악 play
        final Button slow  = root.findViewById(R.id.btn_quarters_speed);    //0.75배속
        final Button slower = root.findViewById(R.id.btn_half_speed);       //0.5배속
        final Button kor_button = root.findViewById(R.id.btn_watch_kor);    //번역 가사 버튼
        final Button pause = root.findViewById(R.id.btn_delay);        //일시정지 버튼
        final Button answer = root.findViewById(R.id.btn_answer);       //정답보기 버튼

        final String[] directoryName = {"love-yourself-mp3","love-yourself-kor","love-yourself-eng"};
        final TextView textCounter = root.findViewById(R.id.tv_pageNum);    //페이지 수 텍스트뷰
        TextView songName = root.findViewById(R.id.tv_songName); //페이지 상단에 나타날 곡제목. --> 메뉴에서 선택 시 값 들어가도록. Intent 전달.

        final TextView kor_text = root.findViewById(R.id.tv_translated);
        final TextView eng_text = root.findViewById(R.id.tv_answer);

        final String sendData = eng_text.getText().toString().toLowerCase(); //현재 영문 가사를 소문자로 다 바꾼다.

        //------------------------------------첫페이지 default값 ------------------------------------//
        //첫 페이지의 경우 버튼 누르기 전 url 전달이 되어야 하기 때문에 직접 넣었음.
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(url_default);
            }});
        //일시정지 버튼은 만들자. 정지만 됨.----------------------------------------------------------//
        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pauseAudio();
            }
        });
        slower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slow_player(0.5f, url_default);
                Toast.makeText(getActivity(), "0.5배속", Toast.LENGTH_SHORT).show();
            }});
        slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slow_player(0.75f,url_default);
                Toast.makeText(getActivity(), "0.75배속", Toast.LENGTH_SHORT).show();
            }});

        //첫페이지 가사 default값
        if(Integer.parseInt(textCounter.getText().toString())==1) {
            kor_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    final String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/" + directoryName[1] + "%2F";
                    final String full_kor_common_url = kor_common_url + "kor_1.txt?alt=media&token=kor_1";
                    find_kor(kor_text, full_kor_common_url);
                    Toast.makeText(getActivity(), "번역 가사", Toast.LENGTH_LONG).show();
                }
            });
            //---------------첫페이지의-영어 가사 텍스트 가져오기. ----------------------------------------//
            //final String eng_fileName = "eng_"+textCounter.getText().toString();
            //버튼. 말하는 값과 같을 경우에만 텍스트 가져오도록.
            answer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String eng_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/" + directoryName[2] + "%2F";
                    String full_eng_common_url = eng_common_url + "1.txt?alt=media&token=eng_1";
                    find_kor(eng_text, full_eng_common_url);
                    Toast.makeText(getActivity(), "answer", Toast.LENGTH_LONG).show();
                }
            });

        }
        //-----------------------------------------------------------------------------------------//
        //현재 페이지의 영어 가사 값을 가지고 있어야 한다. 그러기 위해서는 페이지의 값을 읽어야 하니.. listener안에 위치해야 한다.
        //페이지를 넘길 때마다 영어 가사가 있지만 실제로는 보이지 않는다. 흰색 글씨.
        //버튼 리스너 말고 스피커에서 사용자가 말한 값을 가져오는 것이 우선이다.
        //값을 전달받으면, 화면의 영문 가사를 소문자로 바꾼뒤 둘의 일치여부를 확인한다. eng_text = eng_text.toLowerCase();
        //일치한다면 맞다는 토스트 메시지가 뜬다.
        //가능하다면 화면에 점수도 넣기. text뷰로. 아님 floating 버튼 .


        //-------------------------페이지 수 증가-> 페이지번호가 url에 반영 -> 해당 번째 구절 재생------//
        btnIncrease.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                int count = Integer.parseInt(textCounter.getText().toString());

                //이전 페이지 가사 지우기
                eng_text.setText("");
                kor_text.setText("");

                textCounter.setText(Integer.toString(count + 1));
                if (count >= 16) {
                    textCounter.setText(Integer.toString(16));
                    Toast.makeText(getActivity(), "마지막 페이지 입니다", Toast.LENGTH_LONG).show();
                }
                final String t1 = textCounter.getText().toString();
                String fileName = t1+".mp3";

                String common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+ directoryName[0] +"%2F";
                final String url = common_url + fileName+"?alt=media&token=file_"+t1;

                play.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v) {
                        playAudio(url);
                    }
                });

                //일시정지 버튼은 만들자. 아직 작동 안됨(정지된 뒤 바로 이어서 재생함)--------------------//
                Button pause = getView().findViewById(R.id.btn_delay);
                pause.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        pauseAudio();
                        if (player != null && !player.isPlaying()){
                            resumeAudio();
                        }
                    }
                });

                //------------------------재생속도 기능---------------------------------------------//
                slower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.5f,url);
                        Toast.makeText(getActivity(), "0.5배속", Toast.LENGTH_SHORT).show();
                    }});
                slow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.75f,url);
                        Toast.makeText(getActivity(), "0.75배속", Toast.LENGTH_SHORT).show();
                    }});

                //-----------------------번역 가사-시작----------------------------------------------//
                final String kor_fileName = "kor_"+textCounter.getText().toString();

                kor_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[1]+"%2F";;
                        String full_kor_common_url = kor_common_url + kor_fileName + ".txt?alt=media&token=" + kor_fileName;

                        find_kor(kor_text, full_kor_common_url);
                        Toast.makeText(getActivity(), "번역 가사", Toast.LENGTH_LONG).show();
                    }
                });
                //-----------------------번역 가사--끝----------------------------------------------//
                //-----------------------영문 가사--------------------------------------------------//

                //버튼 사용시
                answer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String eng_fileName = "eng_"+textCounter.getText().toString();
                        String eng_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[2]+"%2F";
                        String full_eng_common_url = eng_common_url + textCounter.getText().toString()+".txt?alt=media&token="+eng_fileName;

                        find_kor(eng_text, full_eng_common_url);
                        Toast.makeText(getActivity(), "answer", Toast.LENGTH_LONG).show();
                    }
                });
                //---------------------영문 가사--끝-------------------------------------------------//
            }
        });
        //-------------------------페이지 수 감소-> 페이지번호가 url에 반영 -> 해당 번째 구절 재생-----//
        btnDecrease.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                int count = Integer.parseInt(textCounter.getText().toString());

                //이전 페이지 가사 지우기----
                eng_text.setText("");
                kor_text.setText("");

                textCounter.setText(Integer.toString(count - 1));
                if (count <= 1) {
                    textCounter.setText(Integer.toString(1));
                    Toast.makeText(getActivity(), "첫 페이지 입니다", Toast.LENGTH_LONG).show();
                }
                final String t1 = textCounter.getText().toString();
                String fileName = t1+".mp3";
                final String common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+ directoryName[0] +"%2F";
                final String url = common_url + fileName+"?alt=media&token=file_"+t1;
                play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playAudio(url);
                    }});

                //일시정지 버튼은 만들자. 아직 작동 안됨.----------------------------------------------//
                Button pause = getView().findViewById(R.id.btn_delay);
                pause.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        pauseAudio();
                        if (player != null && !player.isPlaying()){
                            resumeAudio();
                        }
                    }
                });
                //------------------------재생속도 기능--- 아직 하는 중 ...--------------------------//
                slower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.5f,url);
                    }});
                slow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.75f,url);
                    }
                });

                //-----------------------번역 가사-시작----------------------------------------------//
                final String kor_fileName = "kor_"+textCounter.getText().toString();

                kor_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[1]+"%2F";
                        String full_kor_common_url = kor_common_url + kor_fileName + ".txt?alt=media&token=" + kor_fileName;

                        find_kor(kor_text, full_kor_common_url);
                        Toast.makeText(getActivity(), "번역 가사", Toast.LENGTH_LONG).show();
                    }
                });
                //-----------------------번역 가사--끝-----------------------------------------------//
                //-----------------------영문 가사--------------------------------------------------//
                            //버튼 사용시
                answer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String eng_fileName = "eng_"+textCounter.getText().toString();
                        String eng_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[2]+"%2F";
                        String full_eng_common_url = eng_common_url + textCounter.getText().toString()+".txt?alt=media&token="+eng_fileName;

                        find_kor(eng_text, full_eng_common_url);
                        Toast.makeText(getActivity(), "answer", Toast.LENGTH_LONG).show();
                    }
                });
                //---------------------영문 가사--끝-------------------------------------------------//

                if (count == 1) {
                    textCounter.setText(Integer.toString(1));
                    Toast.makeText(getActivity(), "첫 페이지 입니다", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Inflate the layout for this fragment
        return root;
    }

    //--------------위의 버튼 리스너 안에 들어가는 함수들----------------------------------------------//
    private void playAudio(String url) {        //play
        try {
            closePlayer();

            player = new MediaPlayer();
            player.setDataSource(url);
            player.prepare();
            player.start();

            Toast.makeText(getActivity(), "재생 시작됨.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {         //정지
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();
            Toast.makeText(getActivity(), "일시정지됨.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resumeAudio() {        //resume
        if (player != null && !player.isPlaying()) {
            player.seekTo(position);
            player.start();
            Toast.makeText(getActivity(), "재시작됨.", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if(player != null && player.isPlaying()){
            player.stop();
            Toast.makeText(getActivity(), "중지됨.", Toast.LENGTH_SHORT).show();
        }
    }
    public void closePlayer() {
        if (player != null) {
            player.release();
            player = null;
        }
    }
    public void slow_player(float speed, String url){   //배속
        if (player != null) {
            try {
                closePlayer();
                player = new MediaPlayer();
                player.setDataSource(url);
                PlaybackParams playbackParams = new PlaybackParams();   //재생속도--> API 23 이상,setPlaybackParams 메소드 사용 가능.
                playbackParams.setSpeed(speed);
                player.setPlaybackParams(playbackParams);
                player.prepare();
                player.start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void find_kor(final TextView text, final String url_input){      //가사찾기, 영문도 같이 사용
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
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        text.setText(urls.get(0)); // My TextFile has 3 lines
                    }
                });
            }
        }).start();
    }
}
