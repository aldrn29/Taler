package com.example.taler.MediaMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.taler.R;

public class MediaMenuActivity extends AppCompatActivity {

    private MediaMenuAdapter adapter = new MediaMenuAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media_menu);

        RecyclerView recyclerView = findViewById(R.id.recyclerView_media_menu);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // item Load
        adapter.setItems(new MediaDataList().getItems());
    }
}