package com.example.testproject;

import androidx.appcompat.app.AppCompatActivity;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.DataOutputStream;
import java.io.File;

public class AudioRecordTest_02 extends AppCompatActivity {

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

    private File mRecordingFile;//储存AudioRecord录下来的文件
    private boolean isRecording = false; //true表示正在录音
    private AudioRecord mAudioRecord=null;
    private File mFileRoot=null;//文件目录

    //存放的目录路径名称
    //private static final String mPathName = Environment.getExternalStorageDirectory().getAbsolutePath()+ "/AudioRecordFile";
    private static final String mPathName = "/AudioRecordFile";


    //保存的音频文件名
    private static final String mFileName = "AudioRecordTest.pcm";

    //缓冲区中数据写入到数据，因为需要使用IO操作，因此读取数据的过程应该在子线程中执行。
    private Thread mThread;
    private DataOutputStream mDataOutputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio_record_test02);

        mBtnStartRecord = findViewById(R.id.btn_start_record);
        mBtnStopRecord  = findViewById(R.id.btn_stop_record);

    }


    public void startRecord(View view) {
        mBufferSizeInBytes = AudioRecord.getMinBufferSize(mSampleRateInHz,mChannelConfig,mAudioFormat);
        mAudioRecord = new AudioRecord(mAudioSource,mSampleRateInHz,mChannelConfig,mAudioFormat,mBufferSizeInBytes);
        mFileRoot = new File(mPathName);
        //mFileRoot = new File(getExternalFilesDir(""));
        if(!mFileRoot.exists()) {
            //创建文件夹
            mFileRoot.mkdirs();
        }
        Log.d(TAG,mFileRoot.getAbsolutePath());
    }

    public void stopRecord(View view) {

    }
}