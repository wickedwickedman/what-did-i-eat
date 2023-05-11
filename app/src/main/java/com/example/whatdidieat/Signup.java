package com.example.whatdidieat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    ImageView back;
    EditText name;
    EditText id;
    EditText pw;
    EditText pw2;
    EditText email;
    EditText birthYear;
    EditText birthMonth;
    EditText birthDay;
    Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //뒤로 가기 버튼
        back = findViewById(R.id.back);
        back.setOnClickListener(v -> onBackPressed() );

        //기입 항목
        name = findViewById(R.id.signName);
        id = findViewById(R.id.signID);
        pw = findViewById(R.id.signPW);
        pw2 = findViewById(R.id.signPW2);
        email = findViewById(R.id.signMail);
        birthYear = findViewById(R.id.signBirth);
        birthMonth = findViewById(R.id.signBirth2);
        birthDay = findViewById(R.id.signBirth3);

        //회원가입 완료 버튼
        submit = findViewById(R.id.signupbutton);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCheck();
            }
        });
    }
    private void submitCheck() {
        Boolean nameFlag;
        Boolean idFlag;
        Boolean birthFlag;
        Boolean pwFlag;
        Boolean pwFlag2;
        Boolean emailFlag;

        // 이름 유효성 검사
        if(name.getText().toString().length() >= 1 && name.getText().toString().length() <= 10) {
            nameFlag = true;
        } else {
            nameFlag = false;
        }

        // 아이디 유효성 검사
        if(id.getText().toString().length() >= 6 && id.getText().toString().length() <= 20) {
            idFlag = true;
        } else {
            idFlag = false;
        }

        // 생년월일 유효성 검사
        if(birthYear.getText().toString().length() >= 1 && birthMonth.getText().toString().length() >= 1 && birthDay.getText().toString().length() >= 1) {
            if (Integer.parseInt(birthYear.getText().toString()) <= 2023 &&
                    1900 <= Integer.parseInt(birthYear.getText().toString()) &&
                    1 <= Integer.parseInt(birthMonth.getText().toString()) &&
                    12 >= Integer.parseInt(birthMonth.getText().toString()) &&
                    1 <= Integer.parseInt(birthDay.getText().toString()) &&
                    31 >= Integer.parseInt(birthDay.getText().toString())) {
                birthFlag = true;
            } else {
                birthFlag = false;
            }
        } else {
            birthFlag = false;
        }

        // 비밀번호 유효성 검사
        if(pw.getText().toString().length() >= 8 && pw.getText().toString().length() <= 20) {
            pwFlag = true;
        } else {
            pwFlag = false;
        }

        // 비밀번호 확인
        if(pw.getText().toString().equals(pw2.getText().toString()) && pw.getText().toString().length() >= 8 && pw2.getText().toString().length() >= 8) {
            pwFlag2 = true;
        } else {
            pwFlag2 = false;
        }

        // 이메일 유효성 검사
        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
        if(pattern.matcher(email.getText()).matches()){
            emailFlag = true;
        } else {
            emailFlag = false;
        }

        // 최종 검사
        if (!nameFlag) {
            Toast.makeText(getApplicationContext(), "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!idFlag) {
            Toast.makeText(getApplicationContext(), "아이디는 6~20자로 지정해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!birthFlag) {
            Toast.makeText(getApplicationContext(), "올바른 날짜 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
        } else if (!pwFlag) {
            Toast.makeText(getApplicationContext(), "비밀번호는 8~20자로 지정해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!pwFlag2) {
            Toast.makeText(getApplicationContext(), "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
        } else if (!emailFlag) {
            Toast.makeText(getApplicationContext(), "올바른 이메일 형식이 아닙니다.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "회원가입 성공!", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
        }
    }
}