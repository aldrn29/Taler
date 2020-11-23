package com.example.taler.MediaMenu;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.taler.MediaActivity;
import com.example.taler.PopTestActivity;
import com.example.taler.R;

import java.util.ArrayList;

public class MediaMenuAdapter extends RecyclerView.Adapter<MediaMenuAdapter.ViewHolder> {
    private ArrayList<MediaData> datas = new ArrayList<>();


    @NonNull
    @Override
    public MediaMenuAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(itemView);

        return viewHolder;
    }

    // 화면에 데이터와 레이아웃 연결
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, final int position) {
        MediaData data = datas.get(position);

        Glide.with(viewHolder.itemView.getContext())
                .load(data.getUrl())
                .into(viewHolder.img);
        viewHolder.title.setText(data.getTitle());
        viewHolder.genre.setText(data.getGenre());

        /*
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                viewHolder.itemView
                Intent i = new Intent(view.getContext(), MediaActivity.class);
                view.getContext().startActivity(i);
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public void setItems(ArrayList<MediaData> items) {
        this.datas = items;
    }

    // 뷰 가져오기
    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img;
        TextView title, genre;
        RecyclerView layout;

        ViewHolder(final View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView_item);
            title = itemView.findViewById(R.id.textView_item_title);
            genre = itemView.findViewById(R.id.textView_item_genre);
            layout = itemView.findViewById(R.id.recyclerView_media_menu);

            // Genre로 값 받아오게 수정
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();//getAbsoluteAdapterPosition();

                    switch (pos) {
                        case 0: {
                            Intent intent = new Intent(itemView.getContext(), PopTestActivity.class);
                            ContextCompat.startActivity(itemView.getContext(), intent, null);
                            break;
                        }
                        case 1:
                        case 2: {
                            Intent intent = new Intent(itemView.getContext(), MediaActivity.class);
                            ContextCompat.startActivity(itemView.getContext(), intent, null);
                            break;
                        }
                        default: break;
                    }
                }
            });
        }
    }
}
