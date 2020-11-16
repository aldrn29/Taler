package com.example.taler;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

// ViewHolder Type과 함께 Adapter 상속
public class CardListRecyclerViewAdapter extends RecyclerView.Adapter<CardListRecyclerViewAdapter.ListViewHolder> {
    private String[] notes = {"note01", "note02"};
    private ArrayList<CardDictionary> mList;
//    private String[] thumbnails = {};

    public class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView id;
        public TextView title; //텍스트뷰 참조를 저장할 변수 꼭 이곳에 선언한다.

        public ListViewHolder(View itemView){ //constructor
            super(itemView); //레이아웃 파일로부터 만들어진 뷰 객체를 상위 뷰홀더에 전달
            title = itemView.findViewById(R.id.title);
            id = itemView.findViewById(R.id.id);
//            ImageView thumbnail = view.findViewById(R.id.thumbnail);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    int pos = getAbsoluteAdapterPosition();
//                    if (pos != RecyclerView.NO_POSITION){
////                        RecyclerView item = mData.get(pos);
//                    }
//                }
//            });
        }
    }
    //Todo constructor FB로 채울것
    public CardListRecyclerViewAdapter(ArrayList<CardDictionary> list){
        this.mList = list;
    }

    //새로운 뷰와 뷰홀더를 만드는 메서드
    @Override
    public ListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_card, parent, false); //레이아웃인플레이터로 레이아웃 파일에서 뷰객체 생성
        ListViewHolder vh = new ListViewHolder(v); //뷰홀더에 참조 저장
        return vh;
    }

    //뷰홀더를 이용해 데이터를 채우는 메서드
    @Override
    public void onBindViewHolder(ListViewHolder holder, int position) {
        holder.id.setText(mList.get(position).getId());
        holder.title.setText(mList.get(position).getTitle());
        //Todo set thumbnail from FB
    }

    //전체 아이템의 개수를 반환하는 메서드
    @Override
    public int getItemCount() {
        return (null != mList ? mList.size() : 0);
    }
}
