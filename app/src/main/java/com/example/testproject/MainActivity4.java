package com.example.testproject;

import org.jetbrains.annotations.NotNull;
import org.vosk.demo.Recognizer;


import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity4 extends Activity {

    /* Used to handle permission request */
    private static final int PERMISSIONS_REQUEST_RECORD_AUDIO = 1;
    private static final String TAG = "MainActivity4";

    TextView resultView;
    //控件
    Button btn_mic;


    private final static int AUDIO_INPUT = MediaRecorder.AudioSource.MIC;
    private final static int AUDIO_SAMPLE_RATE = 16000;
    private final static int AUDIO_CHANNEL = AudioFormat.CHANNEL_IN_MONO;
    private final static int AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private  String pcmFileName;
    private  String wavFileName;
    private AudioRecord audioRecord = null;  // 声明 AudioRecord 对象
    private int recordBufSize = 0; // 声明recoordBufffer的大小字段
    private byte[] buffer;
    private boolean isRecording;
    private Recognizer recognizer;
    private int score = 0;


    private final String[] permissions = new String[]{
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static final int MY_PERMISSIONS_REQUEST = 1001;



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(org.vosk.demo.R.layout.vosk);

        // Setup layout
        resultView = findViewById(org.vosk.demo.R.id.result_text);


        //findViewById(org.vosk.demo.R.id.recognize_file).setOnClickListener(view -> recognizeFile());

        btn_mic = findViewById(org.vosk.demo.R.id.recognize_mic);
        btn_mic.setOnClickListener(view -> {
            try {
                recognizeMicrophone();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


        // Check if user has given permission to record audio, init the model after permission is granted
        ActivityCompat.requestPermissions(this, permissions, MY_PERMISSIONS_REQUEST);


        int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.RECORD_AUDIO);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSIONS_REQUEST_RECORD_AUDIO);
        }

        pcmFileName = Environment.getExternalStorageDirectory() + "/Download/read12.pcm";
        wavFileName = Environment.getExternalStorageDirectory() + "/Download/read12.wav";

    }


    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void recognizeMicrophone() throws IOException {
        if(TextUtils.equals(btn_mic.getText(),"StartRecord")) {
            btn_mic.setText("Stop");
            startRecord();
        }else {
            btn_mic.setText("StartRecord");
            stopRecord();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void stopRecord(){
        isRecording = false;

        if (null != audioRecord) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        Pcm2WavUtil.pcmToWav(AUDIO_SAMPLE_RATE,AUDIO_CHANNEL,recordBufSize,pcmFileName,wavFileName);
        check();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void check(){
        recognizer =new Recognizer(MainActivity4.this,"This morning is cool.I am going to play basketball.After play basketball,I feel tired,but I am very happy.Because basketball is my favourite sport.My body is strong.I hope you like basketball too.",wavFileName);

        @SuppressLint("HandlerLeak") Handler handler = new Handler(){
            @SuppressLint("HandlerLeak")
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                //判断标志位
                if (msg.what == 1) {
                    System.out.println("分数为"+msg.obj);
                    //score = (Integer) msg.obj;
                }
            }
        };

        //System.out.println(recognizer.getScore());
        //recognizer.b();
        //recognizer.a(handler);


        try {
            //recognizer.initModel();
            recognizer.b();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            //recognizer.build(handler);
            recognizer.a(handler);
        } catch (Exception e) {
            e.printStackTrace();
        }
        seeScore();
    }

    private void seeScore(){
        System.out.println("看看分数为"+score);
    }

    public void startRecord(){

        //audioRecord能接受的最小的buffer大小
        recordBufSize = AudioRecord.getMinBufferSize(AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING);
        audioRecord = new AudioRecord(AUDIO_INPUT, AUDIO_SAMPLE_RATE, AUDIO_CHANNEL, AUDIO_ENCODING, recordBufSize);
        buffer = new byte[recordBufSize];

        audioRecord.startRecording();
        isRecording = true;

        new Thread(() -> {
            FileOutputStream os = null;

            try {
                if(!new File(pcmFileName).exists()){
                    new File(pcmFileName).createNewFile();
                }
                os = new FileOutputStream(pcmFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (null != os) {
                while (isRecording) {
                    int read = audioRecord.read(buffer, 0, recordBufSize);
                    // 如果读取音频数据没有出现错误，就将数据写入到文件
                    if (AudioRecord.ERROR_INVALID_OPERATION != read) {
                        try {
                            os.write(buffer);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

}
