package com.example.whatdidieat;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Upload extends AppCompatActivity {
    ImageView back;
    TextView write;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        back = findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), List.class);
                startActivity(intent);
            }
        });

        write = findViewById(R.id.writeButton);
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), List.class);
                startActivity(intent);
            }
        });
    }
}
