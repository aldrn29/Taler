package com.example.taler;

import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

// ViewHolder Type과 함께 Adapter 상속
public class CardListRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    //새로운 뷰와 뷰홀더를 만드는 메서드
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    //뷰홀더를 이용해 데이터를 채우는 메서드
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    //전체 아이템의 개수를 반환하는 메서드
    @Override
    public int getItemCount() {
        return 0;
    }
}
