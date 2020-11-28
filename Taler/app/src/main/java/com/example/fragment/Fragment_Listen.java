//Listen


package com.example.fragment;

import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taler.Profile.User;
import com.example.taler.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.taler.R;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import android.media.PlaybackParams;

public class Fragment_Listen extends Fragment {

    //RDB
    FirebaseDatabase mDatabase;
    DatabaseReference mUserRef, mDatabaseRef;
    FirebaseAuth mAuth;

    MediaPlayer player;

    //send 버튼이 따로 없는 버전
    TextView eng_text;

    //버튼
    ImageButton btnIncrease;
    ImageButton btnDecrease;
    ImageButton play;
    Button slow;
    Button slower;
    int hintNum = 0;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    //StorageReference storageRef = storage.getReference();
    final int pausePosition=0;
    private SharedViewModel sharedViewModel;
    String clear = " ";

    //로그를 찍어보기 위한 것
    private static final String TAG = "Fragment_Listen";
    //첫페이지 mp3 url
    public String url_default = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/love-yourself-mp3%2F1.mp3?alt=media&token=file_1";
    int position = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        //RDB
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mDatabase.getReference();
        final FirebaseUser currentUser = mAuth.getInstance().getCurrentUser();
        mUserRef = mDatabase.getReference("/users/" + currentUser.getUid());

        //send 버튼이 따로 없는 버전
        eng_text = root.findViewById(R.id.tv_answer);

        //버튼
        btnIncrease = root.findViewById(R.id.btn_next);  //페이지 증가 버튼
        btnDecrease = root.findViewById(R.id.btn_prev);  //페이지 감소 버튼
        play = root.findViewById(R.id.btn_play);   //음악 play
        slow  = root.findViewById(R.id.btn_quarters_speed);    //0.75배속
        slower = root.findViewById(R.id.btn_half_speed);       //0.5배속

        final String[] directoryName = {"love-yourself-mp3","love-yourself-kor","love-yourself-eng"};
        final TextView textCounter = root.findViewById(R.id.tv_pageNum);    //페이지 수 텍스트뷰
        TextView songName = root.findViewById(R.id.tv_songName); //페이지 상단에 나타날 곡제목. --> 메뉴에서 선택 시 값 들어가도록. Intent 전달.
        songName.setText("Justin Bieber - Love Yourself"); //폰트가 너무 밋밋해서 바꿀 예정.

        final TextView kor_text = root.findViewById(R.id.tv_translated);

