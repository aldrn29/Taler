package com.example.taler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class LoginActivity extends AppCompatActivity {

    private EditText email_login;
    private EditText pwd_login;

    private CheckBox cb_save; //로그인 시 자동완성 추가
    private Context mContext;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 기존 테마로 되돌림
        setTheme(R.style.NoTitleTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mContext = this;

        Button join = findViewById(R.id.button_sign_in);
        Button login = findViewById(R.id.button_login);
        email_login = (EditText) findViewById(R.id.login_email);
        pwd_login = (EditText) findViewById(R.id.login_password);
        cb_save = (CheckBox) findViewById(R.id.checkBox);

        mAuth = FirebaseAuth.getInstance();
        boolean boo = PreferencesManager.getBoolean(mContext,"check");

        if(boo){
            email_login.setText(PreferencesManager.getString(mContext, "id"));
            pwd_login.setText(PreferencesManager.getString(mContext, "pw"));
            cb_save.setChecked(true); //체크박스는 여전히 체크 표시 하도록 셋팅
        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_login.getText().toString().trim();
                String pwd = pwd_login.getText().toString().trim();

                mAuth.signInWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(LoginActivity.this, "로그인 오류", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                PreferencesManager.setString(mContext, "id", email_login.getText().toString()); //id라는 키값으로 저장
                PreferencesManager.setString(mContext, "pw", pwd_login.getText().toString()); //pw라는 키값으로 저장

                // 저장한 키 값으로 저장된 아이디와 암호를 불러와 String 값에 저장
                String checkId = PreferencesManager.getString(mContext, "id");
                String checkPw = PreferencesManager.getString(mContext, "pw"); //아이디와 암호가 비어있는 경우를 체크
                if (TextUtils.isEmpty(checkId) || TextUtils.isEmpty(checkPw)){
                    //아이디나 암호 둘 중 하나가 비어있으면 토스트메시지를 띄운다
                    Toast.makeText(LoginActivity.this, "아이디/암호를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //로그인 기억하기 체크박스 유무에 따른 동작 구현
        cb_save.setOnClickListener(new CheckBox.OnClickListener() {
            @Override public void onClick(View v) {
                if (((CheckBox)v).isChecked()) { // 체크박스 체크 되어 있으면
                                                // editText에서 아이디와 암호 가져와 PreferenceManager에 저장한다.
                    PreferencesManager.setString(mContext, "id", email_login.getText().toString()); //id 키값으로 저장
                    PreferencesManager.setString(mContext, "pw", pwd_login.getText().toString()); //pw 키값으로 저장
                    PreferencesManager.setBoolean(mContext, "check", cb_save.isChecked()); //현재 체크박스 상태 값 저장
                    }
                else { //체크박스가 해제되어있으면
                    PreferencesManager.setBoolean(mContext, "check", cb_save.isChecked()); //현재 체크박스 상태 값 저장
                    PreferencesManager.clear(mContext); //로그인 정보를 모두 날림
                    }
            } }) ;

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}
