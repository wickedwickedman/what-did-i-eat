package com.example.whatdidieat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BasicTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}