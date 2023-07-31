package com.example.whatdidieat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class List extends AppCompatActivity {
    static RequestQueue requestQueue;
    Intent intent;
    Intent getIntent;
    TextView listTextView;
    ImageView info;
    ImageView upload;
    RecyclerView diary;
    static DiaryAdapter adapter;
    String url;
    String address = "http://3.39.1.234:3000";
    private final long finishTime = 2000;
    private long pressTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        getIntent = getIntent();
        listTextView = findViewById(R.id.listTextView);
        info = findViewById(R.id.infoButton);
        upload = findViewById(R.id.uploadButton);
        diary = findViewById(R.id.diary);
        listTextView.setText(getIntent.getStringExtra("userName") + "님의 다이어리");
        listTextView.setSelected(true);

        // 다이어리 불러오기
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        diary.setLayoutManager(layoutManager);
        adapter = new DiaryAdapter(this, this);
        diary.setAdapter(adapter);
        adapter.updateList(adapter.items);
        adapter.notifyDataSetChanged();
        loadDiary();

        // 내 정보
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), Info.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getIntent = getIntent();
                intent.putExtra("userName", getIntent.getStringExtra("userName"));
                intent.putExtra("userID", getIntent.getStringExtra("userID"));
                startActivity(intent);
            }
        });

        // 업로드
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), Upload.class);
                //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                getIntent = getIntent();
                intent.putExtra("userName", getIntent.getStringExtra("userName"));
                intent.putExtra("userID", getIntent.getStringExtra("userID"));
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        long tempTime = System.currentTimeMillis();
        long intervalTime = tempTime - pressTime;
        if (0 <= intervalTime && finishTime >= intervalTime) {
            ActivityCompat.finishAffinity(this);
            System.exit(0);
        } else {
            pressTime = tempTime;
            Toast.makeText(getApplicationContext(), "뒤로가기를 한 번 더 누르시면 앱이 종료됩니다.", Toast.LENGTH_SHORT).show();
        }
    }

    // 다이어리 불러오기
    private void loadDiary() {
        url = address + "/diary";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonObject object = (JsonObject) JsonParser.parseString(response);
                Boolean check = object.get("validation").getAsBoolean();
//                JsonArray data = object.get("items").getAsJsonArray();
//                for (int i = 0; i < data.size(); i++) {
//                    JsonObject temp = (JsonObject) data.get(i);
//                    JsonObject data1 = (JsonObject) temp.get("image");
//                    Log.d("tag1", "data : " + data1.get("data").getClass().getSimpleName().toString());
//                }
                if (check) {
//                    Toast.makeText(getApplicationContext(), "다이어리 불러오기 성공", Toast.LENGTH_SHORT).show();
                    Gson gson = new Gson();
                    DiaryItems diaryItems = gson.fromJson(response, DiaryItems.class);
//                    Log.d("tag1", "123 " + diaryItems.items.toString());
//                    DiaryItem array = gson.fromJson(response, DiaryItem.class);
                    for (int i = 0; i < diaryItems.items.length; i++) {
                        DiaryItem item = diaryItems.items[i];
                        adapter.addItem(item);
//                    Log.d("tag1", data.get(i).toString());
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getApplicationContext(), "다이어리 불러오기 실패", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag1", "diaryRequest Failed");
                Toast.makeText(getApplicationContext(), "다이어리 불러오기에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                getIntent = getIntent();
                params.put("userID", getIntent.getStringExtra("userID"));

                Log.d("tag1", "diaryRequest " + params.toString());
                return params;
            }
        };

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                500,
                1,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        requestQueue.add(request);
    }
}