package com.example.fragment;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.taler.MainActivity;
import com.example.taler.PopTestActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.taler.R;
import com.google.firebase.storage.UploadTask;

import android.util.Log;

import org.w3c.dom.Text;

public class Fragment_Listen extends Fragment {

    MediaPlayer player;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageRef = storage.getReference();
    int position = 0;
    boolean isPaused = false;


    private int songNum;

    Num number = new Num();
    Choice choice = new Choice();

    //로그를 찍어보기 위한 것
    private static final String TAG = "Fragment_Listen";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        //페이지 수 count 버튼
        Button btnIncrease = root.findViewById(R.id.btn_next);
        Button btnDecrease = root.findViewById(R.id.btn_prev);
        final TextView textCounter = root.findViewById(R.id.tv_pageNum);

        //-------------------------------------페이지 수 집계---------------------------------------//

        btnIncrease.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int count = Integer.parseInt(textCounter.getText().toString());
                textCounter.setText(Integer.toString(count + 1));
                number.setNum(count+1);   //페이지 번호에 따른 곡 재생, 가사 정보를 가져오기 위한 것.
                Log.d("페이지 번호:", Integer.toString(number.getNum()));


                if (count ==16) {
                    textCounter.setText(Integer.toString(16));
                    Toast.makeText(getActivity(),"마지막 페이지 입니다", Toast.LENGTH_LONG).show();
                }
            }
        });
        btnDecrease.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int count = Integer.parseInt(textCounter.getText().toString());
                textCounter.setText(Integer.toString(count - 1));
                number.setNum(count-1);

                if (count ==1) {
                    textCounter.setText(Integer.toString(1));
                    Toast.makeText(getActivity(),"첫 페이지 입니다", Toast.LENGTH_LONG).show();
                }
            }
        });

        //음성 파일 play 버튼인 listen(1.0) , 0.5, 0.75 버튼
        //firebase 연동
        //-----------------------------------각 기능 버튼 클릭---------------------------------------//
        String[] directoryName = {"love-yourself-mp3","love-yourself-kor","love-yourself-eng"};
        String fileName = number.getNum()+".mp3";
        Log.d("mp3 파일명:", fileName);

        int i=0;
        final String common_url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/"+ directoryName[i] +"%2F";
        final String url = common_url + fileName+"?alt=media&token=file_"+number.getNum();
        Log.d("url:",url);
        //----------------------------------재생버튼------------------------------------------------//
        final Button play = root.findViewById(R.id.btn_play);
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Integer.parseInt(textCounter.getText().toString()) == 1){
                    String url = "https://firebasestorage.googleapis.com/v0/b/taler-db.appspot.com/o/love-yourself-mp3%2F1.mp3?alt=media&token=file_1";
                }

                if(player !=null){
                    player.release();
                }
                try{
                    player = new MediaPlayer();
                    player.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    player.setDataSource(url); //사이트 url
                    player.prepare();
                    player.start();
                    isPaused = false;
                }catch(Exception e)
                {
                    e.printStackTrace();
                }
                Toast.makeText(getActivity(),"Music player started",Toast.LENGTH_SHORT).show();
            }
        });
        Button pause = root.findViewById(R.id.btn_delay); //일시정지 버튼은 만들자. 일시 정지는 되지만 이어서 재생하는 부분이 안되었음.
        pause.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(player != null && player.isPlaying()){
                    position = player.getCurrentPosition();
                    player.pause();
                    isPaused = true;
                }
            }
        });

        //kor. 조건을 만족시키면 번역된 가사를 볼 수 있음. 일단 누르면 보게 해놓았음.
        final Button kor_button = root.findViewById(R.id.btn_watch_kor);
        final TextView kor_text = root.findViewById(R.id.tv_translated);

        kor_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(getActivity(),"번역 가사",Toast.LENGTH_SHORT).show();
            }
        });

        // Inflate the layout for this fragment
        return root;
    }
}
class Num{
    private int num;

    public int getNum(){
        return num;
    }
    public void setNum(int num){
        this.num = num;
    }
}
class Choice{
    private String menu;

    public String getMenu(){
        return menu;
    }
    public void setMenu(String menu){
        this.menu = menu;
    }
}

