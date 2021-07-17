package com.example.testproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;


import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class MediaRecorderTest extends AppCompatActivity {

    private static final String LOG_TAG = "AudioRecordTest";
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    private static String fileName = null;
    private android.media.MediaRecorder recorder = null;
    private MediaPlayer player = null;
    Button btn_record = null;
    Button btn_play = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record);

        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        btn_record = findViewById(R.id.btn_03);
        btn_play = findViewById(R.id.btn_04);
        btn_record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord();
            }
        });
        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay();
            }
        });

    }

    public void onRecord() {
        if (TextUtils.equals(btn_record.getText(), "开始录制")){
            btn_record.setText("结束录制");
            startRecording();
        } else {
            btn_record.setText("开始录制");
            stopRecording();
        }

    }


    public void onPlay() {
        if(TextUtils.equals(btn_record.getText(),"开始播放")) {
            btn_record.setText("结束播放");
            startPlaying();
        }
        else{
            btn_record.setText("开始播放");
            stopPlaying();
        }
    }

    private void startPlaying() {
        fileName = "a.mp3";
        player = new MediaPlayer();

        try {
            player.setDataSource(new File(getExternalFilesDir(""),"a.mp4").getAbsolutePath());
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    private void stopPlaying() {
        player.release();
        player = null;
    }

    private void startRecording() {

        //根据生命周期来
        //初始化
        recorder = new android.media.MediaRecorder();
        //初始化完成
        recorder.setAudioSource(android.media.MediaRecorder.AudioSource.MIC);

        //进入configured状态
        recorder.setOutputFormat(android.media.MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(android.media.MediaRecorder.AudioEncoder.AMR_NB);
        //recorder.setOutputFile(fileName);
        recorder.setOutputFile(new File(getExternalFilesDir(""),"a.mp4").getAbsolutePath());
        try {
            //进入准备状态
            recorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }

        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 100);
        recorder.start();
    }

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (recorder != null) {
            recorder.release();
            recorder = null;
        }

        if (player != null) {
            player.release();
            player = null;
        }
    }


}
