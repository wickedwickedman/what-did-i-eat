package com.example.whatdidieat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.splashscreen.SplashScreen;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    static RequestQueue requestQueue;
    LinearLayout loginLinearLayout;
    Intent intent;
    Intent getIntent;
    Button signup;
    Button login;
    TextView error;
    EditText editID;
    EditText editPW;
    String id;
    String pw;
    String url;
    String address = "http://3.39.1.234:3000";
    static Boolean cameraPermission = false;
    private final long finishTime = 2000;
    private long pressTime = 0;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor autoLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        setTheme(R.style.BasicTheme);
        setContentView(R.layout.activity_login);

        loginLinearLayout = findViewById(R.id.loginLinearLayout);
        signup = findViewById(R.id.signupButton);
        login = findViewById(R.id.loginButton);
        editID = findViewById(R.id.editID);
        editPW = findViewById(R.id.editPW);
        error = findViewById(R.id.errorMessage);

        sharedPreferences = getSharedPreferences("sharedPreferences", Activity.MODE_PRIVATE);
        autoLogin = sharedPreferences.edit();
        id = sharedPreferences.getString("id", null);
        pw = sharedPreferences.getString("pw", null);

        if (id != null && pw != null) {
            editID.setText(id);
            editPW.setText(pw);
            autoLogin.putString("id", id);
            autoLogin.putString("pw", pw);
            autoLogin.commit();
            loginRequest();
        }

        SplashScreen.installSplashScreen(this);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // 임시(나중에 삭제)
//        editID.setText("asdf1234");
//        editPW.setText("asdf1234");

        editID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editID.getText().length() < 6 || editPW.getText().length() < 8) {
                    login.setEnabled(false);
                    login.setClickable(false);
                    login.setBackgroundResource(R.drawable.box_gray);
                    login.setTextColor(Color.BLACK);
                } else {
                    login.setEnabled(true);
                    login.setClickable(true);
                    login.setBackgroundResource(R.drawable.box_blue);
                    login.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (editID.getText().length() < 6 || editPW.getText().length() < 8) {
                    login.setEnabled(false);
                    login.setClickable(false);
                    login.setBackgroundResource(R.drawable.box_gray);
                    login.setTextColor(Color.BLACK);
                } else {
                    login.setEnabled(true);
                    login.setClickable(true);
                    login.setBackgroundResource(R.drawable.box_blue);
                    login.setTextColor(Color.WHITE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        editID.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    editID.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    editID.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        editPW.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    editPW.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    editPW.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        // 키보드 숨기기
        loginLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        // 회원가입
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent = new Intent(getApplicationContext(), Signup.class);
                startActivity(intent);
            }
        });

        // 로그인
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginRequest();
            }
        });
        checkPermission();
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

    // 키보드 숨기기
    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 카메라 권한 확인
    public void checkPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA);
        if (permission == PackageManager.PERMISSION_DENIED) {
            Log.d("tag1", "permission " + permission);
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, 111);
        }
    }

    // 권한 승인 완료 확인 코드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                cameraPermission = true;
            } else {
                cameraPermission = false;
            }
        }
    }

    // 로그인 요청
    private void loginRequest() {
        url = address + "/login/request";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonObject object = (JsonObject) JsonParser.parseString(response);
                Boolean check = object.get("validation").getAsBoolean();
                String userID = object.get("id").getAsString();
                String userName = object.get("name").getAsString();
                String userBirth = object.get("birth").getAsString();
                String userEmail = object.get("email").getAsString();
                if (check) {
//                    Toast.makeText(getApplicationContext(), "로그인 성공", Toast.LENGTH_SHORT).show();
                    intent = new Intent(getApplicationContext(), List.class);
                    intent.putExtra("userID", userID);
                    intent.putExtra("userName", userName);
                    autoLogin = sharedPreferences.edit();
                    autoLogin.putString("name", userName);
                    autoLogin.putString("id", editID.getText().toString());
                    autoLogin.putString("pw", editPW.getText().toString());
                    autoLogin.putString("birth", userBirth);
                    autoLogin.putString("email", userEmail);
                    autoLogin.commit();
                    startActivity(intent);
                } else {
                    error.setText("아이디 또는 비밀번호를 확인해주세요.");
                    error.setVisibility(View.VISIBLE);
//                    Toast.makeText(getApplicationContext(), "아이디 혹은 비밀번호를 확인해주세요.", Toast.LENGTH_SHORT).show();
                }
                Log.d("tag1", "loginRequest " + check.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag1", "loginRequest " + error.toString());
                Toast.makeText(getApplicationContext(), "로그인 실패 loginRequest", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", editID.getText().toString());
                params.put("pw", editPW.getText().toString());

                Log.d("tag1", "loginRequest " + params.toString());
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