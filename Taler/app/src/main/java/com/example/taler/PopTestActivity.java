package com.example.taler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fragment.Fragment_Listen;
import com.example.fragment.Fragment_Speaker;


public class PopTestActivity extends AppCompatActivity  {
//implements Fragment_Speaker.DataPassListener
    private Fragment_Speaker fragment_speaker;
    private Fragment_Listen fragment_listen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_test);

        fragment_speaker = (Fragment_Speaker) getSupportFragmentManager().findFragmentById(R.id.fragment2);
        fragment_listen = (Fragment_Listen) getSupportFragmentManager().findFragmentById(R.id.fragment1);

    }

}