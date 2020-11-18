package com.example.taler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fragment.Fragment_Listen;
import com.example.fragment.Fragment_Speaker;

public class PopTestActivity extends AppCompatActivity  {
//implements Fragment_Speaker.DataPassListener
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pop_test);

        //getSupportFragmentManager().beginTransaction().add(R.id.container,new Fragment_Speaker()).commit();
        /*
        if (findViewById(R.id.container) != null) {
            if (savedInstanceState != null) {
                return;
            }
            getSupportFragmentManager().beginTransaction().add(R.id.container, new Fragment_Speaker()).commit();
        }

         */
    }
    /*
    @Override
    public void passData(String data) {
        Fragment_Listen fragmentB = new Fragment_Listen ();
        Bundle args = new Bundle();
        args.putString(Fragment_Listen.DATA_RECEIVE, data);
        fragmentB .setArguments(args);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, fragmentB).commit();
    }

     */
}