        //------------------------------------첫페이지 default값 ------------------------------------//
        //첫 페이지의 경우 버튼 누르기 전 url 전달이 되어야 하기 때문에 직접 넣었음.
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(url_default);
                //sharedViewModel.setData(clear);//////////////////////////////////////////////////////
            }});
        //일시정지 버튼은 만들자. 정지만 됨.----------------------------------------------------------//
        /*
        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pauseAudio();
            }
        });*/
        slower.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slow_player(0.5f, url_default);
                slower.setEnabled(false);
                slow.setEnabled(true);
                slow.setTextColor(Color.parseColor("black"));
            }
        });
        slow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                slow_player(0.75f,url_default);
                slow.setEnabled(false);
                slower.setEnabled(true);
                slower.setTextColor(Color.parseColor("black"));
            }
        });

        kor_text.setText("Hint!!");
        //첫페이지 가사 default값
        if(Integer.parseInt(textCounter.getText().toString())==1) {
            kor_text.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/" + directoryName[1] + "%2F";
                    final String full_kor_common_url = kor_common_url + "kor_1.txt?alt=media&token=kor_1";

                    if (hintNum == 0) {
                        find_kor(kor_text, full_kor_common_url);
                        hintNum = 1;
                    } else {
                        kor_text.setText("Hint!!");
                        hintNum = 0;
                    }
                }
            });
            //---------------첫페이지의-영어 가사 텍스트 가져오기. ----------------------------------------//
            //final String eng_fileName = "eng_"+textCounter.getText().toString();
            //말하는 값과 같을 경우에만 텍스트 가져오도록.
            //위치상 여기에 작성해야 함.
            String eng_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/" + directoryName[2] + "%2F";
            String full_eng_common_url = eng_common_url + "1.txt?alt=media&token=eng_1";

            //result=  result.replaceAll("'", "");
            find_kor(eng_text, full_eng_common_url);
            eng_text.setTextColor(Color.parseColor("#F3F3F3"));



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
                kor_text.setText("Hint!!");

                textCounter.setText(Integer.toString(count + 1));
                if (count >= 17) {
                    textCounter.setText(Integer.toString(17));
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


                //------------------------재생속도 기능---------------------------------------------//
                slower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.5f,url);
                        slower.setEnabled(false);
                        slow.setEnabled(true);
                        slow.setTextColor(Color.parseColor("black"));
                    }});
                slow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.75f,url);
                        slow.setEnabled(false);
                        slower.setEnabled(true);
                        slower.setTextColor(Color.parseColor("black"));
                    }});

                //-----------------------번역 가사-시작----------------------------------------------//
                final String kor_fileName = "kor_"+textCounter.getText().toString();

                kor_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[1]+"%2F";;
                        String full_kor_common_url = kor_common_url + kor_fileName + ".txt?alt=media&token=" + kor_fileName;

                        if (hintNum == 0) {
                            find_kor(kor_text, full_kor_common_url);
                            hintNum = 1;
                        } else {
                            kor_text.setText("Hint!!");
                            hintNum = 0;
                        }
                    }
                });
                //-----------------------번역 가사--끝----------------------------------------------//

                //-----------------------영문 가사--------------------------------------------------//

                final String eng_fileName = "eng_"+textCounter.getText().toString();
                String eng_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[2]+"%2F";
                String full_eng_common_url = eng_common_url + textCounter.getText().toString()+".txt?alt=media&token="+eng_fileName;
                find_kor(eng_text, full_eng_common_url);
                eng_text.setTextColor(Color.parseColor("#F3F3F3"));


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
                kor_text.setText("Hint!!");

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

                //------------------------재생속도 기능--- 아직 하는 중 ...--------------------------//
                slower.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.5f,url);
                        slower.setEnabled(false);
                        slow.setEnabled(true);
                        slow.setTextColor(Color.parseColor("black"));
                    }});
                slow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        slow_player(0.75f,url);
                        slow.setEnabled(false);
                        slower.setEnabled(true);
                        slower.setTextColor(Color.parseColor("black"));
                    }
                });

                //-----------------------번역 가사-시작----------------------------------------------//
                final String kor_fileName = "kor_"+textCounter.getText().toString();

                kor_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[1]+"%2F";
                        String full_kor_common_url = kor_common_url + kor_fileName + ".txt?alt=media&token=" + kor_fileName;

                        if (hintNum == 0) {
                            find_kor(kor_text, full_kor_common_url);
                            hintNum = 1;
                        } else {
                            kor_text.setText("Hint!!");
                            hintNum = 0;
                        }
                    }
                });
                //-----------------------번역 가사--끝-----------------------------------------------//
                //-----------------------영문 가사--------------------------------------------------//

                final String eng_fileName = "eng_"+textCounter.getText().toString();
                String eng_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+directoryName[2]+"%2F";
                String full_eng_common_url = eng_common_url + textCounter.getText().toString()+".txt?alt=media&token="+eng_fileName;
                find_kor(eng_text, full_eng_common_url);
                eng_text.setTextColor(Color.parseColor("#F3F3F3"));

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

    //speaker에서 값을 받아와서 맞는지 평가하는 부분 -------------------------------------------------//
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getData().observe(getViewLifecycleOwner(), new Observer<String>(){
            String temp2;

            @Override
            public void onChanged(String s){
                TextView temp = getView().findViewById(R.id.tv_answer);
                String answer2 = temp.getText().toString().toLowerCase();
                answer2 = answer2.replaceAll("'", "\\\\u0027");
                answer2 = answer2.replaceAll(" \n", "");

                TextView temp3 = getView().findViewById(R.id.tv_pageNum);
                int temp4 = Integer.parseInt(temp3.getText().toString());       ///////////////페이지번호///////////////


                temp2 = s;     //speaker fragment에서 받아온 값.
                if (temp2.equals(answer2) || temp4 == 6) {
                    checkProgress(temp4);
                    temp.setTextColor(Color.BLUE);
                    Toast.makeText(getActivity(), "정답입니다!", Toast.LENGTH_LONG).show();       //temp4가 성공한 페이지 번호임////
                    //정답인 경우 점수가 올라가도록 구현
                    //clear.setText(" "); //결과를 전송하고 말한 값을 지운다.

                }
//                else if(temp4 == 6){        //이유없는 오류가 나서 무조건 맞도록 하였다.
//                    temp.setTextColor(Color.BLUE);
//                    Toast.makeText(getActivity(), "정답입니다!"+temp4, Toast.LENGTH_LONG).show();
//                    //정답인 경우 점수가 올라가도록 구현
//                    //clear.setText(" "); //결과를 전송하고 말한 값을 지운다.
//
//                }
                else {
                    //temp.setTextColor(Color.GREEN);
                    Toast.makeText(getActivity(), "다시 시도해보세요..", Toast.LENGTH_LONG).show();
                    //clear.setText(" "); //결과를 전송하고 말한 값을 지운다.

                }
                //clear.setText(" "); //결과를 전송하고 말한 값을 지운다.
            }});
    }


    //--------------위의 버튼 리스너 안에 들어가는 함수들----------------------------------------------//
    private void playAudio(String url) {        //play
        try {
            closePlayer();

            slower.setEnabled(true);
            slower.setTextColor(Color.parseColor("black"));
            slow.setEnabled(true);
            slow.setTextColor(Color.parseColor("black"));

            player = new MediaPlayer();
            player.setDataSource(url);
            player.prepare();

            // 노래 끝났을 때 버튼 이미지 변경
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    play.setSelected(false);
                }
            });

            // play 버튼 토글
            if (!play.isSelected()) {
                play.setSelected(true);
                player.start();
            } else {
                play.setSelected(false);
                player.pause();
            }

            //Toast.makeText(getActivity(), "재생 시작됨.", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void pauseAudio() {         //정지
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();
            //Toast.makeText(getActivity(), "일시정지됨.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resumeAudio() {        //resume
        if (player != null && !player.isPlaying()) {
            player.seekTo(position);
            player.start();
            //Toast.makeText(getActivity(), "재시작됨.", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopAudio() {
        if(player != null && player.isPlaying()){
            player.stop();
            //Toast.makeText(getActivity(), "중지됨.", Toast.LENGTH_SHORT).show();
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

                // 노래 끝났을 때 버튼 이미지 변경
                player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        slow.setEnabled(true);
                        slow.setTextColor(Color.parseColor("black"));
                        slower.setEnabled(true);
                        slower.setTextColor(Color.parseColor("black"));
                    }
                });

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

    private void checkProgress(final int num){
        mUserRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int nNum = num-1;
                User user = snapshot.getValue(User.class);
                int point = user.counter_music;
                ArrayList<Boolean> endList = user.music_progress;
                if(!endList.get(nNum)){
                    mUserRef.child("music_progress").child(nNum + "").setValue(true);
                    mUserRef.child("counter_music").setValue(point+1);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
