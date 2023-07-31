package com.example.whatdidieat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Info extends AppCompatActivity {
    Intent intent;
    Intent getIntent;
    ImageView back;
    TextView infoID;
    TextView infoName;
    TextView infoBirth;
    TextView infoEmail;
    Button logout;
    String id;
    String pw;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        back = findViewById(R.id.back);
        infoID = findViewById(R.id.infoID);
        infoName = findViewById(R.id.infoName);
        infoBirth = findViewById(R.id.infoBirth);
        infoEmail = findViewById(R.id.infoEmail);
        logout = findViewById(R.id.logoutButton);

        sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        //뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        getIntent = getIntent();
        infoName.setText(sharedPreferences.getString("name", null));
        infoID.setText(sharedPreferences.getString("id", null));
        String birth = sharedPreferences.getString("birth", null);
        infoBirth.setText(birth.substring(0, 4) + "/" + birth.substring(4, 6) + "/" + birth.substring(6, 8));
        infoEmail.setText(sharedPreferences.getString("email", null));

        // 로그아웃
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.clear();
                editor.commit();
                intent = new Intent(getApplicationContext(), Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(getApplicationContext(), List.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getIntent = getIntent();
        intent.putExtra("userName", getIntent.getStringExtra("userName"));
        intent.putExtra("userID", getIntent.getStringExtra("userID"));
        startActivity(intent);
    }
}
