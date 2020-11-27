package com.example.taler;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.example.taler.MediaMenu.MediaMenuActivity;
import com.example.taler.Story.CardListActivity;

public class MenuFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);

        // Story Activity로 전환
        FrameLayout menu1 = view.findViewById(R.id.FrameLayout_menu1);
        menu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CardListActivity.class);
                startActivity(intent);
            }
        });

        // Media Activity로 전환
        FrameLayout menu2 = view.findViewById(R.id.FrameLayout_menu2);
        menu2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), MediaMenuActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

}