package com.example.testproject;

import android.Manifest;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{

    private TextureView textureView;
    private Button btn_opt;
    private MediaRecorder mediaRecorder;
    private Camera camera;
    //创建音频文件



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 100);

        textureView = findViewById(R.id.textureView);
        btn_opt = findViewById(R.id.btn_opt);

        btn_opt.setOnClickListener(this);
    }

    @Override
    public void onClick(View v){
        CharSequence text = btn_opt.getText();
        if(TextUtils.equals(text,"开始")){
            btn_opt.setText("结束");

            camera = Camera.open();
            camera.setDisplayOrientation(90);
            camera.unlock();
            mediaRecorder = new MediaRecorder();
            mediaRecorder.setCamera(camera);

            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); //设置音频源 麦克风
            mediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//设备视频源 摄像头
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//指定视频文件格式
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mediaRecorder.setOrientationHint(90);
            //设置视频输出文件
            //mediaRecorder.setOutputFile(new File(getFilesDir(""), "a.mp4").getAbsolutePath());
            System.out.println("目前正常1");
            mediaRecorder.setOutputFile(new File(getExternalFilesDir(""),"a.mp4").getAbsolutePath());
            System.out.println("目前正常2");
            mediaRecorder.setVideoSize(640, 480);
            mediaRecorder.setPreviewDisplay(new Surface(textureView.getSurfaceTexture()));
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                mediaRecorder.start();
            }catch (Exception e){
                e.printStackTrace();
            }
        } else {
            btn_opt.setText("开始");
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;
            camera.stopPreview();
            camera.release();
        }

    }
}