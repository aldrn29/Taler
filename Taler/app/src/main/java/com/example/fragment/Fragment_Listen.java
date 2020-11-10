package com.example.fragment;

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

public class Fragment_Listen extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_screen_slide_page, container, false);

        //페이지 수 count 버튼
        Button btnIncrease = root.findViewById(R.id.btn_next);
        Button btnDecrease = root.findViewById(R.id.btn_prev);
        final TextView textCounter = root.findViewById(R.id.tv_pageNum);

        //음성 파일 play 버튼인 listen(1.0) , 0.5, 0.75 버튼
        //firebase 연동
        StorageReference mStorageRef;
        mStorageRef = FirebaseStorage.getInstance().getReference();
        // Points to "images"
        StorageReference imagesRef = mStorageRef.child("images");

        // Points to "images/space.jpg"
        // Note that you can use variables to create child values
        String fileName = "space.jpg";
        StorageReference  spaceRef = imagesRef.child(fileName);

        // File path is "images/space.jpg"
        String path = spaceRef.getPath();

        // File name is "space.jpg"
        String name = spaceRef.getName();

        // Points to "images"
        imagesRef = spaceRef.getParent();



        //kor. 조건을 만족시키면 번역된 가사를 볼 수 있음.


        btnIncrease.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                int count = Integer.parseInt(textCounter.getText().toString());
                textCounter.setText(Integer.toString(count + 1));
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
                if (count ==1) {
                    textCounter.setText(Integer.toString(1));
                    Toast.makeText(getActivity(),"첫 페이지 입니다", Toast.LENGTH_LONG).show();
                }
            }
        });
        // Inflate the layout for this fragment
        return root;
    }
}
