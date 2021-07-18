package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private OkHttpClient okHttpClient;
    private Retrofit retrofit;
    private static final String url = "https://api.oj.cs.sdu.edu.cn:8080";
    HttpService httpService = null;



    /*
    // URL模板
    http://fy.iciba.com/ajax.php

    // URL实例
    http://fy.iciba.com/ajax.php?a=fy&f=auto&t=auto&w=hello%20world

    // 参数说明：
    // a：固定值 fy
    // f：原文内容类型，日语取 ja，中文取 zh，英语取 en，韩语取 ko，德语取 de，西班牙语取 es，法语取 fr，自动则取 auto
    // t：译文内容类型，日语取 ja，中文取 zh，英语取 en，韩语取 ko，德语取 de，西班牙语取 es，法语取 fr，自动则取 auto
    // w：查询内容

    */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://www.httpbin.org/") //设置网络请求的Url地址
                .addConverterFactory(GsonConverterFactory.create()) //设置数据解析器
                .build();
        //创建网络请求接口的实例
        httpService = retrofit.create(HttpService.class);


    }


    public void getSync() throws IOException {

    }

    public void getSync(View view) throws IOException {
        //对发送请求进行封装
        retrofit2.Call<ResponseBody> call = httpService.getCopyingright();
        Response<ResponseBody> execute = call.execute();
        ResponseBody body = execute.body();
        String string = body.string();
        System.out.println(string);
    }

    public void request(View view) {
        //对 发送请求 进行封装
        retrofit2.Call<reception> call = httpService.getCall();

        //步骤6:发送网络请求(异步)
        call.enqueue(new Callback<reception>() {
            @Override
            public void onResponse(@NotNull Call<reception> call, @NotNull Response<reception> response) {
                Log.d(TAG,response.body().toString());
                response.body().show();
            }

            @Override
            public void onFailure(@NotNull Call<reception> call, @NotNull Throwable t) {
                Log.d(TAG,"连接失败");
                System.out.println("连接失败");
            }
        });
    }

    public void postAsync(View view) {
        retrofit2.Call<ResponseBody> call = httpService.post("lance", "123");
        call.enqueue(new retrofit2.Callback<ResponseBody>() {
            @Override
            public void onResponse(retrofit2.Call<ResponseBody> call, retrofit2.Response<ResponseBody> response) {
                try {
                    Log.i(TAG, "postAsync: " + response.body().string());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<ResponseBody> call, Throwable t) {

            }
        });
    }


    public void dir(View view) {
        //startActivity(new Intent(this, MediaRecorderTest.class));
        //startActivity(new Intent(this,MainActivity2.class));
        startActivity(new Intent(this,AudioRecordTest.class));
    }
}