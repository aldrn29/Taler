package com.example.taler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taler.Profile.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private EditText email_join, nickname_join, pwd_join;
    private DatabaseReference mRef;
    FirebaseAuth firebaseAuth;

    private TextView password_ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        email_join = findViewById(R.id.sign_up_email);
        nickname_join = findViewById(R.id.user_id);
        pwd_join = findViewById(R.id.sign_up_password);
        Button btn_register = findViewById(R.id.button_register);

        password_ref = findViewById(R.id.tv_refer);
        password_ref.setText("*비밀번호는 영문자와 숫자를 섞어서 작성하세요*");

        mRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = email_join.getText().toString().trim();
                String pwd = pwd_join.getText().toString().trim();

                firebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = firebaseAuth.getCurrentUser();
                                    writeNewUser(currentUser.getUid(), nickname_join.getText().toString(), currentUser.getEmail());
                                    Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "등록 에러", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        });
            }
        });
    }
//    private void onAuthSuccess(FirebaseUser user){
//        String username = usernameFromEmail(user.getEmail());
//
//        writeNewUser(user.getUid(), username, user.getEmail());
//
//        //go to next activity
//    }
//
//    private String usernameFromEmail(String email) {
//        if (email.contains("@")) {
//            return email.split("@")[0];
//        } else {
//            return email;
//        }
//    }

    private void writeNewUser(String userId, String name, String email){
        ArrayList<Boolean> storyList, musicList, videoList;
        storyList = new ArrayList<>();
        musicList = new ArrayList<>();
        videoList = new ArrayList<>();

        int num_story = 4;
        int num_music = 17;
        int num_video = 10;

        for(int i = 0; i < num_story; i++){
            storyList.add(false);
        }
        for(int i = 0; i < num_music; i++){
            musicList.add(false);
        }
        for(int i = 0; i < num_video; i++){
            videoList.add(false);
        }
        User user = new User(name, email, 0, storyList, musicList, videoList);
        mRef.child("users").child(userId).setValue(user);
    }
}