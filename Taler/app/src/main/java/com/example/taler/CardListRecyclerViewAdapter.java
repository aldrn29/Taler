package com.example.taler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

// ViewHolder Type과 함께 Adapter 상속
public class CardListRecyclerViewAdapter extends RecyclerView.Adapter<CardListRecyclerViewAdapter.ViewHolder> {
    private String[] notes = {"note01", "note02"};
//    private String[] thumbnails = {};

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title; //텍스트뷰 참조를 저장할 변수 꼭 이곳에 선언한다.

        public ViewHolder(View view){ //constructor
            super(view); //레이아웃 파일로부터 만들어진 뷰 객체를 상위 뷰홀더에 전달
            title = view.findViewById(R.id.title);
//            ImageView thumbnail = view.findViewById(R.id.thumbnail);
        }
    }

    //새로운 뷰와 뷰홀더를 만드는 메서드
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false); //레이아웃인플레이터로 레이아웃 파일에서 뷰객체 생성
        ViewHolder vh = new ViewHolder(v); //뷰홀더에 참조 저장
        return vh;
    }

    //뷰홀더를 이용해 데이터를 채우는 메서드
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.title.setText(notes[position]);
        //Todo set thumbnail from FB
    }

    //전체 아이템의 개수를 반환하는 메서드
    @Override
    public int getItemCount() {
        return notes.length;
    }
}
