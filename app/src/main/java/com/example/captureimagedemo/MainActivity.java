package com.example.captureimagedemo;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    //Initialize Variable 변수선언
    ImageView imageView;
    Button btOpen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);//activity_main을 불러와라

        //Assign Variable
        imageView = findViewById(R.id.image_view); //액티비티메인에 있는거 갖다쓴거
        btOpen = findViewById(R.id.bt_open);


        //Request For Camera permission 카메라 허가 요청
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }

        btOpen.setOnClickListener(new View.OnClickListener() { //setonclicklistner:클릭을 했을때 밑을 수행해라
            @Override
            public void onClick(View view) {
                //open camera 카메라 열기
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE); //intent 화면 전환시 사용됨
                startActivityForResult(intent, 100);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            //get capture image
            Bitmap captureImage = (Bitmap) data.getExtras().get("data"); //bitmap이미지 사용을 해서 쓰임.
            //set captute image to ImageView
            imageView.setImageBitmap(captureImage);
        }

        new AlertDialog.Builder(MainActivity.this) // TestActivity 부분에는 현재 Activity의 이름 입력.
                .setMessage("여기에 멘트를 뭘로할까요?")     // 제목 부분 (직접 작성)
                .setPositiveButton("보내기", new DialogInterface.OnClickListener() {      // 버튼1 (직접 작성)
                    public void onClick(DialogInterface dialog, int which){
                        Toast.makeText(getApplicationContext(), "확인 누름", Toast.LENGTH_SHORT).show(); // 실행할 코드
                        AddPostServer();
                    }
                })
                .setNegativeButton("다시 찍기", new DialogInterface.OnClickListener() {     // 버튼2 (직접 작성)
                    public void onClick(DialogInterface dialog, int which){
                        Toast.makeText(getApplicationContext(), "취소 누름", Toast.LENGTH_SHORT).show(); // 실행할 코드
                    }
                })
                .show();
    }


    public void AddPostServer() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(DjangoApi.DJANGO_SITE)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        DjangoApi postApi= retrofit.create(DjangoApi.class);


        PostModel postModel = new PostModel("hello world", "from chicken wing", "Programmer", 9999);
        Gson gson = new Gson();

        String obj = gson.toJson(postModel); //{"age":25,"job":"programmer","name":"뀨뀩이","type":"type"}
        JsonObject object = gson.fromJson(obj, JsonObject.class);

        System.out.println("Data: " + object);
        System.out.println("Type: " + object.getClass().getName());

        Call<RequestBody> call = postApi.addPostVoditel(object);

        call.enqueue(new Callback<RequestBody>() {
            @Override
            public void onResponse(Call<RequestBody> call, Response<RequestBody> response) {
                Log.d("good", "good");
            }
            @Override
            public void onFailure(Call<RequestBody> call, Throwable t) {
                Log.d("fail", "fail");
            }
        });
    }


}