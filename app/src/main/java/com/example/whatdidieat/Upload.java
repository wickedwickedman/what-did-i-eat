package com.example.whatdidieat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Upload extends AppCompatActivity {
    static RequestQueue requestQueue;
    LinearLayout uploadLinearLayout;
    LinearLayout uploadLinearLayout2;
    Intent intent;
    Intent getIntent;
    ImageView image;
    ImageView back;
    Button write;
    EditText foodName;
    EditText calories;
    EditText title;
    EditText text;
    String url;
    String address = "http://3.39.1.234:3000";
    String uploadImage = "";
    Boolean isCamera = false;
    ActivityResultLauncher<Intent> activityResultLauncher;
    Context mContext = this;
    ImageConverter converter = new ImageConverter();
    Login login = new Login();
    AlertDialog.Builder backCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        uploadLinearLayout = findViewById(R.id.uploadLinearLayout);
        uploadLinearLayout2 = findViewById(R.id.uploadLinearLayout2);
        image = findViewById(R.id.image);
        back = findViewById(R.id.back);
        write = findViewById(R.id.writeButton);
        foodName = findViewById(R.id.foodName);
        calories = findViewById(R.id.calories);
        title = findViewById(R.id.title);
        text = findViewById(R.id.text);

        // 키보드 숨기기
        uploadLinearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        // 키보드 숨기기
        uploadLinearLayout2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
            }
        });

        // 뒤로가기
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // 뒤로가기 다이얼로그
        backCheck = new AlertDialog.Builder(this)
                .setTitle("뒤로가기")
                .setMessage("정말 뒤로가시겠습니까? \n저장되지 않은 정보는 사라집니다.")
                .setPositiveButton("네", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        back();
                    }
                })
                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });

        // 사진 업로드 다이얼로그
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("사진 업로드");
        builder.setItems(R.array.photo, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int pos) {
                if (pos == 0) {
                    // 앨범에서 사진 선택
                    isCamera = false;
                    getAlbum();
                } else if (pos == 1) {
                    // 카메라로 사진 촬영
                    if (login.cameraPermission) {
                        isCamera = true;
                        takePhoto();
                    } else {
                        if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                            Toast.makeText(mContext, "카메라 권한이 필요합니다.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:" + getPackageName()));
                            startActivity(intent);
                        } else if (ActivityCompat.checkSelfPermission(mContext, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                            login.cameraPermission = true;
                            isCamera = true;
                            takePhoto();
                        }
                    }
                }
            }
        });

        // 사진 업로드
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        // 작성
        write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadCheck();
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (result.getResultCode() == RESULT_OK) {
                    Bitmap bitMap = null;
                    Uri uri = null;
                    if (isCamera == true) {
                        //카메라로 직접 저장
                        Bundle bundle = result.getData().getExtras();
                        bitMap = converter.resize(mContext, ((Bitmap) bundle.get("data")));
                        image.setImageBitmap(bitMap);
                    } else {
                        // 앨범에서 사진 선택
                        Intent intent = result.getData();
                        uri = intent.getData();
                        try {
                            bitMap = converter.resize(mContext, MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), uri));
                            image.setImageBitmap(bitMap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    uploadImage = converter.bitmapToString(bitMap);
//                            Log.d("tag1", "123" + uploadImage);
//                            Toast.makeText(mContext, bitMap.getAllocationByteCount(), Toast.LENGTH_SHORT).show();
//                  이미지 분석
                    analyzeImage(uploadImage);
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        AlertDialog alertDialog = backCheck.create();
        alertDialog.show();
    }

    public void back() {
        intent = new Intent(getApplicationContext(), List.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        getIntent = getIntent();
        intent.putExtra("userName", getIntent.getStringExtra("userName"));
        intent.putExtra("userID", getIntent.getStringExtra("userID"));
        startActivity(intent);
    }

    // 키보드 숨기기
    private void hideKeyboard() {
        if (getCurrentFocus() != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    // 앨범에서 사진 선택
    private void getAlbum() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);
    }

    // 카메라로 사진 촬영
    private void takePhoto() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        activityResultLauncher.launch(intent);
    }

    // 이미지 분석
    private void analyzeImage(String uploadImage) {
        url = "http://52.64.132.172/image";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
//                JsonObject object = (JsonObject) JsonParser.parseString(response);
//                Boolean check = object.get("validation").getAsBoolean();
//                if (check) {
//                Log.d("tag1", "analyzeImage " + response);
//                    Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), "이미지 분석에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
//                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("tag1", "analyzeImage " + error.toString());
                Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("image", uploadImage);

                Log.d("tag1", "analyzeImage " + params.toString().length());
                return params;
            }
        };

        request.setRetryPolicy(new com.android.volley.DefaultRetryPolicy(
                1000,
                10,
                com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    // 다이어리 유효성 검사
    private void uploadCheck() {
        Boolean foodNameFlag;
        Boolean caloriesFlag;
        Boolean titleFlag;
        Boolean textFlag;

        if (foodName.getText().toString().length() >= 1 && foodName.getText().toString().length() <= 10) {
            foodNameFlag = true;
        } else {
            foodNameFlag = false;
        }

        if (calories.getText().toString().length() >= 1 && calories.getText().toString().length() <= 4) {
            caloriesFlag = true;
        } else {
            caloriesFlag = false;
        }

        if (title.getText().toString().length() >= 1 && title.getText().toString().length() <= 20) {
            titleFlag = true;
        } else {
            titleFlag = false;
        }

        if (text.getText().toString().length() >= 1 && text.getText().toString().length() <= 1000) {
            textFlag = true;
        } else {
            textFlag = false;
        }

        if (!foodNameFlag) {
            Toast.makeText(getApplicationContext(), "음식 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!caloriesFlag) {
            Toast.makeText(getApplicationContext(), "칼로리를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!titleFlag) {
            Toast.makeText(getApplicationContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else if (!textFlag) {
            Toast.makeText(getApplicationContext(), "내용을 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            diaryUpload(uploadImage);
//            Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();
        }
    }

    // 다이어리 업로드
    private void diaryUpload(String uploadImage) {
        url = address + "/diary/upload";
        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JsonObject object = (JsonObject) JsonParser.parseString(response);
                Boolean check = object.get("validation").getAsBoolean();
                if (check) {
                    back();
//                    Toast.makeText(getApplicationContext(), "작성 완료", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "작성에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "업로드 실패", Toast.LENGTH_SHORT).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() {
                getIntent = getIntent();
                Map<String, String> params = new HashMap<>();
                params.put("diaryID", getIntent.getStringExtra("userID") + "_" + System.currentTimeMillis());
                params.put("userID", getIntent.getStringExtra("userID"));
                params.put("title", title.getText().toString());
                params.put("text", text.getText().toString());
                params.put("foodName", foodName.getText().toString());
                params.put("calories", calories.getText().toString());
                params.put("date", LocalDateTime.now().toString());
                params.put("image", uploadImage);

//                Log.d("tag1", "diaryUpload " + uploadImage);
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