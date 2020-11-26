package com.example.taler.Story;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.example.taler.R;

import java.util.ArrayList;

public class CardListActivity extends AppCompatActivity {

    private ArrayList<CardDictionary> arrayList;
    private CardListRecyclerViewAdapter adapter;
    private int count = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_list);

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true); // 모은 아이템뷰의 사이즈가 고정되어있음

//        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this); //layoutManager 생성
//        layoutManager.setAutoMeasureEnabled(false); //레이아웃 메니저가 아이템뷰의 폭을 임의로 조정하지 않도록 설정

        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //레이아웃 생성
        recyclerView.setLayoutManager(layoutManager); //연결

        arrayList = new ArrayList<>();

        int num_of_title = 3;
        for(int i = 0; i < num_of_title; i++){
            CardDictionary data = new CardDictionary(i);
            arrayList.add(data); //리사이클러뷰 마지막줄에 삽입 첫줄에 삽입은 (0, data)
        }

        adapter = new CardListRecyclerViewAdapter(arrayList); // 객체생성
        recyclerView.setAdapter(adapter);

        //터치
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CardDictionary dict = arrayList.get(position); //아이덴티파이어는 현재 리스트 오더에 따른다. 타이틀카드 리스트이다.

                Intent intent = new Intent(getApplicationContext(), StoryCardActivity.class);
                //identifier PK를 넘겨준다.
                intent.putExtra("id", dict.getId());
                intent.putExtra("title", dict.getTitle());
                intent.putExtra("genre", dict.getGenre());

                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int postion) {

            }
        }));


//        //현재는 임의 리스트 생성
//        Button insertButton = findViewById(R.id.btn_generate);
//        insertButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                count++;
//
//                CardDictionary data = new CardDictionary(count + "", "Test" + count);
//
//                arrayList.add(data); //리사이클러뷰 마지막줄에 삽입 첫줄에 삽입은 (0, data)
//
//                adapter.notifyDataSetChanged();
//            }
//        });
    }

    //터치
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int postion);
    }

    //터치
    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {
        private GestureDetector gestureDetector;
        private CardListActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final CardListActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildAdapterPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }
}