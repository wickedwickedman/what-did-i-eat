package com.example.whatdidieat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
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

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Signup extends AppCompatActivity {
    static RequestQueue requestQueue;
    LinearLayout signupLinearLayout;
    LinearLayout signupLinearLayout2;
    Intent intent;
    ImageView back;
    EditText name;
    EditText id;
    EditText pw;
    EditText pw2;
    EditText email;
    EditText birth;
    Button submit;
    String url;
    String address = "http://3.39.1.234:3000";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        signupLinearLayout = findViewById(R.id.signupLinearLayout);
        signupLinearLayout2 = findViewById(R.id.signupLinearLayout2);
        back = findViewById(R.id.back);
        name = findViewById(R.id.signName);
        id = findViewById(R.id.signID);
        pw = findViewById(R.id.signPW);
        pw2 = findViewById(R.id.signPW2);
        email = findViewById(R.id.signMail);
        birth = findViewById(R.id.signBirth);
        submit = findViewById(R.id.signupButton);

        name.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    name.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    name.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        id.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    id.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    id.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        pw.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    pw.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    pw.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        pw2.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    pw2.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    pw2.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        email.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    email.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    email.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        birth.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    birth.setBackgroundResource(R.drawable.box_black_underline);
                } else {
                    birth.setBackgroundResource(R.drawable.box_gray_underline);
                }
            }
        });

        // 키보드 숨기기
        signupLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        // 키보드 숨기기
        signupLinearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        //뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 임시(나중에 삭제)
        name.setText("최건영");
        id.setText("asdf1234");
        pw.setText("asdf1234");
        pw2.setText("asdf1234");
        email.setText("asdf1234@naver.com");
        birth.setText("20000101");

        //회원가입
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitCheck();
            }
        });
    }

    @Override
    public void onBackPressed() {
        intent = new Intent(getApplicationContext(), Login.class);
        //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    // 키보드 숨기기
    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void submitCheck() {
        Boolean nameFlag;
        Boolean idFlag;
        Boolean birthFlag;
        Boolean pwFlag;
        Boolean pwFlag2;
        Boolean emailFlag;

        if (name.getText().toString().length() >= 1 && name.getText().toString().length() <= 10) {
            nameFlag = true;
        } else {
            nameFlag = false;
        }

        if (id.getText().toString().length() >= 6 && id.getText().toString().length() <= 20) {
            idFlag = true;
        } else {
            idFlag = false;
        }

        if (birth.getText().toString().length() >= 1) {
            if (19000101 <= Integer.parseInt(birth.getText().toString()) && Integer.parseInt(birth.getText().toString()) <= 20230101) {
                birthFlag = true;
            } else {
                birthFlag = false;
            }
        } else {
            birthFlag = false;
        }

        if (pw.getText().toString().length() >= 8 && pw.getText().toString().length() <= 20) {
            pwFlag = true;
        } else {
            pwFlag = false;
        }

        if (pw.getText().toString().equals(pw2.getText().toString()) && pw.getText().toString().length() >= 8 && pw2.getText().toString().length() >= 8) {
            pwFlag2 = true;
        } else {
            pwFlag2 = false;
        }

        Pattern pattern = android.util.Patterns.EMAIL_ADDRESS;
        if (pattern.matcher(email.getText()).matches()) {
            emailFlag = true;
        } else {
            emailFlag = false;
        }

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
            signupRequest();
        }
    }

    // 회원가입 요청
    private void signupRequest() {
        url = address + "/signup/request";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonObject object = (JsonObject) JsonParser.parseString(response);
                Boolean check = object.get("validation").getAsBoolean();
                if (check) {
                    intent = new Intent(getApplicationContext(), Login.class);
                    startActivity(intent);
                    Toast.makeText(getApplicationContext(), "회원가입 성공", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "회원님의 정보로 이미 가입된 계정이 있습니다.", Toast.LENGTH_SHORT).show();
                }
                Log.d("tag1", "loginRequest " + check.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", id.getText().toString());
                params.put("name", name.getText().toString());
                params.put("pw", pw.getText().toString());
                params.put("birth", birth.getText().toString());
                params.put("email", email.getText().toString());

                Log.d("tag1", "signupRequest " + params.toString());
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