package com.example.taler;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

public class FloatingActivity extends AppCompatActivity {
    //popup 액티비티로 활용될 예정.
    //레이아웃을 테이블레이아웃과 리스트뷰 중 고민 중.

       Button backpass; //다시 poptest activity로 돌아감.

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            //타이틀바 없애기
            setContentView(R.layout.activity_floating);
            backpass = findViewById(R.id.btn_back);

            backpass.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {    //다시 돌아감.
                    Intent intent = new Intent(FloatingActivity.this, PopTestActivity.class);
                    startActivity(intent);
                }
            });

        }

    }

