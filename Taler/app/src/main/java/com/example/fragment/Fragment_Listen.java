package com.example.fragment;

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
    private int pausePosition=0;

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

        //버튼
        Button btnIncrease = root.findViewById(R.id.btn_next);  //페이지 증가 버튼
        Button btnDecrease = root.findViewById(R.id.btn_prev);  //페이지 감소 버튼
        final Button play = root.findViewById(R.id.btn_play);   //음악 play
        final Button slow  = root.findViewById(R.id.btn_quarters_speed);    //0.75배속
        final Button slower = root.findViewById(R.id.btn_half_speed);       //0.5배속
        final TextView textCounter = root.findViewById(R.id.tv_pageNum);    //페이지 수 텍스트뷰
        final Button kor_button = root.findViewById(R.id.btn_watch_kor);    //번역 가사 버튼
        final Button pause = root.findViewById(R.id.btn_delay);        //일시정지 버튼

        final String[] directoryName = {"love-yourself-mp3","love-yourself-kor","love-yourself-eng"};
        //final String fileName;

        //------------------------------------첫페이지 default값 ------------------------------------//
        //첫 페이지의 경우 버튼 누르기 전 url 전달이 되어야 하기 때문에 직접 넣었음.
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playAudio(url_default);
        }});
        //일시정지 버튼은 만들자. 아직 작동 안됨.----------------------------------------------//

        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                pauseAudio();
                /*
                if(player != null) {
                    player.pause();
                    pausePosition= player.getCurrentPosition();
                    player.pause();
                    Log.d("pause check",":"+pausePosition);
                }
                if(!player.isPlaying()){
                    player.seekTo(pausePosition);
                    player.start();
                }

                 */
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
        kor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                 TextView kor_text = getActivity().findViewById(R.id.tv_translated);
                    // Create a URL for the desired page
                    final String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/love-yourself-kor%2F";
                    final String full_kor_common_url = kor_common_url+"kor_1.txt?alt=media&token=kor_1";
                    find_kor(kor_text, full_kor_common_url);
                    Toast.makeText(getActivity(), "번역 가사", Toast.LENGTH_LONG).show();
            }
        });

        //-------------------------페이지 수 증가-> 페이지번호가 url에 반영 -> 해당 번째 구절 재생-----//
        btnIncrease.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int count = Integer.parseInt(textCounter.getText().toString());
                textCounter.setText(Integer.toString(count + 1));
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

                //일시정지 버튼은 만들자. 아직 작동 안됨.----------------------------------------------//
                Button pause = getView().findViewById(R.id.btn_delay);
                pause.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        if(player != null) {
                            player.pause();
                            //player.pause();
                            Log.d("pause check",":"+pausePosition);
                        }
                        if(!player.isPlaying()){
                            resumeAudio();
                        }
                    }
                });
                //number.setNum(count + 1);   //페이지 번호에 따른 곡 재생, 가사 정보를 가져오기 위한 것.
                //------------------------재생속도 기능--- 아직 하는 중 ...--------------------------//
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
                        TextView kor_text = getActivity().findViewById(R.id.tv_translated);
                        // Create a URL for the desired page
                        String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/love-yourself-kor%2F";
                        String full_kor_common_url = kor_common_url + kor_fileName + ".txt?alt=media&token=" + kor_fileName;

                        find_kor(kor_text, full_kor_common_url);
                        Toast.makeText(getActivity(), "번역 가사", Toast.LENGTH_LONG).show();

                    }
                    //다음 페이지로 넘어가면 사라지게.

                });
                //-----------------------번역 가사--끝----------------------------------------------//
                if (count == 16) {
                    textCounter.setText(Integer.toString(16));
                    Toast.makeText(getActivity(), "마지막 페이지 입니다", Toast.LENGTH_LONG).show();
                }
            }
        });
        //-------------------------페이지 수 감소-> 페이지번호가 url에 반영 -> 해당 번째 구절 재생-----//
        btnDecrease.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int count = Integer.parseInt(textCounter.getText().toString());
                textCounter.setText(Integer.toString(count - 1));
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
                        if(player != null) {
                            player.pause();
                            pausePosition= player.getCurrentPosition();
                            player.pause();
                            Log.d("pause check",":"+pausePosition);
                        }
                        if(!player.isPlaying()){
                            player.seekTo(pausePosition);
                            player.start();
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
                        TextView kor_text = getActivity().findViewById(R.id.tv_translated);
                        // Create a URL for the desired page
                        String kor_common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/love-yourself-kor%2F";
                        String full_kor_common_url = kor_common_url + kor_fileName + ".txt?alt=media&token=" + kor_fileName;

                        find_kor(kor_text, full_kor_common_url);
                        Toast.makeText(getActivity(), "번역 가사", Toast.LENGTH_LONG).show();
                    }
                });
                //-----------------------번역 가사--끝-----------------------------------------------//
                if (count == 1) {
                    textCounter.setText(Integer.toString(1));
                    Toast.makeText(getActivity(), "첫 페이지 입니다", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Inflate the layout for this fragment
        return root;
    }
    private void playAudio(String url) {
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

    //
    private void pauseAudio() {
        if (player != null) {
            position = player.getCurrentPosition();
            player.pause();

            Toast.makeText(getActivity(), "일시정지됨.", Toast.LENGTH_SHORT).show();
         }
    }

    private void resumeAudio() {
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
    public void slow_player(float speed, String url){
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
    public void find_kor(final TextView text, final String url_input){
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





