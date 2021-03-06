package com.example.testproject.useless;

import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.testproject.R;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class AudioRecordTest extends AppCompatActivity implements Runnable {

    private final static String TAG = "AudioRecordTest";

    private Button mBtnStartRecord,mBtnStopRecord;

    //指定音频源 这个和MediaRecorder是相同的 MediaRecorder.AudioSource.MIC指的是麦克风
    private static final int mAudioSource = MediaRecorder.AudioSource.MIC;

    //指定采样率 （MediaRecorder 的采样率通常是8000Hz AAC的通常是44100Hz。 设置采样率为44100，目前为常用的采样率，官方文档表示这个值可以兼容所有的设置）
    private static final int mSampleRateInHz=44100 ;

    //指定捕获音频的声道数目。在AudioFormat类中指定用于此的常量
    private static final int mChannelConfig= AudioFormat.CHANNEL_CONFIGURATION_MONO; //单声道

    //指定音频量化位数 ,在AudioFormat类中指定了以下各种可能的常量。通常我们选择ENCODING_PCM_16BIT和ENCODING_PCM_8BIT PCM代表的是脉冲编码调制，它实际上是原始音频样本。
    //因此可以设置每个样本的分辨率为16位或者8位，16位将占用更多的空间和处理能力,表示的音频也更加接近真实。
    private static final int mAudioFormat=AudioFormat.ENCODING_PCM_16BIT;

    //指定缓冲区大小。调用AudioRecord类的getMinBufferSize方法可以获得。
    private int mBufferSizeInBytes;

    private File mRecordingFile =new File(getExternalFilesDir(""), "audiorecordtest.pcm");;//储存AudioRecord录下来的文件
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord=null;
    private File mFileRoot=null;//文件目录

    //存放的目录路径名称
    //private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/AudioRecordFile";
    private static final String mPathName = "/AudioRecordFile";


    //保存的音频文件名
    private static final String mFileName = "audiorecordtest.pcm";

    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;
    private DataOutputStream mDataOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record_test);

        ActivityCompat.requestPermissions(this, new String[]{android
                .Manifest.permission.WRITE_EXTERNAL_STORAGE}, 10001);

        initDatas();
        initUI();

    }

    //初始化数据
    private void initDatas() {
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz,mChannelConfig, mAudioFormat);//计算最小缓冲区

        //创建AudioRecorder对象
        mAudioRecord = new AudioRecord(mAudioSource,mSampleRateInHz,mChannelConfig,
                mAudioFormat, mBufferSizeInBytes);

        mFileRoot = new File(mPathName);
        //mFileRoot = new File(getExternalFilesDir(""));
        if(!mFileRoot.exists()) {
            //创建文件夹
            mFileRoot.mkdirs();
        }
        Log.d(TAG,mFileRoot.getAbsolutePath());

    }

    //初始化UI
    private void initUI() {
        mBtnStartRecord = findViewById(R.id.btn_start_record);
        mBtnStopRecord = findViewById(R.id.btn_stop_record);

        mBtnStartRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecord();
            }
        });

        mBtnStopRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecord();
            }
        });
    }

    //开始录音
    public void startRecord() {

        //如果当前正在录音，则直接返回
        if(isRecording){
            return;
        }
        //AudioRecord.getMinBufferSize的参数是否支持当前的硬件设备
        if (AudioRecord.ERROR_BAD_VALUE == mBufferSizeInBytes || AudioRecord.ERROR == mBufferSizeInBytes) {
            throw new RuntimeException("Unable to getMinBufferSize");
        }else{
            destroyThread();
            isRecording = true;
            if(mThread == null){
                mThread = new Thread(this);
                //开启线程
                mThread.start();
            }
        }
    }

    /**
     * 销毁线程方法
     */
    private void destroyThread() {
        try {
            isRecording = false;
            if (null != mThread && Thread.State.RUNNABLE == mThread.getState()) {
                try {
                    Thread.sleep(500);
                    mThread.interrupt();
                } catch (Exception e) {
                    mThread = null;
                }
            }
            mThread = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mThread = null;
        }
    }

    //停止录音
    public void stopRecord() {
        isRecording = false;
        //停止录音，回收AudioRecord对象，释放内存
        if (mAudioRecord != null) {
            if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {//初始化成功
                mAudioRecord.stop();
            }
            if (mAudioRecord  !=null ) {
                mAudioRecord.release();
            }
        }
    }

    @Override
    public void run() {

        //标记为开始采集状态
        isRecording = true;
        //创建一个流，存放从AudioRecord读取的数据
        //mRecordingFile = new File(mFileRoot,mFileName);

        mRecordingFile = new File(getExternalFilesDir(""), "audiorecordtest.pcm");

        Log.d(TAG,mRecordingFile.exists()+"");
        //音频文件保存过了删除


        if(mRecordingFile.exists()){
            boolean delete = mRecordingFile.delete();
            if(!delete){
                Log.d(TAG,"文件删除失败");
            }
        }


        try {
            boolean newFile = true;

            if(!mRecordingFile.exists()){
                //mRecordingFile = new File(mFileRoot,mFileName);
                mRecordingFile = new File(getExternalFilesDir(""), "audiorecordtest.pcm");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("lu","创建储存音频文件出错");
        }


        try {
            //获取到文件的数据流
            byte[] buffer = null;
            try {
                if(!mRecordingFile.exists()){
                    Log.d(TAG,"该文件不存在");
                }
                FileOutputStream fileOutputStream = new FileOutputStream(mRecordingFile);;

                BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
                mDataOutputStream = new DataOutputStream(bufferedOutputStream);
            }catch(Exception e){
                Log.d(TAG,"数据流创建出错");
                e.printStackTrace();
            }
            buffer = new byte[mBufferSizeInBytes];

            //判断AudioRecord未初始化，停止录音的时候释放了，状态就为STATE_UNINITIALIZED
            if(mAudioRecord.getState() == AudioRecord.STATE_UNINITIALIZED){
                Log.d(TAG,"STATE_UNINITIALIZED");
                initDatas();
            }

            mAudioRecord.startRecording();//开始录音
            //getRecordingState获取当前AudioRecording是否正在采集数据的状态

            while (isRecording && mAudioRecord.getRecordingState() == AudioRecord.RECORDSTATE_RECORDING) {
                int bufferReadResult = mAudioRecord.read(buffer,0,mBufferSizeInBytes);
                for (int i = 0; i < bufferReadResult; i++) {
                    mDataOutputStream.write(buffer[i]);
                }
            }
            mDataOutputStream.close();
        } catch (Throwable t) {
            Log.e("lu", "Recording Failed");
            stopRecord();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"被销毁了");
        destroyThread();
        stopRecord();
    }
}