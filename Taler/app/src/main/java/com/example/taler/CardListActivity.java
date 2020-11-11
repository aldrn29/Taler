package com.example.taler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class CardListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true); // 모은 아이템뷰의 사이즈가 고정되어있음

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); //layoutManager 생성
        recyclerView.setLayoutManager(layoutManager); //연결

        RecyclerView.Adapter adapter = new CardListRecyclerViewAdapter(); // 객체생성
        recyclerView.setAdapter(adapter);


        Button selectButton = findViewById(R.id.btn_select);
        selectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), StoryCardActivity.class);
                startActivity(intent);
            }
        });
    }
}